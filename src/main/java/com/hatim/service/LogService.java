package com.hatim.service;

/**
 * 日志
 * Created by Hatim on 2017/5/6.
 */
public interface LogService {

    /**
     * 添加日志
     * @param account
     * @param msg
     * @return
     */
    boolean logAdd(String account, String msg);
}
