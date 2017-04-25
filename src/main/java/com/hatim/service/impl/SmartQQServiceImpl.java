package com.hatim.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hatim.service.SmartQQService;
import com.hatim.smartqq.callback.MessageCallback;
import com.hatim.smartqq.constant.ApiURL;
import com.hatim.smartqq.model.*;
import net.dongliu.requests.Client;
import net.dongliu.requests.HeadOnlyRequestBuilder;
import net.dongliu.requests.Response;
import net.dongliu.requests.Session;
import net.dongliu.requests.exception.RequestException;
import net.dongliu.requests.struct.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Thread.sleep;

/**
 * Created by Hatim on 2017/4/22.
 */
@Service
public class SmartQQServiceImpl implements SmartQQService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(SmartQQServiceImpl.class);

    //客户端
    private static Client client;

    //会话
    private Session session;
    //二维码令牌
    private String qrsig;

    //鉴权参数
    private String ptwebqq;
    private String vfwebqq;
    private long uin;
    private String psessionid;

    //客户端id，固定的
    private static final long Client_ID = 53999199;

    //消息id，这个好像可以随便设置，所以设成全局的
    private static long MESSAGE_ID = 43690001;

    /**
     * SmartQQ客户端
     */
    private static boolean working = true;
    private static final String CONTAINS_KEY = "黑子";

    //消息发送失败重发次数
    private static final long RETRY_TIMES = 5;

    private static List<Friend> friendList = new ArrayList<>();                 //好友列表
    private static List<Group> groupList = new ArrayList<>();                   //群列表
    private static List<Discuss> discussList = new ArrayList<>();               //讨论组列表
    private static Map<Long, Friend> friendFromID = new HashMap<>();            //好友id到好友映射
    private static Map<Long, Group> groupFromID = new HashMap<>();              //群id到群映射
    private static Map<Long, GroupInfo> groupInfoFromID = new HashMap<>();      //群id到群详情映射
    private static Map<Long, Discuss> discussFromID = new HashMap<>();          //讨论组id到讨论组映射
    private static Map<Long, DiscussInfo> discussInfoFromID = new HashMap<>();  //讨论组id到讨论组详情映射

    /**
     * 开启服务
     *
     * @return
     */
    @Override
    public void startService() {
    }

    /**
     * 关闭服务
     *
     * @return
     */
    @Override
    public boolean stopService() {
        return false;
    }

    /**
     * 获取二维码
     *
     * @return
     */
    @Override
    public String getQRCode() {
        logger.debug("开始获取二维码");
        this.client = Client.pooled().maxPerRoute(5).maxTotal(10).build();
        this.session = client.session();

        //本地存储二维码图片
        String filePath;
        try {
            filePath = new File("src\\main\\resources\\static\\images\\qrcode.png").getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException("二维码保存失败");
        }
        Response response = session.get(ApiURL.GET_QR_CODE.getUrl())
                .addHeader("User-Agent", ApiURL.USER_AGENT)
                .file(filePath);
        for (Cookie cookie : response.getCookies()) {
            if (Objects.equals(cookie.getName(), "qrsig")) {
                qrsig = cookie.getValue();
                break;
            }
        }
        logger.info("二维码已保存在 " + filePath + " 文件中，请打开手机QQ并扫描二维码");
        return filePath;
    }

    /**
     * 校验二维码
     *
     * @return
     */
    @Async
    @Override
    public void verifyQRCode() {
        logger.info("等待扫描二维码");
        boolean flag = true;
        //阻塞直到确认二维码认证成功
        while (flag) {
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Response<String> response = get(ApiURL.VERIFY_QR_CODE, hash33(qrsig));
            String result = response.getBody();
            logger.info("result:{}", result);
            if (result.contains("成功")) {
                for (String content : result.split("','")) {
                    if (content.startsWith("http")) {
                        logger.info("正在登录，请稍后");

                        //登录流程3：获取ptwebqq
                        getPtwebqq(content);
                        //登录流程4：获取vfwebqq
                        getVfwebqq();
                        //登录流程5：获取uin和psessionid
                        getUinAndPsessionid();
                        //获取朋友列表
                        getFriendStatus(); //修复Api返回码[103]的问题
                        //登录成功欢迎语
                        UserInfo userInfo = getAccountInfo();
                        logger.info(userInfo.getNick() + "，欢迎！");

                        friendList = getFriendList();                //获取好友列表
                        groupList = getGroupList();                  //获取群列表
                        discussList = getDiscussList();              //获取讨论组列表
                        for (Friend friend : friendList) {                  //建立好友id到好友映射
                            friendFromID.put(friend.getUserId(), friend);
                        }
                        for (Group group : groupList) {                     //建立群id到群映射
                            groupFromID.put(group.getId(), group);
                        }
                        for (Discuss discuss : discussList) {               //建立讨论组id到讨论组映射
                            discussFromID.put(discuss.getId(), discuss);
                        }

                        getclient();

                        flag = false;
                    }
                }
            } else if (result.contains("已失效")) {
                logger.info("二维码已失效，尝试重新获取二维码");
                getQRCode();
            }
        }
    }

    //登录流程3：获取ptwebqq
    private void getPtwebqq(String url) {
        logger.debug("开始获取ptwebqq");

        Response<String> response = get(ApiURL.GET_PTWEBQQ, url);
        this.ptwebqq = response.getCookies().get("ptwebqq").iterator().next().getValue();
    }

    //登录流程4：获取vfwebqq
    private void getVfwebqq() {
        logger.debug("开始获取vfwebqq");

        Response<String> response = get(ApiURL.GET_VFWEBQQ, ptwebqq);
        this.vfwebqq = getJsonObjectResult(response).getString("vfwebqq");
    }

    //登录流程5：获取uin和psessionid
    private void getUinAndPsessionid() {
        logger.debug("开始获取uin和psessionid");

        JSONObject r = new JSONObject();
        r.put("ptwebqq", ptwebqq);
        r.put("clientid", Client_ID);
        r.put("psessionid", "");
        r.put("status", "online");

        Response<String> response = post(ApiURL.GET_UIN_AND_PSESSIONID, r);
        JSONObject result = getJsonObjectResult(response);
        this.psessionid = result.getString("psessionid");
        this.uin = result.getLongValue("uin");
    }

    /**
     * 获得讨论组列表
     *
     * @return
     */
    public List<Discuss> getDiscussList() {
        logger.debug("开始获取讨论组列表");

        Response<String> response = get(ApiURL.GET_DISCUSS_LIST, psessionid, vfwebqq);
        return JSON.parseArray(getJsonObjectResult(response).getJSONArray("dnamelist").toJSONString(), Discuss.class);
    }

    /**
     * 获取群列表
     *
     * @return
     */
    public List<Group> getGroupList() {
        logger.debug("开始获取群列表");

        JSONObject r = new JSONObject();
        r.put("vfwebqq", vfwebqq);
        r.put("hash", hash());

        Response<String> response = post(ApiURL.GET_GROUP_LIST, r);
        JSONObject result = getJsonObjectResult(response);
        return JSON.parseArray(result.getJSONArray("gnamelist").toJSONString(), Group.class);
    }

    /**
     * 获取好友列表
     *
     * @return
     */
    public List<Friend> getFriendList() {
        logger.debug("开始获取好友列表");

        JSONObject r = new JSONObject();
        r.put("vfwebqq", vfwebqq);
        r.put("hash", hash());

        Response<String> response = post(ApiURL.GET_FRIEND_LIST, r);
        return new ArrayList<>(parseFriendMap(getJsonObjectResult(response)).values());
    }

    //将json解析为好友列表
    private static Map<Long, Friend> parseFriendMap(JSONObject result) {
        Map<Long, Friend> friendMap = new HashMap<>();
        JSONArray info = result.getJSONArray("info");
        for (int i = 0; info != null && i < info.size(); i++) {
            JSONObject item = info.getJSONObject(i);
            Friend friend = new Friend();
            friend.setUserId(item.getLongValue("uin"));
            friend.setNickname(item.getString("nick"));
            friendMap.put(friend.getUserId(), friend);
        }
        JSONArray marknames = result.getJSONArray("marknames");
        for (int i = 0; marknames != null && i < marknames.size(); i++) {
            JSONObject item = marknames.getJSONObject(i);
            friendMap.get(item.getLongValue("uin")).setMarkname(item.getString("markname"));
        }
        JSONArray vipinfo = result.getJSONArray("vipinfo");
        for (int i = 0; vipinfo != null && i < vipinfo.size(); i++) {
            JSONObject item = vipinfo.getJSONObject(i);
            Friend friend = friendMap.get(item.getLongValue("u"));
            friend.setVip(item.getIntValue("is_vip") == 1);
            friend.setVipLevel(item.getIntValue("vip_level"));
        }
        return friendMap;
    }

    //hash加密方法
    private String hash() {
        return hash(uin, ptwebqq);
    }

    //hash加密方法
    private static String hash(long x, String K) {
        int[] N = new int[4];
        for (int T = 0; T < K.length(); T++) {
            N[T % 4] ^= K.charAt(T);
        }
        String[] U = {"EC", "OK"};
        long[] V = new long[4];
        V[0] = x >> 24 & 255 ^ U[0].charAt(0);
        V[1] = x >> 16 & 255 ^ U[0].charAt(1);
        V[2] = x >> 8 & 255 ^ U[1].charAt(0);
        V[3] = x & 255 ^ U[1].charAt(1);

        long[] U1 = new long[8];

        for (int T = 0; T < 8; T++) {
            U1[T] = T % 2 == 0 ? N[T >> 1] : V[T >> 1];
        }

        String[] N1 = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String V1 = "";
        for (long aU1 : U1) {
            V1 += N1[(int) ((aU1 >> 4) & 15)];
            V1 += N1[(int) (aU1 & 15)];
        }
        return V1;
    }

    /**
     * 获得登录状态
     *
     * @return
     */
    public List<FriendStatus> getFriendStatus() {
        logger.debug("开始获取好友状态");

        Response<String> response = get(ApiURL.GET_FRIEND_STATUS, vfwebqq, psessionid);
        return JSON.parseArray(getJsonArrayResult(response).toJSONString(), FriendStatus.class);
    }

    /**
     * 获得当前登录用户的详细信息
     *
     * @return
     */
    public UserInfo getAccountInfo() {
        logger.debug("开始获取登录用户信息");

        Response<String> response = get(ApiURL.GET_ACCOUNT_INFO);
        return JSON.parseObject(getJsonObjectResult(response).toJSONString(), UserInfo.class);
    }

    MessageCallback myCallback = new MessageCallback() {
        @Override
        public void onMessage(Message message) {
            System.out.println("onMessage");
            if (message.getContent().contains(CONTAINS_KEY)) {
                sendMessageToFriend(message.getUserId(), getFriendNick(message) + ",大爷你好！");
            }
            System.out.println("[" + getTime() + "] [私聊] " + getFriendNick(message) + "：" + message.getContent());
        }

        @Override
        public void onGroupMessage(GroupMessage message) {
            System.out.println("onGroupMessage");
            if (message.getContent().contains(CONTAINS_KEY)) {
                sendMessageToGroup(message.getGroupId(), getGroupUserNick(message) + " 大爷你好！");
            }
            System.out.println("[" + getTime() + "] [" + getGroupName(message) + "] " + getGroupUserNick(message) + "：" + message.getContent());
        }

        @Override
        public void onDiscussMessage(DiscussMessage message) {
            System.out.println("onDiscussMessage");
            if (message.getContent().contains(CONTAINS_KEY)) {
                sendMessageToDiscuss(message.getDiscussId(), getDiscussUserNick(message) + " 大爷你好！");
            }
            System.out.println("[" + getTime() + "] [" + getDiscussName(message) + "] " + getDiscussUserNick(message) + "：" + message.getContent());
        }
    };

    public void getclient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        pollMessage(myCallback);
                    } catch (RequestException e) {
                        //忽略SocketTimeoutException
                        if (!(e.getCause() instanceof SocketTimeoutException)) {
                            logger.error(e.getMessage());
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }).start();
    }

    /**
     * 获取讨论组消息所在讨论组名称
     *
     * @param msg 被查询的讨论组消息
     * @return 该消息所在讨论组名称
     */
    private static String getDiscussName(DiscussMessage msg) {
        return getDiscuss(msg).getName();
    }
    /**
     * 获取讨论组消息所在讨论组
     *
     * @param msg 被查询的讨论组消息
     * @return 该消息所在讨论组
     */
    private static Discuss getDiscuss(DiscussMessage msg) {
        return discussFromID.get(msg.getDiscussId());
    }

    /**
     * 获取群消息所在群名称
     *
     * @param msg 被查询的群消息
     * @return 该消息所在群名称
     */
    private static String getGroupName(GroupMessage msg) {
        return getGroup(msg).getName();
    }

    /**
     * 获取群消息所在群
     *
     * @param msg 被查询的群消息
     * @return 该消息所在群
     */
    private static Group getGroup(GroupMessage msg) {
        return groupFromID.get(msg.getGroupId());
    }

    /**
     * 获取讨论组消息发送者昵称
     *
     * @param msg 被查询的讨论组消息
     * @return 该消息发送者昵称
     */
    private String getDiscussUserNick(DiscussMessage msg) {
        for (DiscussUser user : getDiscussInfoFromID(msg.getDiscussId()).getUsers()) {
            if (user.getUin() == msg.getUserId()) {
                return user.getNick(); //返回发送者昵称
            }
        }
        return "系统消息"; //若在讨论组成员列表中查询不到，则为系统消息
        //TODO: 也有可能是新加讨论组的用户
    }

    /**
     * 获取讨论组id对应讨论组详情
     *
     * @param id 被查询的讨论组id
     * @return 该讨论组详情
     */
    private DiscussInfo getDiscussInfoFromID(Long id) {
        if (!discussInfoFromID.containsKey(id)) {
            discussInfoFromID.put(id, getDiscussInfo(discussFromID.get(id).getId()));
        }
        return discussInfoFromID.get(id);
    }

    /**
     * 获得讨论组的详细信息
     *
     * @param discussId 讨论组id
     * @return
     */
    public DiscussInfo getDiscussInfo(long discussId) {
        logger.debug("开始获取讨论组资料");

        Response<String> response = get(ApiURL.GET_DISCUSS_INFO, discussId, vfwebqq, psessionid);
        JSONObject result = getJsonObjectResult(response);
        DiscussInfo discussInfo = result.getObject("info", DiscussInfo.class);
        //获得讨论组成员信息
        Map<Long, DiscussUser> discussUserMap = new HashMap<>();
        JSONArray minfo = result.getJSONArray("mem_info");
        for (int i = 0; minfo != null && i < minfo.size(); i++) {
            DiscussUser discussUser = minfo.getObject(i, DiscussUser.class);
            discussUserMap.put(discussUser.getUin(), discussUser);
            discussInfo.addUser(discussUser);
        }
        JSONArray stats = result.getJSONArray("mem_status");
        for (int i = 0; stats != null && i < stats.size(); i++) {
            JSONObject item = stats.getJSONObject(i);
            DiscussUser discussUser = discussUserMap.get(item.getLongValue("uin"));
            discussUser.setClientType(item.getIntValue("client_type"));
            discussUser.setStatus(item.getString("status"));
        }
        return discussInfo;
    }

    /**
     * 获取群消息发送者昵称
     *
     * @param msg 被查询的群消息
     * @return 该消息发送者昵称
     */
    private String getGroupUserNick(GroupMessage msg) {
        for (GroupUser user : getGroupInfoFromID(msg.getGroupId()).getUsers()) {
            if (user.getUin() == msg.getUserId()) {
                if (user.getCard() == null || user.getCard().equals("")) {
                    return user.getNick(); //若发送者无群名片则返回其昵称
                } else {
                    return user.getCard(); //否则返回其群名片
                }
            }
        }
        return "系统消息"; //若在群成员列表中查询不到，则为系统消息
        //TODO: 也有可能是新加群的用户或匿名用户
    }

    /**
     * 获取群id对应群详情
     *
     * @param id 被查询的群id
     * @return 该群详情
     */
    private GroupInfo getGroupInfoFromID(Long id) {
        if (!groupInfoFromID.containsKey(id)) {
            groupInfoFromID.put(id, getGroupInfo(groupFromID.get(id).getCode()));
        }
        return groupInfoFromID.get(id);
    }

    /**
     * 获得群的详细信息
     *
     * @param groupCode 群编号
     * @return
     */
    public GroupInfo getGroupInfo(long groupCode) {
        logger.debug("开始获取群资料");

        Response<String> response = get(ApiURL.GET_GROUP_INFO, groupCode, vfwebqq);
        JSONObject result = getJsonObjectResult(response);
        GroupInfo groupInfo = result.getObject("ginfo", GroupInfo.class);
        //获得群成员信息
        Map<Long, GroupUser> groupUserMap = new HashMap<>();
        JSONArray minfo = result.getJSONArray("minfo");
        for (int i = 0; minfo != null && i < minfo.size(); i++) {
            GroupUser groupUser = minfo.getObject(i, GroupUser.class);
            groupUserMap.put(groupUser.getUin(), groupUser);
            groupInfo.addUser(groupUser);
        }
        JSONArray stats = result.getJSONArray("stats");
        for (int i = 0; stats != null && i < stats.size(); i++) {
            JSONObject item = stats.getJSONObject(i);
            GroupUser groupUser = groupUserMap.get(item.getLongValue("uin"));
            groupUser.setClientType(item.getIntValue("client_type"));
            groupUser.setStatus(item.getIntValue("stat"));
        }
        JSONArray cards = result.getJSONArray("cards");
        for (int i = 0; cards != null && i < cards.size(); i++) {
            JSONObject item = cards.getJSONObject(i);
            groupUserMap.get(item.getLongValue("muin")).setCard(item.getString("card"));
        }
        JSONArray vipinfo = result.getJSONArray("vipinfo");
        for (int i = 0; vipinfo != null && i < vipinfo.size(); i++) {
            JSONObject item = vipinfo.getJSONObject(i);
            GroupUser groupUser = groupUserMap.get(item.getLongValue("u"));
            groupUser.setVip(item.getIntValue("is_vip") == 1);
            groupUser.setVipLevel(item.getIntValue("vip_level"));
        }
        return groupInfo;
    }

    /**
     * 获取私聊消息发送者昵称
     *
     * @param msg 被查询的私聊消息
     * @return 该消息发送者
     */
    private static String getFriendNick(Message msg) {
        Friend user = friendFromID.get(msg.getUserId());
        if (user.getMarkname() == null || user.getMarkname().equals("")) {
            return user.getNickname(); //若发送者无备注则返回其昵称
        } else {
            return user.getMarkname(); //否则返回其备注
        }

    }


    /**
     * 获取本地系统时间
     *
     * @return 本地系统时间
     */
    private static String getTime() {
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return time.format(new Date());
    }

    /**
     * 发送群消息
     *
     * @param groupId 群id
     * @param msg     消息内容
     */
    public void sendMessageToGroup(long groupId, String msg) {
        logger.debug("开始发送群消息");

        JSONObject r = new JSONObject();
        r.put("group_uin", groupId);
        r.put("content", JSON.toJSONString(Arrays.asList(msg, Arrays.asList("font", Font.DEFAULT_FONT))));  //注意这里虽然格式是Json，但是实际是String
        r.put("face", 573);
        r.put("clientid", Client_ID);
        r.put("msg_id", MESSAGE_ID++);
        r.put("psessionid", psessionid);

        Response<String> response = postWithRetry(ApiURL.SEND_MESSAGE_TO_GROUP, r);
        checkSendMsgResult(response);
    }

    /**
     * 发送讨论组消息
     *
     * @param discussId 讨论组id
     * @param msg       消息内容
     */
    public void sendMessageToDiscuss(long discussId, String msg) {
        logger.debug("开始发送讨论组消息");

        JSONObject r = new JSONObject();
        r.put("did", discussId);
        r.put("content", JSON.toJSONString(Arrays.asList(msg, Arrays.asList("font", Font.DEFAULT_FONT))));  //注意这里虽然格式是Json，但是实际是String
        r.put("face", 573);
        r.put("clientid", Client_ID);
        r.put("msg_id", MESSAGE_ID++);
        r.put("psessionid", psessionid);

        Response<String> response = postWithRetry(ApiURL.SEND_MESSAGE_TO_DISCUSS, r);
        checkSendMsgResult(response);
    }

    /**
     * 发送消息
     *
     * @param friendId 好友id
     * @param msg      消息内容
     */
    public void sendMessageToFriend(long friendId, String msg) {
        logger.debug("开始发送消息");

        JSONObject r = new JSONObject();
        r.put("to", friendId);
        r.put("content", JSON.toJSONString(Arrays.asList(msg, Arrays.asList("font", Font.DEFAULT_FONT))));  //注意这里虽然格式是Json，但是实际是String
        r.put("face", 573);
        r.put("clientid", Client_ID);
        r.put("msg_id", MESSAGE_ID++);
        r.put("psessionid", psessionid);

        Response<String> response = postWithRetry(ApiURL.SEND_MESSAGE_TO_FRIEND, r);
        checkSendMsgResult(response);
    }

    //发送post请求，失败时重试
    private Response<String> postWithRetry(ApiURL url, JSONObject r) {
        int times = 0;
        Response<String> response;
        do {
            response = post(url, r);
            times++;
        } while (times < RETRY_TIMES && response.getStatusCode() != 200);
        return response;
    }

    //检查消息是否发送成功
    private static void checkSendMsgResult(Response<String> response) {
        if (response.getStatusCode() != 200) {
            logger.error(String.format("发送失败，Http返回码[%d]", response.getStatusCode()));
        }
        JSONObject json = JSON.parseObject(response.getBody());
        Integer errCode = json.getInteger("errCode");
        if (errCode != null && errCode == 0) {
            logger.debug("发送成功");
        } else {
            logger.error(String.format("发送失败，Api返回码[%d]", json.getInteger("retcode")));
        }
    }

    /**
     * 拉取消息
     *
     * @param callback 获取消息后的回调
     */
    private void pollMessage(MessageCallback callback) {
        logger.debug("开始接收消息");

        JSONObject r = new JSONObject();
        r.put("ptwebqq", ptwebqq);
        r.put("clientid", Client_ID);
        r.put("psessionid", psessionid);
        r.put("key", "");

        Response<String> response = post(ApiURL.POLL_MESSAGE, r);
        JSONArray array = getJsonArrayResult(response);
        for (int i = 0; array != null && i < array.size(); i++) {
            JSONObject message = array.getJSONObject(i);
            String type = message.getString("poll_type");
            if ("message".equals(type)) {
                callback.onMessage(new Message(message.getJSONObject("value")));
            } else if ("group_message".equals(type)) {
                callback.onGroupMessage(new GroupMessage(message.getJSONObject("value")));
            } else if ("discu_message".equals(type)) {
                callback.onDiscussMessage(new DiscussMessage(message.getJSONObject("value")));
            }
        }
    }

    //获取返回json的result字段（JSONArray类型）
    private static JSONArray getJsonArrayResult(Response<String> response) {
        return getResponseJson(response).getJSONArray("result");
    }

    //获取返回json的result字段（JSONObject类型）
    private static JSONObject getJsonObjectResult(Response<String> response) {
        return getResponseJson(response).getJSONObject("result");
    }

    //检验Json返回结果
    private static JSONObject getResponseJson(Response<String> response) {
        if (response.getStatusCode() != 200) {
            throw new RequestException(String.format("请求失败，Http返回码[%d]", response.getStatusCode()));
        }
        JSONObject json = JSON.parseObject(response.getBody());
        Integer retCode = json.getInteger("retcode");
        if (retCode == null) {
            throw new RequestException(String.format("请求失败，Api返回异常", retCode));
        } else if (retCode != 0) {
            switch (retCode) {
                case 103: {
                    logger.error("请求失败，Api返回码[103]。你需要进入http://w.qq.com，检查是否能正常接收消息。如果可以的话点击[设置]->[退出登录]后查看是否恢复正常");
                    break;
                }
                case 100100: {
                    logger.debug("请求失败，Api返回码[100100]");
                    break;
                }
                default: {
                    throw new RequestException(String.format("请求失败，Api返回码[%d]", retCode));
                }
            }
        }
        return json;
    }


    //发送get请求
    private Response<String> get(ApiURL url, Object... params) {
        HeadOnlyRequestBuilder request = session.get(url.buildUrl(params))
                .addHeader("User-Agent", ApiURL.USER_AGENT);
        if (url.getReferer() != null) {
            request.addHeader("Referer", url.getReferer());
        }
        return request.text();
    }

    //发送post请求
    private Response<String> post(ApiURL url, JSONObject r) {
        return session.post(url.getUrl())
                .addHeader("User-Agent", ApiURL.USER_AGENT)
                .addHeader("Referer", url.getReferer())
                .addHeader("Origin", url.getOrigin())
                .addForm("r", r.toJSONString())
                .text();
    }

    //用于生成ptqrtoken的哈希函数
    private static int hash33(String s) {
        int e = 0, n = s.length();
        for (int i = 0; n > i; ++i)
            e += (e << 5) + s.charAt(i);
        return 2147483647 & e;
    }

}
