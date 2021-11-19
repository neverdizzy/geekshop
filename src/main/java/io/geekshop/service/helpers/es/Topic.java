package io.geekshop.service.helpers.es;

import lombok.Builder;
import lombok.Data;
import org.elasticsearch.common.geo.GeoPoint;
import org.joda.time.DateTime;

import javax.xml.stream.Location;
import java.time.LocalDateTime;

/**
 * @author bo.chen
 * @date 2021/11/19
 **/
@Data
@Builder
public class Topic {
    /**
     * 主键
     */
    private String id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 状态
     */
    private short status;
    /**
     * 是否删除
     */
    private Boolean del;

    /**
     * 经纬度
     */
    private GeoPoint location;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
