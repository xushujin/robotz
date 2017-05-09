package com.hatim.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hatim on 2017/4/13.
 */
public class RestUtil {

    private static final Logger logger = LoggerFactory.getLogger(RestUtil.class);

    private final String url;

    private final Map<Object, Object> params = new HashMap<Object, Object>();

    public RestUtil set(String key, String value) {
        params.put(key, value);
        return this;
    }

    public RestUtil set(Object bean) {
        if (bean != null) {
            BeanMap beanMap = new BeanMap(bean);
            for (Object key : beanMap.keySet()) {
                if("class".equals(key)){
                    continue;
                }
                params.put(key, beanMap.get(key));
            }
        }
        return this;
    }

    /**
     * 构造方法,请求url.
     *
     * @param url 请求地址
     */
    public RestUtil(String url) {
        super();
        this.url = url;
    }


    /**
     * 发送/获取 服务端数据(主要用于解决发送put,delete方法无返回值问题).
     *
     * @param url      绝对地址
     * @param method   请求方式
     * @param bodyType 返回类型
     * @param <T>      返回类型
     * @return 返回结果(响应体)
     */
    public <T> T exchange(String url, HttpMethod method, Class<T> bodyType, RestTemplate restTemplate) {
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        MimeType mimeType = MimeTypeUtils.parseMimeType("application/json");
        MediaType mediaType =
            new MediaType(mimeType.getType(), mimeType.getSubtype(), Charset.forName("UTF-8"));
        // 请求体
        headers.setContentType(mediaType);
        //提供json转化功能
        ObjectMapper mapper = new ObjectMapper();
        String str = null;
        try {
            if (!params.isEmpty()) {
                str = mapper.writeValueAsString(params);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // 发送请求
        HttpEntity<String> entity = new HttpEntity<String>(str, headers);
        // TODO 404之类的接口请求错误是否需要抓取
        ResponseEntity<T> resultEntity = restTemplate.exchange(url, method, entity, bodyType);
        logger.info("url:{}, method:{}, statusCode:{}", url, method, resultEntity.getStatusCode());
        return resultEntity.getBody();
    }
}
