package com.hatim.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hatim.common.constant.Global;
import com.hatim.common.constant.enu.ApiURL;
import com.hatim.model.DiscussMessageModel;
import com.hatim.model.FontModel;
import com.hatim.model.GroupMessageModel;
import com.hatim.model.MessageModel;
import com.hatim.service.QQMessageService;
import net.dongliu.requests.Response;
import net.dongliu.requests.exception.RequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by Hatim on 2017/4/27.
 */
public class QQMsgUtil {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(QQMsgUtil.class);
    /**
     * 发送群消息
     *
     * @param groupId 群id
     * @param msg     消息内容
     */
    public static void sendMessageToGroup(long groupId, String msg) {

        JSONObject r = new JSONObject();
        r.put("group_uin", groupId);
        r.put("content", JSON.toJSONString(Arrays.asList(msg, Arrays.asList("font", FontModel.DEFAULT_FONT))));  //注意这里虽然格式是Json，但是实际是String
        r.put("face", 573);
        r.put("clientid", Global.Client_ID);
        r.put("msg_id", Global.MESSAGE_ID++);
        r.put("psessionid", Global.psessionid);

        Response<String> response = HttpUtil.postWithRetry(ApiURL.SEND_MESSAGE_TO_GROUP, r, Global.session);
        checkSendMsgResult(response);
    }

    /**
     * 发送讨论组消息
     *
     * @param discussId 讨论组id
     * @param msg       消息内容
     */
    public static void sendMessageToDiscuss(long discussId, String msg) {

        JSONObject r = new JSONObject();
        r.put("did", discussId);
        r.put("content", JSON.toJSONString(Arrays.asList(msg, Arrays.asList("font", FontModel.DEFAULT_FONT))));  //注意这里虽然格式是Json，但是实际是String
        r.put("face", 573);
        r.put("clientid", Global.Client_ID);
        r.put("msg_id", Global.MESSAGE_ID++);
        r.put("psessionid", Global.psessionid);

        Response<String> response = HttpUtil.postWithRetry(ApiURL.SEND_MESSAGE_TO_DISCUSS, r, Global.session);
        checkSendMsgResult(response);
    }

    /**
     * 发送私聊消息
     *
     * @param friendId 好友id
     * @param msg      消息内容
     */
    public static void sendMessageToFriend(long friendId, String msg) {

        JSONObject r = new JSONObject();
        r.put("to", friendId);
        r.put("content", JSON.toJSONString(Arrays.asList(msg, Arrays.asList("font", FontModel.DEFAULT_FONT))));  //注意这里虽然格式是Json，但是实际是String
        r.put("face", 573);
        r.put("clientid", Global.Client_ID);
        r.put("msg_id", Global.MESSAGE_ID++);
        r.put("psessionid", Global.psessionid);

        Response<String> response = HttpUtil.postWithRetry(ApiURL.SEND_MESSAGE_TO_FRIEND, r, Global.session);
        checkSendMsgResult(response);
    }

    /**
     * 拉取消息
     *
     * @param callback 获取消息后的回调
     */
    public static void pollMessage(QQMessageService callback) {

        JSONObject r = new JSONObject();
        r.put("ptwebqq", Global.ptwebqq);
        r.put("clientid", Global.Client_ID);
        r.put("psessionid", Global.psessionid);
        r.put("key", "");

        Response<String> response = HttpUtil.post(ApiURL.POLL_MESSAGE, r, Global.session);
        JSONArray array = getJsonArrayResult(response);
        for (int i = 0; array != null && i < array.size(); i++) {
            JSONObject message = array.getJSONObject(i);
            String type = message.getString("poll_type");
            if ("message".equals(type)) {
                callback.onMessage(new MessageModel(message.getJSONObject("value")));
            } else if ("group_message".equals(type)) {
                callback.onGroupMessage(new GroupMessageModel(message.getJSONObject("value")));
            } else if ("discu_message".equals(type)) {
                callback.onDiscussMessage(new DiscussMessageModel(message.getJSONObject("value")));
            }
        }
    }

    /**
     * 检查消息是否发送成功
     *
     * @param response
     */
    public static void checkSendMsgResult(Response<String> response) {
        if (response.getStatusCode() != 200) {
            logger.info("发送失败，Http返回码" + response.getStatusCode());
        }
        JSONObject json = JSON.parseObject(response.getBody());
        Integer errCode = json.getInteger("errCode");
        if (errCode != null && errCode == 0) {
            logger.info("发送成功");
        } else {
            logger.info("发送失败，Api返回码" + json.getInteger("retcode"));
        }
    }

    /**
     * 获取返回json的result字段（JSONArray类型）
     *
     * @param response
     * @return
     */
    public static JSONArray getJsonArrayResult(Response<String> response) {
        return getResponseJson(response).getJSONArray("result");
    }

    /**
     * 获取返回json的result字段（JSONObject类型）
     *
     * @param response
     * @return
     */
    public static JSONObject getJsonObjectResult(Response<String> response) {
        return getResponseJson(response).getJSONObject("result");
    }

    /**
     * 检验Json返回结果
     *
     * @param response
     * @return
     */
    public static JSONObject getResponseJson(Response<String> response) {
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
                    logger.info("请求失败，Api返回码[103]。你需要进入http://w.qq.com，检查是否能正常接收消息。如果可以的话点击[设置]->[退出登录]后查看是否恢复正常");
                    break;
                }
                case 100100: {
                    logger.info("请求失败，Api返回码[100100]");
                    break;
                }
                default: {
                    throw new RequestException(String.format("请求失败，Api返回码[%d]", retCode));
                }
            }
        }
        return json;
    }
}
