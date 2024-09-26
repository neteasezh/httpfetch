package com.github.youzan.httpfetch.http;


import com.github.youzan.httpfetch.annotations.*;
import com.github.youzan.httpfetch.chains.HttpApiChain;
import com.github.youzan.httpfetch.convertor.ResponseGeneratorConvertor;
import com.github.youzan.httpfetch.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by daiqiang on 16/12/6.
 */
public class HttpApiService {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpApiService.class);
    private static final String ARG_PREFIX = "arg";

    private HttpApiConfiguration configuration;

    private HttpApiInvoker startInvoker = null;

    private Map<Class<?>, Object> serviceCache = new ConcurrentHashMap<>();

    private Map<Method, HttpApiMethodWrapper> methodsCache = new HashMap<>();

    public HttpApiService(HttpApiConfiguration configuration) {
        this.configuration = configuration;
    }


    public void init() {
        //创建调用链
        List<HttpApiChain> chains = configuration.getChains();

        //排序
        Collections.sort(chains, new Comparator<HttpApiChain>() {
            @Override
            public int compare(HttpApiChain o1, HttpApiChain o2) {
                int order1 = o1.getOrder();
                int order2 = o2.getOrder();
                return order2 > order1 ? 1 : -1;
            }
        });

        HttpApiInvoker last = null;
        for (int i = chains.size() - 1; i >= 0; i--) {
            final HttpApiChain chain = chains.get(i);
            final HttpApiInvoker next = last;
            last = new HttpApiInvoker() {
                @Override
                public HttpResult invoke(Invocation invocation) {
                    return chain.doChain(next, invocation);
                }
            };
        }
        startInvoker = last;
    }

    public <T> T getOrCreateService(Class<T> serviceCls) {
        Object service;
        if (!serviceCache.containsKey(serviceCls)) {
            try {
                service = createService(serviceCls);
            } catch (Exception e) {
                String msg = String.format("服务创建失败! serviceCls [" + serviceCls + "]");
                LOGGER.error(msg, e);
                throw new RuntimeException(msg);
            }
            serviceCache.put(serviceCls, service);
        } else {
            service = serviceCache.get(serviceCls);
        }
        return (T) service;
    }

    /**
     * 创建代理服务,对代理类和头参数等进行封装
     *
     * @param serviceCls 服务接口类
     * @return
     */
    private Object createService(final Class<?> serviceCls) {
        if (serviceCls.isInterface()) {
            return Proxy.newProxyInstance(
                    serviceCls.getClassLoader(),
                    new Class[]{serviceCls},
                    new HttpApiMethodProxy(serviceCls));
        } else {
            String msg = String.format("类[%s]不是接口!", serviceCls);
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    class HttpApiMethodProxy implements InvocationHandler {
        private Class<?> serviceCls;

        public HttpApiMethodProxy(Class<?> serviceCls) {
            this.serviceCls = serviceCls;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            HttpApi httpApiAnno = method.getAnnotation(HttpApi.class);
            if (httpApiAnno == null) {
                //没有httpApi注解
                throw new RuntimeException("系统异常,httpapi注解不存在!");
            }

            Invocation invocation = new Invocation();
            HttpApiMethodWrapper wrapper = getOrNewWrapper(method, httpApiAnno);
            invocation.setWrapper(wrapper);

            this.wrapRequestParameter(invocation, wrapper, args);

            //设置请求参数
            HttpApiRequestParam requestParam = new HttpApiRequestParam();
            requestParam.setEncoding(wrapper.getEncoding());
            requestParam.addHeaders(wrapper.getHeaders());
            invocation.setRequestParam(requestParam);

            invocation.setMethod(method);
            invocation.setServiceCls(serviceCls);

            HttpResult httpResult = startInvoker.invoke(invocation);

            return httpResult.getData();
        }

        private void wrapRequestParameter(Invocation invocation, HttpApiMethodWrapper wrapper, Object[] args) {
            //封装参数
            if (!CommonUtils.isArrayEmpty(wrapper.getParameters())) {
                for (int i = 0; i < wrapper.getParameters().length; i++) {
                    ParameterWrapper parameterWrapper = wrapper.getParameters()[i];
                    invocation.addParameters(new RequestParameter(parameterWrapper, args[i]));
                }
            }
        }

        public HttpApiMethodWrapper getOrNewWrapper(Method method, HttpApi httpApiAnno) {
            HttpApiMethodWrapper wrapper;
            if (methodsCache.containsKey(method)) {
                wrapper = methodsCache.get(method);
            } else {
                wrapper = new HttpApiMethodWrapper();
                wrapper.setMethod(httpApiAnno.method());
                wrapper.setReturnCls(method.getReturnType());
                wrapper.setAnnotations(method.getAnnotations());
                wrapper.setEncoding(httpApiAnno.encoding());

                //获取参数名称
                ParameterWrapper[] parameters = wrapperParameters(method);
                wrapper.setParameters(parameters);

                //获取头参数
                Map<String, String> headers = getHeaders(httpApiAnno);
                wrapper.setHeaders(headers);

                //查询指定的结果转换类获取结果转换服务类
                ResponseGeneratorConvertor generatorService = null;
                if (!CommonUtils.isStringEmpty(httpApiAnno.generator())) {
                    for (ResponseGeneratorConvertor handler : configuration.getConvertors()) {
                        if (httpApiAnno instanceof ResponseGeneratorConvertor) {
                            generatorService = handler;
                            break;
                        }
                    }
                }

                wrapper.setGeneratorService(generatorService);

                //去函数的返回类
                wrapper.setResponseCls(method.getReturnType());

                if (httpApiAnno.timeout() > 0) {
                    wrapper.setTimeout(httpApiAnno.timeout());
                }
                if (httpApiAnno.readTimeout() > 0) {
                    wrapper.setReadTimeout(httpApiAnno.readTimeout());
                }

                //重试次数
                wrapper.setRetry(httpApiAnno.retry());
                //获取重试判断类
                wrapper.setRetryPolicyClazz(httpApiAnno.retryPolicy());
                //使用jsonpath解析返回结果
                wrapper.setJsonPath(httpApiAnno.jsonPath());
                methodsCache.put(method, wrapper);
            }
            return wrapper;
        }


        /**
         * 获取请求参数
         *
         * @param method
         * @return
         */
        private ParameterWrapper[] wrapperParameters(Method method) {

            Parameter[] parameters = method.getParameters();
            if (parameters == null || parameters.length == 0) {
                return new ParameterWrapper[0];
            }
            String[] paramNames = new String[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Annotation[] annotations = parameter.getAnnotations();
                if (annotations != null && annotations.length != 0) {
                    for (Annotation annotation : annotations) {
                        if (QueryParam.class.isAssignableFrom(annotation.annotationType())) {
                            paramNames[i] = ((QueryParam) annotation).value();
                            break;
                        }

                        if (FormParam.class.isAssignableFrom(annotation.annotationType())) {
                            paramNames[i] = ((FormParam) annotation).value();
                            break;
                        }

                        if (PostParam.class.isAssignableFrom(annotation.annotationType())) {
                            paramNames[i] = ((PostParam) annotation).value();
                            break;
                        }
                    }
                } else {
                    if (parameter.getName().startsWith(ARG_PREFIX)) {
                        paramNames[i] = parameter.getName().replace(ARG_PREFIX, "");
                    }
                }
            }

            ParameterWrapper[] parameterWrappers = new ParameterWrapper[paramNames.length];
            Annotation[][] annotationArray = method.getParameterAnnotations();

            for (int i = 0; i < paramNames.length; ++i) {
                parameterWrappers[i] = new ParameterWrapper(method.getParameterTypes()[i], paramNames[i], method.getGenericParameterTypes()[i], annotationArray[i]);
            }

            return parameterWrappers;
        }

        /**
         * 取注解上面的header
         *
         * @param anno
         * @return
         */
        private Map<String, String> getHeaders(HttpApi anno) {
            Map<String, String> headers = new HashMap<>();
            Header[] headersAnno = anno.headers();
            if (!CommonUtils.isArrayEmpty(headersAnno)) {
                for (Header header : headersAnno) {
                    if (header != null) {
                        headers.put(header.key(), header.value());
                    }
                }
            }
            return headers;
        }

    }

}
