package com.hatim.service;


import com.hatim.model.DiscussMessageModel;
import com.hatim.model.GroupMessageModel;
import com.hatim.model.MessageModel;

/**
 * 收到QQ消息的回调
 * Created by Hatim on 2017/4/28.
 */
public interface QQMessageService {

    /**
     * 收到私聊消息后的回调
     *
     * @param message
     */
    void onMessage(MessageModel message);

    /**
     * 收到群消息后的回调
     *
     * @param message
     */
    void onGroupMessage(GroupMessageModel message);

    /**
     * 收到讨论组消息后的回调
     *
     * @param message
     */
    void onDiscussMessage(DiscussMessageModel message);
}
