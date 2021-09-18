/*
 * Copyright (c) 2020 GeekShop.
 * All rights reserved.
 */

package io.geekshop.types.stock;

/**
 * Created on Nov, 2020 by @author bobo
 */
public enum StockMovementType {
    ADJUSTMENT,
    /**
     * 销售
     */
    SALE,
    /**
     * 退货
     */
    CANCELLATION,
    /**
     * 还库
     */
    RETURN
}
