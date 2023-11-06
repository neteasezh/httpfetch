package com.github.youzan.httpfetch.bookworm.chains;


import com.github.youzan.httpfetch.chains.HttpApiChain;
import com.github.youzan.httpfetch.http.HttpApiInvoker;
import com.github.youzan.httpfetch.http.HttpApiRequestParam;
import com.github.youzan.httpfetch.http.HttpResult;
import com.github.youzan.httpfetch.http.Invocation;

/**
 * Created by daiqiang on 17/6/14.
 */
public class BookWormTokenChain implements HttpApiChain {
    @Override
    public HttpResult doChain(HttpApiInvoker invoker, Invocation invocation) {
        if(invocation.getWrapper().hasAnnotation(BookWormApi.class)){
            //填充token
            HttpApiRequestParam requestParam = invocation.getRequestParam();
            requestParam.addHeaders("Cookie", "token=XXXXXXX");
        }
        return invoker.invoke(invocation);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
