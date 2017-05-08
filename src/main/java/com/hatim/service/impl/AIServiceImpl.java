package com.hatim.service.impl;

import com.hatim.common.constant.Keys;
import com.hatim.common.constant.enu.ReplyType;
import com.hatim.service.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Hatim on 2017/4/24.
 */
@Service
public class AIServiceImpl implements AIService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(AIServiceImpl.class);

    @Value("${itpk.api}")
    private String ITPK_API;
    @Value("${itpk.key}")
    private String ITPK_KEY;
    @Value("${itpk.secret}")
    private String ITPK_SECRET;

    @Autowired
    RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        return builder.build();
    }


    /**
     * 消息匹配
     *
     * @param msg 用户发送的消息
     * @return
     */
    @Override
    public ReplyType msgMatching(String msg) {
        if (msg.contains(Keys.CHAT)) {
            // 唠嗑
            return ReplyType.ITPK_AI;
        }
        if (msg.contains(Keys.FIND_GOODS)) {
            // 查找商品
            return ReplyType.FIND_GOODS;
        }
        if (msg.contains(Keys.TRANSFER_CHAIN)) {
            // 转链
            return ReplyType.TRANSFER_CHAIN;
        }
        return ReplyType.BAIDU_AI;
    }

    /**
     * 转链服务
     *
     * @param msg
     * @return
     */
    @Override
    public String transferChain(String msg) {
        return null;
    }

    /**
     * 查找商品服务
     *
     * @param msg
     * @return
     */
    @Override
    public String findGoods(String msg) {
        return null;
    }

    /**
     * 图灵机器人
     *
     * @param msg 用户发送的消息
     * @return
     */
    @Override
    public String talkToTuring(String msg) {
        return null;
    }

    /**
     * 百度机器人
     *
     * @param msg 用户发送的消息
     * @return
     */
    @Override
    public String talkToBaidu(String msg) {
        return null;
    }

    /**
     * 茉莉机器人
     *
     * @param msg 用户发送的消息
     * @return
     */
    @Override
    public String talkToItpk(String msg) {
        String[] array = msg.split(Keys.CHAT);
        if (array.length > 1) {
            msg = array[1].trim();
        }
        String url = ITPK_API + "?api_key=" + ITPK_KEY + "&limit=8&api_secret=" + ITPK_SECRET + "&question=" + msg;
        String s = restTemplate.getForObject(url, String.class);
        return s;
    }

}
