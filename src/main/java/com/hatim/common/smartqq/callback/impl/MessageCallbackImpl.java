package com.hatim.common.smartqq.callback.impl;

import com.hatim.common.smartqq.callback.MessageCallback;
import com.hatim.common.smartqq.model.DiscussMessage;
import com.hatim.common.smartqq.model.GroupMessage;
import com.hatim.common.smartqq.model.Message;

/**
 * Created by Hatim on 2017/4/27.
 */
public class MessageCallbackImpl implements MessageCallback {
    /**
     * 收到私聊消息后的回调
     *
     * @param message
     */
    @Override
    public void onMessage(Message message) {

    }

    /**
     * 收到群消息后的回调
     *
     * @param message
     */
    @Override
    public void onGroupMessage(GroupMessage message) {

    }

    /**
     * 收到讨论组消息后的回调
     *
     * @param message
     */
    @Override
    public void onDiscussMessage(DiscussMessage message) {

    }
}
