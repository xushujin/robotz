package com.hatim.service;

/**
 * qq机器人
 * Created by Hatim on 2017/4/22.
 */
public interface QQService {
    /**
     * 开启服务
     *
     * @return
     */
    void startService();

    /**
     * 关闭服务
     *
     * @return
     */
    boolean stopService();

    /**
     * 比如刷新好友列表
     */
    void refresh();
}
