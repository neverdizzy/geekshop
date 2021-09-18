/*
 * Copyright (c) 2020 GeekShop.
 * All rights reserved.
 */

package io.geekshop.config.shipping_method;

import io.geekshop.common.ConfigArgValues;
import io.geekshop.common.ConfigurableOperationDef;
import io.geekshop.entity.OrderEntity;
import io.geekshop.types.shipping.ShippingMethod;
import lombok.Getter;

/**
 * The ShippingEligibilityChecker class is used to check whether an order qualifies for a given {@link ShippingMethod}.
 *
 * Created on Dec, 2020 by @author bobo
 */
@Getter
public abstract class ShippingEligibilityChecker extends ConfigurableOperationDef {
    private final String code;
    private final String description;

    protected ShippingEligibilityChecker(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 检查给定的订单并确保它是否符合条件
     * Check the given Order to determine whether it is eligible.
     */
    public abstract boolean check(OrderEntity orderEntity, ConfigArgValues argValues);
}
