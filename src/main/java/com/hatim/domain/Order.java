package com.hatim.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 订单绑定信息
 * Created by Hatim on 2017/5/6.
 */
@Document(collection = "t_order")
public class Order {
    @Id
    private ObjectId id;
    // 会员账号
    private String account;
    // 订单号
    private String orderNo;
    // 创建时间
    private Date createDate;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", orderNo='" + orderNo + '\'' +
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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
