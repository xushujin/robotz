package com.hatim.service;

import com.hatim.common.constant.enu.ReplyType;

/**
 * 傻逼AI
 * Created by Hatim on 2017/4/24.
 */
public interface AIService {

    /**
     * 消息匹配，判断是否需要做回复处理
     *
     * @param msg 用户发送的消息
     * @return
     */
    ReplyType msgMatching(String msg);

    /**
     * 转链服务
     *
     * @param msg
     * @return
     */
    String transferChain(String msg);

    /**
     * 查找商品服务
     *
     * @param msg
     * @return
     */
    String findGoods(String msg);

    /**
     * 图灵机器人
     *
     * @param msg 用户发送的消息
     * @return
     */
    String talkToTuring(String msg);

    /**
     * 百度机器人
     *
     * @param msg 用户发送的消息
     * @return
     */
    String talkToBaidu(String msg);

    /**
     * 茉莉机器人
     *
     * @param msg 用户发送的消息
     * @return
     */
    String talkToItpk(String msg);
}
