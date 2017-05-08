package com.hatim.service.impl;

import com.hatim.common.constant.enu.Vip;
import com.hatim.domain.Member;
import com.hatim.domain.MemberRepository;
import com.hatim.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Hatim on 2017/5/6.
 */
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    MemberRepository memberRepository;

    /**
     * 会员信息查询
     *
     * @param account
     * @return
     */
    @Override
    public Member findByAccount(String account) {
        return memberRepository.findByAccount(account);
    }

    /**
     * 会员积分修改
     *
     * @param account 用户账号
     * @param point   正数为加，负数为减
     * @return
     */
    @Override
    public boolean pointMod(String account, int point) {
        Member member = memberRepository.findByAccount(account);
        member.setPoint(member.getPoint() + point);
        memberRepository.delete(member.getId());
        member = memberRepository.save(member);
        if (member != null && member.getId() != null) {
            return true;
        }
        return false;
    }

    /**
     * 会员等级修改
     *
     * @param account 用户账号
     * @param vip     升级/降级
     * @return
     */
    @Override
    public boolean vipMod(String account, Vip vip) {
        Member member = memberRepository.findByAccount(account);
        if(Vip.Down==vip){
            // 降级
            member.setVip(member.getVip() - 1);
        }
        if(Vip.Up==vip){
            // 升级
            member.setVip(member.getVip() + 1);
        }
        memberRepository.delete(member.getId());
        member = memberRepository.save(member);
        if (member != null && member.getId() != null) {
            return true;
        }
        return false;
    }
}
