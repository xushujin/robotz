package com.hatim.common.constant;

import com.hatim.bo.*;
import net.dongliu.requests.Client;
import net.dongliu.requests.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hatim on 2017/4/25.
 */
public class Global {
    // 客户端id，固定的
    public static final long Client_ID = 53999199;
    // 消息id，这个好像可以随便设置，所以设成全局的
    public static long MESSAGE_ID = 43690001;
    // 消息发送失败重发次数
    public static final long RETRY_TIMES = 5;
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
}
