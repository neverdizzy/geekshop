/*
 * Copyright (c) 2020 GeekShop.
 * All rights reserved.
 */

package io.geekshop.types.history;

/**
 * Created on Nov, 2020 by @author bobo
 */
public enum HistoryEntryType {
    /**
     * 客户注册
     */
    CUSTOMER_REGISTERED,
    /**
     * 客户验证
     */
    CUSTOMER_VERIFIED,
    /**
     * 客户详情修改
     */
    CUSTOMER_DETAIL_UPDATED,
    /**
     * 客户添加到客户群体
     */
    CUSTOMER_ADDED_TO_GROUP,
    /**
     * 客户移除客户群体
     */
    CUSTOMER_REMOVED_FROM_GROUP,
    /**
     * 客户新增地址信息
     */
    CUSTOMER_ADDRESS_CREATED,
    /**
     * 客户修改地址信息
     */
    CUSTOMER_ADDRESS_UPDATED,
    /**
     * 客户删除地址信息
     */
    CUSTOMER_ADDRESS_DELETED,
    /**
     * 客户密码修改
     */
    CUSTOMER_PASSWORD_UPDATED,
    /**
     * 客户密码重置
     */
    CUSTOMER_PASSWORD_RESET_REQUESTED,
    /**
     * 客户密码验证
     */
    CUSTOMER_PASSWORD_RESET_VERIFIED,
    /**
     * 客户邮箱修改
     */
    CUSTOMER_EMAIL_UPDATE_REQUESTED,
    /**
     * 客户邮箱修改验证
     */
    CUSTOMER_EMAIL_UPDATE_VERIFIED,
    /**
     * 客户备注
     */
    CUSTOMER_NOTE,
    /**
     * 订单状态转换
     */
    ORDER_STATE_TRANSITION,
    /**
     * 订单支付状态转换
     */
    ORDER_PAYMENT_TRANSITION,
    /**
     * 订单履约
     */
    ORDER_FULFILLMENT,
    /**
     * 订单取消
     */
    ORDER_CANCELLATION,
    /**
     * 退单
     */
    ORDER_REFUND_TRANSITION,
    /**
     * 订单备注
     */
    ORDER_NOTE,
    /**
     * 订单优惠券应用
     */
    ORDER_COUPON_APPLIED,
    /**
     * 订单优惠券删除
     */
    ORDER_COUPON_REMOVED
}
