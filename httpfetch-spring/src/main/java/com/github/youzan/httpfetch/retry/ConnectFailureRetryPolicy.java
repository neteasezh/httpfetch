package com.github.youzan.httpfetch.retry;

import com.github.youzan.httpfetch.http.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class ConnectFailureRetryPolicy implements RetryPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectFailureRetryPolicy.class);

    /**
     * 如果是网络异常则重试
     * @param result http请求结果
     * @param retryTimes 重试次数
     * @param remainRetryTimes  剩余重试次数
     * @return
     */
    @Override
    public boolean needRetry(HttpResult result, int retryTimes, int remainRetryTimes) {
        Exception e = result.getException();
        if(e instanceof SocketTimeoutException || e instanceof ConnectException){
            LOGGER.info("超时重试: {}, 重试次数: {} 剩余次数: {}", e, retryTimes, remainRetryTimes);
            return true;
        }
        return false;
    }

}
