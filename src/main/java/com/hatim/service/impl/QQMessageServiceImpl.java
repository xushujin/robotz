package com.hatim.service.impl;

import com.hatim.bo.DiscussMessageBo;
import com.hatim.bo.GroupMessageBo;
import com.hatim.bo.MessageBo;
import com.hatim.common.constant.enu.ReplyType;
import com.hatim.common.utils.DateUtil;
import com.hatim.common.utils.QQInfoUtil;
import com.hatim.common.utils.QQMsgUtil;
import com.hatim.service.AIService;
import com.hatim.service.QQMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by Hatim on 2017/4/27.
 */
@Service
public class QQMessageServiceImpl implements QQMessageService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(QQMessageServiceImpl.class);

    @Autowired
    AIService aiService;

    /**
     * 收到私聊消息后的回调
     *
     * @param message
     */
    @Override
    public void onMessage(MessageBo message) {
        ReplyType replyType = aiService.msgMatching(message.getContent());
        if (!StringUtils.isEmpty(getSendMsg(replyType, message.getContent()))) {
            QQMsgUtil.sendMessageToFriend(message.getUserId(), QQInfoUtil.getFriendNick(message) + ":" + getSendMsg(replyType, message.getContent()));
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
        ReplyType replyType = aiService.msgMatching(message.getContent());
        if (!StringUtils.isEmpty(getSendMsg(replyType, message.getContent()))) {
            QQMsgUtil.sendMessageToGroup(message.getGroupId(), QQInfoUtil.getGroupUserNick(message) + ":" + getSendMsg(replyType, message.getContent()));
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
        ReplyType replyType = aiService.msgMatching(message.getContent());
        if (!StringUtils.isEmpty(getSendMsg(replyType, message.getContent()))) {
            QQMsgUtil.sendMessageToDiscuss(message.getDiscussId(), QQInfoUtil.getDiscussUserNick(message) + ":" + getSendMsg(replyType, message.getContent()));
        }
        logger.info(DateUtil.getTime() + " [ 讨论组:{} ] {}:{}", QQInfoUtil.getDiscussName(message), QQInfoUtil.getDiscussUserNick(message), message.getContent());
    }

    /**
     * 获取返回消息
     *
     * @param replyType
     * @param msg
     * @return
     */
    public String getSendMsg(ReplyType replyType, String msg) {
        if (ReplyType.FIND_GOODS.equals(replyType)) {
            return aiService.findGoods(msg);
        }
        if (ReplyType.TRANSFER_CHAIN.equals(replyType)) {
            return aiService.transferChain(msg);
        }
        if (ReplyType.ITPK_AI.equals(replyType)) {
            return aiService.talkToItpk(msg);
        }
        if (ReplyType.BAIDU_AI.equals(replyType)) {
            return aiService.talkToBaidu(msg);
        }
        if (ReplyType.TURING_AI.equals(replyType)) {
            return aiService.talkToTuring(msg);
        }
        return null;
    }
}
