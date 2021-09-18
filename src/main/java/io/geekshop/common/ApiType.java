/*
 * Copyright (c) 2020 GeekShop.
 * All rights reserved.
 */

package io.geekshop.common;

/**
 * Which of the GraphQL APIs the current request came via.
 *
 * Created on Nov, 2020 by @author bobo
 */
public enum ApiType {
    /**
     * 管理后台
     */
    ADMIN,
    /**
     * 购物网站端
     */
    SHOP,
    /**
     * 消费者客户
     */
    CUSTOM
}
