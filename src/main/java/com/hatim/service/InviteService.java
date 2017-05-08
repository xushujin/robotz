package com.hatim.service;

/**
 * 群成员邀请
 * Created by Hatim on 2017/4/24.
 */
public interface InviteService {

    /**
     * 邀请记录
     *
     * @param account 用户账号
     * @return
     */
    int invite(String account);


    /**
     * 退群处理
     *
     * @param account 用户账号
     * @return
     */
    int quit(String account);

}
