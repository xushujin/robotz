package com.hatim.service.impl;

import com.hatim.bo.DiscussBo;
import com.hatim.bo.FriendBo;
import com.hatim.bo.GroupBo;
import com.hatim.bo.UserInfoBo;
import com.hatim.common.constant.Status;
import com.hatim.common.constant.enu.ApiURL;
import com.hatim.common.utils.*;
import com.hatim.service.MessageService;
import com.hatim.service.QQService;
import net.dongliu.requests.Response;
import net.dongliu.requests.exception.RequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.SocketTimeoutException;

import static java.lang.Thread.sleep;

/**
 * Created by Hatim on 2017/4/22.
 */
@Service
public class QQServiceImpl implements QQService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(QQServiceImpl.class);

    @Autowired
    MessageService qqMessageService;

    /**
     * 开启服务
     *
     * @return
     */
    @Async
    @Override
    public void startService() {
        // 先获取登录验证码
        QQLoginUtil.getQRCode();

        logger.info("等待扫描二维码");
        Status.isWaittingLogin = true;
        boolean flag = true;
        //阻塞直到确认二维码认证成功
        while (flag) {
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Response<String> response = HttpUtil.get(ApiURL.VERIFY_QR_CODE, Status.session, EncryptUtil.hash33(Status.qrsig));
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
                        UserInfoBo userInfo = QQInfoUtil.getAccountInfo();
                        logger.info(userInfo.getNick() + "，登录成功！");

                        // 创建客户端监听及处理消息
                        createClient();
                        // 跳出阻塞
                        flag = false;
                    }
                }
            } else if (result.contains("已失效")) {
                logger.info("二维码已失效，尝试重新获取二维码");
                QQLoginUtil.getQRCode();
            }
        }
        refresh();
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
     * 比如刷新好友列表
     */
    @Async
    @Override
    public void refresh() {
        while (true) {
            logger.info("刷新好友列表");
            // 获取好友列表
            Status.friendList = QQInfoUtil.getFriendList();
            // 获取群列表
            Status.groupList = QQInfoUtil.getGroupList();
            // 获取讨论组列表
            Status.discussList = QQInfoUtil.getDiscussList();
            // 建立好友id到好友映射
            for (FriendBo friend : Status.friendList) {
                Status.friendFromID.put(friend.getUserId(), friend);
            }
            // 建立群id到群映射
            for (GroupBo group : Status.groupList) {
                Status.groupFromID.put(group.getId(), group);
            }
            // 建立讨论组id到讨论组映射
            for (DiscussBo discuss : Status.discussList) {
                Status.discussFromID.put(discuss.getId(), discuss);
            }

            try {
                // 60秒刷新一次
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    // 创建客户端
    public void createClient() {
        logger.info("创建监听消息客户端");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        QQMsgUtil.pollMessage(qqMessageService);
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
}
