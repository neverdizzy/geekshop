/*
 * Copyright (c) 2020 GeekShop.
 * All rights reserved.
 */

package io.geekshop.service.helpers.refund_state_machine;

/**
 * These are the default states of the refund process.
 *
 * Created on Dec, 2020 by @author bobo
 */
public enum RefundState {
    /**
     * 退款中
     */
    Pending,
    /**
     * 完成
     */
    Settled,
    /**
     * 失败
     */
    Failed
}
