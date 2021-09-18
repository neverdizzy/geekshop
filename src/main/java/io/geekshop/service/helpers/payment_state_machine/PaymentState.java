/*
 * Copyright (c) 2020 GeekShop.
 * All rights reserved.
 */

package io.geekshop.service.helpers.payment_state_machine;

/**
 * These are the default states of the payment process.
 *
 * Created on Dec, 2020 by @author bobo
 */
public enum PaymentState {
    /**
     * 创建支付单
     */
    Created,
    /**
     * 授权支付
     */
    Authorized,
    /**
     * 结算支付
     */
    Settled,
    /**
     * 拒绝支付
     */
    Declined,
    /**
     * 错误支付
     */
    Error
}
