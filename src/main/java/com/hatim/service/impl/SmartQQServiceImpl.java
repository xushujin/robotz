package com.hatim.service.impl;

import com.hatim.common.contents.Global;
import com.hatim.common.smartqq.callback.MessageCallback;
import com.hatim.common.smartqq.constant.ApiURL;
import com.hatim.common.smartqq.model.DiscussMessage;
import com.hatim.common.smartqq.model.GroupMessage;
import com.hatim.common.smartqq.model.Message;
import com.hatim.common.smartqq.model.UserInfo;
import com.hatim.common.utils.*;
import com.hatim.service.SmartQQService;
import net.dongliu.requests.Client;
import net.dongliu.requests.Response;
import net.dongliu.requests.exception.RequestException;
import net.dongliu.requests.struct.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Objects;

import static java.lang.Thread.sleep;

/**
 * Created by Hatim on 2017/4/22.
 */
@Service
public class SmartQQServiceImpl implements SmartQQService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(SmartQQServiceImpl.class);

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
        Global.client = Client.pooled().maxPerRoute(5).maxTotal(10).build();
        Global.session = Global.client.session();

        //本地存储二维码图片
        String filePath;
        try {
            // 先把已存在的图片删除
            new File("src\\main\\resources\\static\\images\\qrcode.png").deleteOnExit();
            filePath = new File("src\\main\\resources\\static\\images\\qrcode.png").getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException("二维码保存失败");
        }
        Response response = Global.session.get(ApiURL.GET_QR_CODE.getUrl())
                .addHeader("User-Agent", ApiURL.USER_AGENT)
                .file(filePath);
        for (Cookie cookie : response.getCookies()) {
            if (Objects.equals(cookie.getName(), "qrsig")) {
                Global.qrsig = cookie.getValue();
                break;
            }
        }
        logger.info("二维码已保存在 " + filePath + " 文件中，请打开手机QQ并扫描二维码");
        return filePath;
    }

    /**
     * 校验二维码并登录
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
            Global.isWaittingLogin = true;
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Response<String> response = HttpUtil.get(ApiURL.VERIFY_QR_CODE, Global.session, EncryptUtil.hash33(Global.qrsig));
            String result = response.getBody();
            logger.info("result:{}", result);
            if (result.contains("成功")) {
                for (String content : result.split("','")) {
                    if (content.startsWith("http")) {
                        logger.info("正在登录，请稍后");

                        // 登录流程3：获取ptwebqq
                        QQLoginUtil.getPtwebqq(content);
                        // 登录流程4：获取vfwebqq
                        QQLoginUtil.getVfwebqq();
                        // 登录流程5：获取uin和psessionid
                        QQLoginUtil.getUinAndPsessionid();
                        // 获取朋友列表
                        QQInfoUtil.getFriendStatus(); //修复Api返回码[103]的问题
                        // 登录成功欢迎语
                        UserInfo userInfo = QQInfoUtil.getAccountInfo();
                        logger.info(userInfo.getNick() + "，登录成功！");

                        // TODO 这里的信息要定时刷新
                        QQInfoUtil.getQQmembersInfo();

                        // 创建客户端监听及处理消息
                        createClient();
                        // 跳出阻塞
                        flag = false;
                    }
                }
            } else if (result.contains("已失效")) {
                logger.info("二维码已失效，尝试重新获取二维码");
                getQRCode();
            }
        }
    }


    // 创建客户端
    public void createClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        QQMsgUtil.pollMessage(myCallback);
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

    MessageCallback myCallback = new MessageCallback() {
        @Override
        public void onMessage(Message message) {
            if (message.getContent().contains(Global.CONTAINS_KEY)) {
                QQMsgUtil.sendMessageToFriend(message.getUserId(), QQInfoUtil.getFriendNick(message) + ",大爷你好！");
            }
            System.out.println("[" + DateUtil.getTime() + "] [私聊] " + QQInfoUtil.getFriendNick(message) + "：" + message.getContent());
        }

        @Override
        public void onGroupMessage(GroupMessage message) {
            if (message.getContent().contains(Global.CONTAINS_KEY)) {
                QQMsgUtil.sendMessageToGroup(message.getGroupId(), QQInfoUtil.getGroupUserNick(message) + " 大爷你好！");
            }
            System.out.println("[" + DateUtil.getTime() + "] [" + QQInfoUtil.getGroupName(message) + "] " + QQInfoUtil.getGroupUserNick(message) + "：" + message.getContent());
        }

        @Override
        public void onDiscussMessage(DiscussMessage message) {
            if (message.getContent().contains(Global.CONTAINS_KEY)) {
                QQMsgUtil.sendMessageToDiscuss(message.getDiscussId(), QQInfoUtil.getDiscussUserNick(message) + " 大爷你好！");
            }
            System.out.println("[" + DateUtil.getTime() + "] [" + QQInfoUtil.getDiscussName(message) + "] " + QQInfoUtil.getDiscussUserNick(message) + "：" + message.getContent());
        }
    };

}
