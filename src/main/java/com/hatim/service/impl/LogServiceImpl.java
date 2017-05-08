package com.hatim.service.impl;

import com.hatim.domain.Log;
import com.hatim.domain.LogRepository;
import com.hatim.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Hatim on 2017/5/6.
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    LogRepository logRepository;

    /**
     * 添加日志
     *
     * @param account
     * @param msg
     * @return
     */
    @Override
    public boolean logAdd(String account, String msg) {
        Log log = new Log();
        log.setAccount(account);
        log.setMsg(msg);
        log.setCreateDate(new Date());
        log = logRepository.save(log);
        if (log != null && log.getId() != null) {
            return true;
        }
        return false;
    }
}
