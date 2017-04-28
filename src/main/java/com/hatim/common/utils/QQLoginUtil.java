package com.hatim.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.hatim.common.constant.Global;
import com.hatim.common.constant.enu.ApiURL;
import net.dongliu.requests.Client;
import net.dongliu.requests.Response;
import net.dongliu.requests.struct.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by Hatim on 2017/4/27.
 */
public class QQLoginUtil {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(QQLoginUtil.class);

    /**
     * 获取验证码
     */
    public static void getQRCode() {
        logger.info("开始获取二维码");
        Global.client = Client.pooled().maxPerRoute(5).maxTotal(10).build();
        Global.session = Global.client.session();

        //本地存储二维码图片
        try {
            // 先把已存在的图片删除
            new File("src\\main\\resources\\static\\images\\qrcode.png").deleteOnExit();
            Global.imgUrl = new File("src\\main\\resources\\static\\images\\qrcode.png").getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException("二维码保存失败");
        }
        Response response = Global.session.get(ApiURL.GET_QR_CODE.getUrl())
                .addHeader("User-Agent", ApiURL.USER_AGENT)
                .file(Global.imgUrl);
        for (Cookie cookie : response.getCookies()) {
            if (Objects.equals(cookie.getName(), "qrsig")) {
                Global.qrsig = cookie.getValue();
                break;
            }
        }
        logger.info("二维码已保存在 " + Global.imgUrl + " 文件中，请打开手机QQ并扫描二维码");
    }

    /**
     * 登录流程3：获取ptwebqq
     *
     * @param url
     */
    public static void getPtwebqq(String url) {

        Response<String> response = HttpUtil.get(ApiURL.GET_PTWEBQQ, Global.session, url);
        Global.ptwebqq = response.getCookies().get("ptwebqq").iterator().next().getValue();
    }

    /**
     * 登录流程4：获取vfwebqq
     */
    public static void getVfwebqq() {

        Response<String> response = HttpUtil.get(ApiURL.GET_VFWEBQQ, Global.session, Global.ptwebqq);
        Global.vfwebqq = QQMsgUtil.getJsonObjectResult(response).getString("vfwebqq");
    }

    /**
     * 登录流程5：获取uin和psessionid
     */
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
