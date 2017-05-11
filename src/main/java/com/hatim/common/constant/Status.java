package com.hatim.common.constant;

import com.hatim.bo.*;
import net.dongliu.requests.Client;
import net.dongliu.requests.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hatim on 2017/5/9.
 */
public class Status {
    // 好友列表
    public static List<FriendBo> friendList = new ArrayList<>();
    // 群列表
    public static List<GroupBo> groupList = new ArrayList<>();
    // 讨论组列表
    public static List<DiscussBo> discussList = new ArrayList<>();
    // 好友id到好友映射
    public static Map<Long, FriendBo> friendFromID = new HashMap<>();
    // 群id到群映射
    public static Map<Long, GroupBo> groupFromID = new HashMap<>();
    // 群id到群详情映射
    public static Map<Long, GroupInfoBo> groupInfoFromID = new HashMap<>();
    // 讨论组id到讨论组映射
    public static Map<Long, DiscussBo> discussFromID = new HashMap<>();
    // 讨论组id到讨论组详情映射
    public static Map<Long, DiscussInfoBo> discussInfoFromID = new HashMap<>();

    // 是否等待登录
    public static boolean isWaittingLogin = false;
    // 验证码图片路径
    public static String imgUrl = "";

    //客户端
    public static Client client;
    //会话
    public static Session session;

    //二维码令牌
    public static String qrsig;
    //鉴权参数
    public static String ptwebqq;
    public static String vfwebqq;
    public static long uin;
    public static String psessionid;
}
