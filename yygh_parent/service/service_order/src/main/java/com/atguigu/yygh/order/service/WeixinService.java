package com.atguigu.yygh.order.service;

import java.util.Map;

public interface WeixinService {
    /**
     * 根据订单号下单，生成支付链接
     */
    Map createNative(Long orderId);

    //调用微信接口实现支付状态查询
    Map<String, String> queryPayStatus(Long orderId, String name);

    /***
     * 退款
     * @param orderId
     * @return
     */
    Boolean refund(Long orderId);

}
