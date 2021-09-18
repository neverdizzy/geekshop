/*
 * Copyright (c) 2020 GeekShop.
 * All rights reserved.
 */

package io.geekshop.config.shipping_method;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * Created on Dec, 2020 by @author bobo
 */
@Data
public class ShippingCalculationResult {
    /**
     * 运送方式价格
     * The shipping_method price
     */
    private Integer price;
    /**
     * Arbitrary metadata may be returned from the calculation function.
     * This can be used e.g. to return data on estimated delivery times or any other data which may be
     * needed in the storefront application when listing eligible shipping_method methods.
     */
    private Map<String, String> metadata = new HashMap<>();
}
