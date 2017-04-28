package com.hatim.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.hatim.common.constant.Global;
import com.hatim.common.constant.enu.ApiURL;
import net.dongliu.requests.HeadOnlyRequestBuilder;
import net.dongliu.requests.Response;
import net.dongliu.requests.Session;

/**
 * Created by Hatim on 2017/4/26.
 */
public class HttpUtil {
    /**
     * 发送get请求
     *
     * @param url
     * @param session
     * @param params
     * @return
     */
    public static Response<String> get(ApiURL url, Session session, Object... params) {
        HeadOnlyRequestBuilder request = session.get(url.buildUrl(params))
                .addHeader("User-Agent", ApiURL.USER_AGENT);
        if (url.getReferer() != null) {
            request.addHeader("Referer", url.getReferer());
        }
        return request.text();
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param r
     * @param session
     * @return
     */
    public static Response<String> post(ApiURL url, JSONObject r, Session session) {
        return session.post(url.getUrl())
                .addHeader("User-Agent", ApiURL.USER_AGENT)
                .addHeader("Referer", url.getReferer())
                .addHeader("Origin", url.getOrigin())
                .addForm("r", r.toJSONString())
                .text();
    }

    /**
     * 发送post请求，失败时重试
     *
     * @param url
     * @param r
     * @param session
     * @return
     */
    public static Response<String> postWithRetry(ApiURL url, JSONObject r, Session session) {
        int times = 0;
        Response<String> response;
        do {
            response = post(url, r, session);
            times++;
        } while (times < Global.RETRY_TIMES && response.getStatusCode() != 200);
        return response;
    }
}
