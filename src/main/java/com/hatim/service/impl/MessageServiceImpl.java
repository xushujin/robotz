package com.hatim.service.impl;

import com.hatim.bo.DiscussMessageBo;
import com.hatim.bo.GroupMessageBo;
import com.hatim.bo.MessageBo;
import com.hatim.bo.SendMsgBo;
import com.hatim.common.utils.DateUtil;
import com.hatim.common.utils.QQInfoUtil;
import com.hatim.common.utils.QQMsgUtil;
import com.hatim.service.AIService;
import com.hatim.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by Hatim on 2017/4/27.
 */
@Service
public class MessageServiceImpl implements MessageService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    AIService aiService;

    /**
     * 收到私聊消息后的回调
     *
     * @param message
     */
    @Override
    public void onMessage(MessageBo message) {
        SendMsgBo bo = aiService.msgMatching(message.getContent());
        if (bo != null && !StringUtils.isEmpty(bo.getMsg())) {
            QQMsgUtil.sendMessageToFriend(message.getUserId(), QQInfoUtil.getFriendNick(message) + ":" + bo.getMsg());
        }
        logger.info(DateUtil.getTime() + " [ 私聊 ] {}:{}", QQInfoUtil.getFriendNick(message), message.getContent());
    }

    /**
     * 收到群消息后的回调
     *
     * @param message
     */
    @Override
    public void onGroupMessage(GroupMessageBo message) {
        SendMsgBo bo = aiService.msgMatching(message.getContent());
        if (bo != null && !StringUtils.isEmpty(bo.getMsg())) {
            QQMsgUtil.sendMessageToGroup(message.getGroupId(), QQInfoUtil.getGroupUserNick(message) + ":" + bo.getMsg());
        }
        logger.info(DateUtil.getTime() + " [ 群聊:{} ] {}:{}", QQInfoUtil.getGroupName(message), QQInfoUtil.getGroupUserNick(message), message.getContent());
    }

    /**
     * 收到讨论组消息后的回调
     *
     * @param message
     */
    @Override
    public void onDiscussMessage(DiscussMessageBo message) {
        SendMsgBo bo = aiService.msgMatching(message.getContent());
        if (bo != null && !StringUtils.isEmpty(bo.getMsg())) {
            QQMsgUtil.sendMessageToDiscuss(message.getDiscussId(), QQInfoUtil.getDiscussUserNick(message) + ":" + bo.getMsg());
        }
        logger.info(DateUtil.getTime() + " [ 讨论组:{} ] {}:{}", QQInfoUtil.getDiscussName(message), QQInfoUtil.getDiscussUserNick(message), message.getContent());
    }

}
