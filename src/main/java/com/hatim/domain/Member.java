package com.hatim.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 会员信息
 * Created by Hatim on 2017/5/6.
 */
@Document(collection = "t_members")
public class Member implements Serializable {
    @Id
    private ObjectId id;
    // 会员账号（唯一）
    private String account;
    // 会员名称
    private String name;
    // 会员积分
    private String point;
    // 会员余额
    private String money;
    // VIP登记
    private int vip;

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", point='" + point + '\'' +
                ", money='" + money + '\'' +
                ", vip=" + vip +
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }
}
