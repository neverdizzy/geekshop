/*
 * Copyright (c) 2021 掘艺网络(jueyi.co).
 * All rights reserved.
 */

package io.geekshop.asset;

/**
 * Created on Jan, 2021 by @author bobo
 */
public class ImageTransformer {


    /**
     * Calculates the Region to extract from the intermediate image.
     */



    /**
     * Limit the input value to the specified min and max values.
     */
    int clamp(int min, int max, int input) {
        return Math.min(Math.max(min, input), max);
    }
}
