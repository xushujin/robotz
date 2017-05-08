package com.hatim.service;

import com.hatim.bo.GroupMessageBo;
import com.hatim.bo.MessageBo;

/**
 * 收到QQ消息的回调
 * Created by Hatim on 2017/4/28.
 */
public interface WeChatMessageService {

    /**
     * 收到私聊消息后的回调
     *
     * @param message
     */
    void onMessage(MessageBo message);

    /**
     * 收到群消息后的回调
     *
     * @param message
     */
    void onGroupMessage(GroupMessageBo message);

}
