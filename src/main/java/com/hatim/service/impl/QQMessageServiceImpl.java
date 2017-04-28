package com.hatim.service.impl;

import com.hatim.common.constant.Global;
import com.hatim.common.utils.DateUtil;
import com.hatim.common.utils.QQInfoUtil;
import com.hatim.common.utils.QQMsgUtil;
import com.hatim.model.DiscussMessageModel;
import com.hatim.model.GroupMessageModel;
import com.hatim.model.MessageModel;
import com.hatim.service.QQMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Hatim on 2017/4/27.
 */
public class QQMessageServiceImpl implements QQMessageService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(QQMessageServiceImpl.class);

    /**
     * 收到私聊消息后的回调
     *
     * @param message
     */
    @Override
    public void onMessage(MessageModel message) {
        if (message.getContent().contains(Global.CONTAINS_KEY)) {
            QQMsgUtil.sendMessageToFriend(message.getUserId(), QQInfoUtil.getFriendNick(message) + " 大爷你好！");
        }
        logger.info(DateUtil.getTime() + " [ 私聊 ] {}:{}", QQInfoUtil.getFriendNick(message), message.getContent());
    }

    /**
     * 收到群消息后的回调
     *
     * @param message
     */
    @Override
    public void onGroupMessage(GroupMessageModel message) {
        if (message.getContent().contains(Global.CONTAINS_KEY)) {
            QQMsgUtil.sendMessageToGroup(message.getGroupId(), QQInfoUtil.getGroupUserNick(message) + " 大爷你好！");
        }
        logger.info(DateUtil.getTime() + " [ 群聊:{} ] {}:{}", QQInfoUtil.getGroupName(message), QQInfoUtil.getGroupUserNick(message), message.getContent());
    }

    /**
     * 收到讨论组消息后的回调
     *
     * @param message
     */
    @Override
    public void onDiscussMessage(DiscussMessageModel message) {
        if (message.getContent().contains(Global.CONTAINS_KEY)) {
            QQMsgUtil.sendMessageToDiscuss(message.getDiscussId(), QQInfoUtil.getDiscussUserNick(message) + " 大爷你好！");
        }
        logger.info(DateUtil.getTime() + " [ 讨论组:{} ] {}:{}", QQInfoUtil.getDiscussName(message), QQInfoUtil.getDiscussUserNick(message), message.getContent());
    }
}
