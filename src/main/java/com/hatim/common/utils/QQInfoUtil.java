package com.hatim.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hatim.common.constant.Global;
import com.hatim.common.constant.enu.ApiURL;
import com.hatim.model.*;
import net.dongliu.requests.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hatim on 2017/4/27.
 */
public class QQInfoUtil {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(QQInfoUtil.class);

    /**
     * 获取私聊消息发送者昵称
     *
     * @param msg 被查询的私聊消息
     * @return
     */
    public static String getFriendNick(MessageModel msg) {
        FriendModel user = Global.friendFromID.get(msg.getUserId());
        if (user.getMarkname() == null || user.getMarkname().equals("")) {
            return user.getNickname(); //若发送者无备注则返回其昵称
        } else {
            return user.getMarkname(); //否则返回其备注
        }

    }

    /**
     * 获得群的详细信息
     *
     * @param groupCode 群编号
     * @return
     */
    public static GroupInfoModel getGroupInfo(long groupCode) {

        Response<String> response = HttpUtil.get(ApiURL.GET_GROUP_INFO, Global.session, groupCode, Global.vfwebqq);
        JSONObject result = QQMsgUtil.getJsonObjectResult(response);
        GroupInfoModel groupInfo = result.getObject("ginfo", GroupInfoModel.class);
        //获得群成员信息
        Map<Long, GroupUserModel> groupUserMap = new HashMap<>();
        JSONArray minfo = result.getJSONArray("minfo");
        for (int i = 0; minfo != null && i < minfo.size(); i++) {
            GroupUserModel groupUser = minfo.getObject(i, GroupUserModel.class);
            groupUserMap.put(groupUser.getUin(), groupUser);
            groupInfo.addUser(groupUser);
        }
        JSONArray stats = result.getJSONArray("stats");
        for (int i = 0; stats != null && i < stats.size(); i++) {
            JSONObject item = stats.getJSONObject(i);
            GroupUserModel groupUser = groupUserMap.get(item.getLongValue("uin"));
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
            GroupUserModel groupUser = groupUserMap.get(item.getLongValue("u"));
            groupUser.setVip(item.getIntValue("is_vip") == 1);
            groupUser.setVipLevel(item.getIntValue("vip_level"));
        }
        return groupInfo;
    }

    /**
     * 获取群id对应群详情
     *
     * @param id 被查询的群id
     * @return
     */
    public static GroupInfoModel getGroupInfoFromID(Long id) {
        if (!Global.groupInfoFromID.containsKey(id)) {
            Global.groupInfoFromID.put(id, getGroupInfo(Global.groupFromID.get(id).getCode()));
        }
        return Global.groupInfoFromID.get(id);
    }

    /**
     * 获取群消息发送者昵称
     *
     * @param msg 被查询的群消息
     * @return
     */
    public static String getGroupUserNick(GroupMessageModel msg) {
        for (GroupUserModel user : getGroupInfoFromID(msg.getGroupId()).getUsers()) {
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
     * 获得讨论组的详细信息
     *
     * @param discussId 讨论组id
     * @return
     */
    public static DiscussInfoModel getDiscussInfo(long discussId) {

        Response<String> response = HttpUtil.get(ApiURL.GET_DISCUSS_INFO, Global.session, discussId, Global.vfwebqq, Global.psessionid);
        JSONObject result = QQMsgUtil.getJsonObjectResult(response);
        DiscussInfoModel discussInfo = result.getObject("info", DiscussInfoModel.class);
        //获得讨论组成员信息
        Map<Long, DiscussUserModel> discussUserMap = new HashMap<>();
        JSONArray minfo = result.getJSONArray("mem_info");
        for (int i = 0; minfo != null && i < minfo.size(); i++) {
            DiscussUserModel discussUser = minfo.getObject(i, DiscussUserModel.class);
            discussUserMap.put(discussUser.getUin(), discussUser);
            discussInfo.addUser(discussUser);
        }
        JSONArray stats = result.getJSONArray("mem_status");
        for (int i = 0; stats != null && i < stats.size(); i++) {
            JSONObject item = stats.getJSONObject(i);
            DiscussUserModel discussUser = discussUserMap.get(item.getLongValue("uin"));
            discussUser.setClientType(item.getIntValue("client_type"));
            discussUser.setStatus(item.getString("status"));
        }
        return discussInfo;
    }

    /**
     * 获取讨论组id对应讨论组详情
     *
     * @param id 被查询的讨论组id
     * @return
     */
    public static DiscussInfoModel getDiscussInfoFromID(Long id) {
        if (!Global.discussInfoFromID.containsKey(id)) {
            Global.discussInfoFromID.put(id, getDiscussInfo(Global.discussFromID.get(id).getId()));
        }
        return Global.discussInfoFromID.get(id);
    }

    /**
     * 获取讨论组消息发送者昵称
     *
     * @param msg 被查询的讨论组消息
     * @return
     */
    public static String getDiscussUserNick(DiscussMessageModel msg) {
        for (DiscussUserModel user : getDiscussInfoFromID(msg.getDiscussId()).getUsers()) {
            if (user.getUin() == msg.getUserId()) {
                return user.getNick(); //返回发送者昵称
            }
        }
        return "系统消息"; //若在讨论组成员列表中查询不到，则为系统消息
        //TODO: 也有可能是新加讨论组的用户
    }

    /**
     * 获取好友列表
     *
     * @return
     */
    public static List<FriendModel> getFriendList() {

        JSONObject r = new JSONObject();
        r.put("vfwebqq", Global.vfwebqq);
        r.put("hash", EncryptUtil.hash(Global.uin, Global.ptwebqq));

        Response<String> response = HttpUtil.post(ApiURL.GET_FRIEND_LIST, r, Global.session);
        return new ArrayList<>(parseFriendMap(QQMsgUtil.getJsonObjectResult(response)).values());
    }

    /**
     * 获取讨论组消息所在讨论组名称
     *
     * @param msg 被查询的讨论组消息
     * @return
     */
    public static String getDiscussName(DiscussMessageModel msg) {
        return getDiscuss(msg).getName();
    }

    /**
     * 获取讨论组消息所在讨论组
     *
     * @param msg 被查询的讨论组消息
     * @return
     */
    public static DiscussModel getDiscuss(DiscussMessageModel msg) {
        return Global.discussFromID.get(msg.getDiscussId());
    }

    /**
     * 获取群消息所在群名称
     *
     * @param msg 被查询的群消息
     * @return
     */
    public static String getGroupName(GroupMessageModel msg) {
        return getGroup(msg).getName();
    }

    /**
     * 获取群消息所在群
     *
     * @param msg 被查询的群消息
     * @return
     */
    public static GroupModel getGroup(GroupMessageModel msg) {
        return Global.groupFromID.get(msg.getGroupId());
    }

    /**
     * 将json解析为好友列表
     *
     * @param result
     * @return
     */
    public static Map<Long, FriendModel> parseFriendMap(JSONObject result) {
        Map<Long, FriendModel> friendMap = new HashMap<>();
        JSONArray info = result.getJSONArray("info");
        for (int i = 0; info != null && i < info.size(); i++) {
            JSONObject item = info.getJSONObject(i);
            FriendModel friend = new FriendModel();
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
            FriendModel friend = friendMap.get(item.getLongValue("u"));
            friend.setVip(item.getIntValue("is_vip") == 1);
            friend.setVipLevel(item.getIntValue("vip_level"));
        }
        return friendMap;
    }

    /**
     * 获得登录状态
     *
     * @return
     */
    public static List<FriendStatusModel> getFriendStatus() {

        Response<String> response = HttpUtil.get(ApiURL.GET_FRIEND_STATUS, Global.session, Global.vfwebqq, Global.psessionid);
        return JSON.parseArray(QQMsgUtil.getJsonArrayResult(response).toJSONString(), FriendStatusModel.class);
    }

    /**
     * 获得当前登录用户的详细信息
     *
     * @return
     */
    public static UserInfoModel getAccountInfo() {

        Response<String> response = HttpUtil.get(ApiURL.GET_ACCOUNT_INFO, Global.session);
        return JSON.parseObject(QQMsgUtil.getJsonObjectResult(response).toJSONString(), UserInfoModel.class);
    }

    /**
     * 获得讨论组列表
     *
     * @return
     */
    public static List<DiscussModel> getDiscussList() {

        Response<String> response = HttpUtil.get(ApiURL.GET_DISCUSS_LIST, Global.session, Global.psessionid, Global.vfwebqq);
        return JSON.parseArray(QQMsgUtil.getJsonObjectResult(response).getJSONArray("dnamelist").toJSONString(), DiscussModel.class);
    }

    /**
     * 获取群列表
     *
     * @return
     */
    public static List<GroupModel> getGroupList() {

        JSONObject r = new JSONObject();
        r.put("vfwebqq", Global.vfwebqq);
        r.put("hash", EncryptUtil.hash(Global.uin, Global.ptwebqq));

        Response<String> response = HttpUtil.post(ApiURL.GET_GROUP_LIST, r, Global.session);
        JSONObject result = QQMsgUtil.getJsonObjectResult(response);
        return JSON.parseArray(result.getJSONArray("gnamelist").toJSONString(), GroupModel.class);
    }

    /**
     * 初始化成员信息
     */
    public static void refreshQQmembersInfo() {
        while (true) {
            logger.info("刷新好友列表");
            // 获取好友列表
            Global.friendList = QQInfoUtil.getFriendList();
            // 获取群列表
            Global.groupList = QQInfoUtil.getGroupList();
            // 获取讨论组列表
            Global.discussList = QQInfoUtil.getDiscussList();
            // 建立好友id到好友映射
            for (FriendModel friend : Global.friendList) {
                Global.friendFromID.put(friend.getUserId(), friend);
            }
            // 建立群id到群映射
            for (GroupModel group : Global.groupList) {
                Global.groupFromID.put(group.getId(), group);
            }
            // 建立讨论组id到讨论组映射
            for (DiscussModel discuss : Global.discussList) {
                Global.discussFromID.put(discuss.getId(), discuss);
            }

            try {
                // 60秒刷新一次
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
