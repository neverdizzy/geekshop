package io.geekshop.document;

import lombok.Data;

/**
 * @author bo.chen
 * @date 2021/10/27
 **/
@Data
public class ProfileDocument {
    /**
     * 主键
     */
    private String id;

    /**
     * 名称
     */
    private String name;
}
