package com.hatim.service;

/**
 * qq机器人
 * Created by Hatim on 2017/4/22.
 */
public interface SmartQQService {
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
     * 获取二维码
     *
     * @return
     */
    String getQRCode();

    /**
     * 校验二维码
     *
     * @return
     */
    void verifyQRCode();
}
