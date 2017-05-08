package com.hatim.service.impl;

import com.hatim.service.WeChatService;
import org.springframework.stereotype.Service;

/**
 * Created by Hatim on 2017/4/24.
 */
@Service
public class WeChatServiceImpl implements WeChatService {
    /**
     * 开启服务
     *
     * @return
     */
    @Override
    public String startService() {
        return null;
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
}
