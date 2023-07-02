package com.jktickets.context;

import com.jktickets.res.MemberLoginRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//用于存取 Member的id
public class LoginMemberContext {
    private static final Logger LOG = LoggerFactory.getLogger(LoginMemberContext.class);

    private static final ThreadLocal<MemberLoginRes> member = new ThreadLocal<>();

    public static MemberLoginRes getMember() {
        return member.get();
    }

    public static void setMember(MemberLoginRes member) {
        LoginMemberContext.member.set(member);
    }

    public static Long getId() {
        try {
            return member.get().getId();
        } catch (Exception e) {
            LOG.error("获取登录会员信息异常", e);
            throw e;
        }
    }

}
