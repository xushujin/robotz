package com.hatim.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hatim on 2017/4/27.
 */
public class DateUtil {
    /**
     * 获取本地系统时间
     *
     * @return
     */
    public static String getTime() {
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return time.format(new Date());
    }
}
