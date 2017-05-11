package com.hatim.service;


import com.hatim.bo.DiscussMessageBo;
import com.hatim.bo.GroupMessageBo;
import com.hatim.bo.MessageBo;

/**
 * 收到消息后的回调
 * Created by Hatim on 2017/4/28.
 */
public interface MessageService {

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

    /**
     * 收到讨论组消息后的回调
     *
     * @param message
     */
    void onDiscussMessage(DiscussMessageBo message);
}
