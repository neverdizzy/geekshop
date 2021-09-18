/*
 * Copyright (c) 2020 GeekShop.
 * All rights reserved.
 */

package io.geekshop.service.helpers.order_state_machine;

/**
 * 订单处理状态
 * These are the default states of the Order process.
 *
 * Created on Dec, 2020 by @author bobo
 */
public enum OrderState {
    /**
     * 正在选择商品
     */
    AddingItems,
    /**
     * 正在付款
     */
    ArrangingPayment,
    /**
     * 已授权支付
     */
    PaymentAuthorized,
    /**
     * 已结算
     */
    PaymentSettled,
    /**
     * 部分配货
     */
    PartiallyFulfilled,
    /**
     * 已完成
     */
    Fulfilled,
    /**
     * 已取消
     */
    Cancelled
}
