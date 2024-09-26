package com.github.youzan.httpfetch.bookworm.api;

import com.github.youzan.httpfetch.annotations.BeanParam;
import com.github.youzan.httpfetch.annotations.FormParam;
import com.github.youzan.httpfetch.annotations.HttpApi;
import com.github.youzan.httpfetch.annotations.QueryParam;
import com.github.youzan.httpfetch.bookworm.chains.BookWormApi;
import com.github.youzan.httpfetch.bookworm.vo.UploadFileRequestVo;
import com.github.youzan.httpfetch.bookworm.vo.UploadFileResponseVo;
import java.io.File;
import java.net.URL;

/**
 * Created by daiqiang on 17/6/14.
 */
public interface BookWormHttpApi {

    /**
     *
     * 新增一个 拦截的chain  用于增加特定请求的token
     * @see[BookWormTokenChain]
     * @param file
     * @param name
     * @param nValue
     * @return
     */
    @HttpApi(timeout = 2000, url = "http://bookworm365.com/uploadImage")
    @BookWormApi
    UploadFileResponseVo uploadFile(@FormParam("file") File file,
                                    @QueryParam("name") String name,
                                    @QueryParam("n_value") String nValue);

    /**
     *
     * 新增一个 拦截的chain  用于增加特定请求的token
     * @see[BookWormTokenChain]
     * @param url
     * @param name
     * @param nValue
     * @return
     */
    @HttpApi(timeout = 2000, url = "http://bookworm365.com/uploadImage")
    @BookWormApi
    UploadFileResponseVo uploadFile(@FormParam("file") URL url,
                                    @QueryParam("name") String name,
                                    @QueryParam("n_value") String nValue);

    @HttpApi(timeout = 2000, url = "http://bookworm365.com/uploadImage")
    @BookWormApi
    UploadFileResponseVo uploadFile(@BeanParam @FormParam UploadFileRequestVo requestVo);

    @HttpApi(timeout = 200, url = "http://bookworm365.com/checkHeader")
    @BookWormApi
    String checkHeader();

    @HttpApi(timeout = 35, url = "http://bookworm365.com/checkHeader", retry = 10)
    @BookWormApi
    String checkRetryPolicy();

}
