package com.hatim.bo;

import com.hatim.common.constant.enu.ReplyType;

/**
 * Created by Hatim on 2017/5/9.
 */
public class SendMsgBo {

    private ReplyType replyType;
    private String msg;

    @Override
    public String toString() {
        return "SendMsgBo{" +
                "replyType=" + replyType +
                ", msg='" + msg + '\'' +
                '}';
    }

    public ReplyType getReplyType() {
        return replyType;
    }

    public SendMsgBo setReplyType(ReplyType replyType) {
        this.replyType = replyType;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public SendMsgBo setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
