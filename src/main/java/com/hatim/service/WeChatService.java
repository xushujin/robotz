package com.hatim.service;

/**
 * 微信机器人
 * Created by Hatim on 2017/4/24.
 */
public interface WeChatService {
    /**
     * 开启服务
     * @return
     */
    String startService();

    /**
     * 关闭服务
     * @return
     */
    boolean stopService();
}
