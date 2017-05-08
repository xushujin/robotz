package com.hatim.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 日志
 * Created by Hatim on 2017/4/28.
 */
@Document(collection = "t_logs")
public class Log implements Serializable {
    @Id
    private ObjectId id;
    // 会员账号（唯一）
    private String account;
    // 发送的消息
    private String msg;
    // 创建时间
    private Date createDate;

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", msg='" + msg + '\'' +
                ", createDate=" + createDate +
                '}';
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
