package com.hatim.service;

/**
 * 会员体系
 * Created by Hatim on 2017/4/24.
 */
public interface AssociatorService {

    /**
     * 会员积分查询
     *
     * @param userId 用户ID
     * @return
     */
    int integral(String userId);

    /**
     * 会员积分调整
     *
     * @param userId 用户ID
     * @param num    正数为加，负数为减
     * @return
     */
    int integral(String userId, int num);

    /**
     * 会员等级查询
     *
     * @param userId 用户ID
     * @return
     */
    int vip(String userId);

    /**
     * 会员等级调整
     *
     * @param userId 用户ID
     * @param num    正数为加，负数为减
     * @return
     */
    int vip(String userId, int num);
}
