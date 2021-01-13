/*
 * Copyright (c) 2021 掘艺网络(jueyi.co).
 * All rights reserved.
 */

package io.geekshop.asset;

import lombok.Data;

/**
 * Created on Jan, 2021 by @author bobo
 */
@Data
public class ImageTransformPreset {
    private String name;
    private Integer width;
    private Integer height;
    private ImageTransformMode mode;
}
