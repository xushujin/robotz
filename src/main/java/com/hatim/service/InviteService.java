package com.hatim.service;

/**
 * 群成员邀请
 * Created by Hatim on 2017/4/24.
 */
public interface InviteService {

    /**
     * 邀请人数
     *
     * @param userId 用户ID
     * @return
     */
    int inviteCount(String userId);


    /**
     * 退群人数
     *
     * @param userId
     * @return
     */
    int quitCount(String userId);

}
