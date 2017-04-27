package com.hatim.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.hatim.common.contents.Global;
import com.hatim.common.smartqq.constant.ApiURL;
import net.dongliu.requests.Response;

/**
 * Created by Hatim on 2017/4/27.
 */
public class QQLoginUtil {
    // 登录流程3：获取ptwebqq
    public static void getPtwebqq(String url) {

        Response<String> response = HttpUtil.get(ApiURL.GET_PTWEBQQ, Global.session, url);
        Global.ptwebqq = response.getCookies().get("ptwebqq").iterator().next().getValue();
    }

    // 登录流程4：获取vfwebqq
    public static void getVfwebqq() {

        Response<String> response = HttpUtil.get(ApiURL.GET_VFWEBQQ, Global.session, Global.ptwebqq);
        Global.vfwebqq = QQMsgUtil.getJsonObjectResult(response).getString("vfwebqq");
    }

    // 登录流程5：获取uin和psessionid
    public static void getUinAndPsessionid() {

        JSONObject r = new JSONObject();
        r.put("ptwebqq", Global.ptwebqq);
        r.put("clientid", Global.Client_ID);
        r.put("psessionid", "");
        r.put("status", "online");

        Response<String> response = HttpUtil.post(ApiURL.GET_UIN_AND_PSESSIONID, r, Global.session);
        JSONObject result = QQMsgUtil.getJsonObjectResult(response);
        Global.psessionid = result.getString("psessionid");
        Global.uin = result.getLongValue("uin");
    }
}
