package io.geekshop.service.helpers.es;

import org.elasticsearch.common.geo.GeoPoint;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author bo.chen
 * @date 2021/11/19
 **/
public class TopicService extends BaseEsService<Topic> {
    public TopicService(String indexName) {
        super(indexName);
    }

    public static void main(String[] args) throws Exception {
        TopicService topicService = new TopicService("topic");
        Topic topic = Topic.builder().id(UUID.randomUUID().toString())
                .userId(1L)
                .title("测试标题")
                .content("测试内容之-虽然我走得很慢,但我从不后退!")
                .status((short) 1)
                .del(false).location(new GeoPoint("22.541144,113.952953"))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        topicService.saveDoc(topic.getId(),topic);
    }

}
