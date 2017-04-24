package com.hatim.service.impl;

import com.hatim.service.AIService;

/**
 * Created by Hatim on 2017/4/24.
 */
public class AIServiceImpl implements AIService {

    /**
     * 消息匹配
     *
     * @param msg 用户发送的消息
     * @return
     */
    @Override
    public boolean msgMatching(String msg) {
        return false;
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
        return null;
    }
}
