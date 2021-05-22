package com.atguigu.yygh.msm.service;

import com.atguigu.yygh.vo.msm.MsmVo;

public interface MsmService {
    // 发送手机验证码
    boolean send(String phone, String code);

    // 使用容联云发送短信验证码
    boolean sendByCloopen(String phone, String code);

    // mq发送短信
    boolean send(MsmVo msmVo);
}
