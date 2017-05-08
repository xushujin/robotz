package com.hatim.service;

import com.hatim.common.constant.enu.Vip;
import com.hatim.domain.Member;

/**
 * 会员体系
 * Created by Hatim on 2017/4/24.
 */
public interface MemberService {

    /**
     * 会员信息查询
     *
     * @param account
     * @return
     */
    Member findByAccount(String account);

    /**
     * 会员积分修改
     *
     * @param account 用户账号
     * @param point     正数为加，负数为减
     * @return
     */
    boolean pointMod(String account, int point);

    /**
     * 会员等级修改
     *
     * @param account 用户账号
     * @param vip     升级/降级
     * @return
     */
    boolean vipMod(String account, Vip vip);


}
