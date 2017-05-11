package com.hatim.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hatim.bo.SendMsgBo;
import com.hatim.bo.TuringBo;
import com.hatim.common.constant.Global;
import com.hatim.common.constant.enu.ReplyType;
import com.hatim.service.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

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

    @Value("${turing.api}")
    private String TURING_API;
    @Value("${turing.key}")
    private String TURING_KEY;

    @Value("${msg.key.chat}")
    private String msg_key_chat;
    @Value("${msg.key.transferChain}")
    private String msg_key_transferChain;
    @Value("${msg.key.findGoods}")
    private String msg_key_findGoods;

    @Autowired
    RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        return builder.build();
    }

    /**
     * 消息匹配，并回复处理
     *
     * @param msg 用户发送的消息
     * @return
     */
    @Override
    public SendMsgBo msgMatching(String msg) {
        if (msg.startsWith(msg_key_chat)) {
            // 唠嗑
            return new SendMsgBo().setReplyType(ReplyType.TURING_AI)
                    .setMsg(this.talkToTuring(msg.replaceFirst(msg_key_chat, Global.BLANK_STR).trim()));
        }
        if (msg.startsWith(msg_key_findGoods)) {
            // 查找商品
            return new SendMsgBo().setReplyType(ReplyType.FIND_GOODS)
                    .setMsg(this.findGoods(msg.replaceFirst(msg_key_findGoods, Global.BLANK_STR).trim()));
        }
        if (msg.startsWith(msg_key_transferChain)) {
            // 转链
            return new SendMsgBo().setReplyType(ReplyType.TRANSFER_CHAIN)
                    .setMsg(this.transferChain(msg.replaceFirst(msg_key_transferChain, Global.BLANK_STR).trim()));
        }
        return null;
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
        String[] array = msg.split(msg_key_chat);
        if (array.length > 1) {
            msg = array[1].trim();
        }
        String url = TURING_API + "?key=" + TURING_KEY + "&userid=123&info=" + msg;
        String str = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        TuringBo bo = null;
        try {
            bo = mapper.readValue(str, TuringBo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bo != null && !StringUtils.isEmpty(bo.getText())) {
            return bo.getText();
        }
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
        String[] array = msg.split(msg_key_chat);
        if (array.length > 1) {
            msg = array[1].trim();
        }
        String url = ITPK_API + "?api_key=" + ITPK_KEY + "&limit=8&api_secret=" + ITPK_SECRET + "&question=" + msg;
        String text = restTemplate.getForObject(url, String.class);
        return StringUtils.isEmpty(text) ? null : text;
    }

}
