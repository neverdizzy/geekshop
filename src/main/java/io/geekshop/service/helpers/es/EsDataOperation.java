package io.geekshop.service.helpers.es;

import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据管理
 *
 * @author bo.chen
 * @date 2021/11/8
 **/

@Service
public class EsDataOperation {

    @Autowired
    private RestHighLevelClient client;
    private static final RequestOptions options;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // 默认缓存限制为100MB，此处修改为30MB。
        builder.setHttpAsyncResponseConsumerFactory(
                new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024));
        options = builder.build();
    }

    public void createWithJson() {
        IndexRequest request = new IndexRequest();
    }

    /**
     * 写入数据
     */
    public boolean insert(String indexName, String id, Map<String, Object> dataMap) {
        try {
            BulkRequest request = new BulkRequest();
            request.add(new IndexRequest(indexName).id(id)
                    .opType("create").source(dataMap, XContentType.JSON));
            BulkResponse response = this.client.bulk(request, options);
            System.out.println("BulkResponse: " + response.toString());
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    /**
     * 批量写入数据
     */
    public boolean batchInsert(String indexName,  String id, List<Map<String, Object>> dataMapList) {
        try {
            BulkRequest request = new BulkRequest();
            for (Map<String, Object> dataMap : dataMapList) {
                request.add(new IndexRequest(indexName).id(id)
                        .opType("create").source(dataMap, XContentType.JSON));
            }
            BulkResponse response = this.client.bulk(request, options);
            System.out.println("BulkResponse: " + response.toString());

            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    /**
     * 更新数据，可以直接修改索引结构
     */
    public boolean update(String indexName, String id, Map<String, Object> dataMap) {
        try {
            UpdateRequest updateRequest = new UpdateRequest(indexName, id);
            updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
            updateRequest.doc(dataMap);
            UpdateResponse response = this.client.update(updateRequest, options);
            System.out.println("UpdateResponse: " + response.toString());
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    /**
     * 删除数据
     */
    public boolean delete(String indexName, String id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
            DeleteResponse response = this.client.delete(deleteRequest, options);
            System.out.println("DeleteResponse: " + response.toString());
            // return response.getResult().name().equals("DELETED") ? Boolean.TRUE : Boolean.FALSE;
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    /**
     * 通过主键查询文档
     * @param indexName
     * @param id
     * @return
     */
    public boolean findById(String indexName, String id) {
        try {
            GetRequest getRequest = new GetRequest(indexName).id(id);
            GetResponse getResponse = null;
            getResponse = client.get(getRequest, options);

            Map<String, Object> resultMap = getResponse.getSource();
            System.out.println("findById resultMap: " + resultMap.toString());

            return resultMap != null ? true : false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Boolean.FALSE;
    }

    public boolean testIndex() {
        if (true){
            // 索引存在，直接插入数据
            Map<String, Object> message = new HashMap<>();
            message.put("type", "text");
            Map<String, Object> properties = new HashMap<>();
            properties.put("message", message);
            Map<String, Object> mapping = new HashMap<>();
            mapping.put("properties", properties);
            // this.insert(Constant.ES_PRODUCT_ITEM_INDEX, convertProductItemDocumentToMap(item));
        } else {
            {
                CreateIndexRequest request = new CreateIndexRequest("twitter");
                request.settings(Settings.builder()
                        .put("index.number_of_shards", 3)
                        .put("index.number_of_replicas", 2)
                );
                request.mapping(
                        "{\n" +
                                "  \"properties\": {\n" +
                                "    \"message\": {\n" +
                                "      \"type\": \"text\"\n" +
                                "    }\n" +
                                "  }\n" +
                                "}",
                        XContentType.JSON);

                // Map<String, Object> message = new HashMap<>();
                // message.put("type", "text");
                // Map<String, Object> properties = new HashMap<>();
                // properties.put("message", message);
                // Map<String, Object> mapping = new HashMap<>();
                // mapping.put("properties", properties);
                // request.mapping(mapping);

                request.alias(new Alias("twitter_alias").filter(QueryBuilders.termQuery("user", "kimchy")));

                CreateIndexResponse response = null;
                try {
                    response = this.client.indices().create(request, options);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                boolean acknowledged = response.isAcknowledged();
                boolean shardsAcknowledged = response.isShardsAcknowledged();
                System.out.println("acknowledged: " + acknowledged + ",shardsAcknowledged: " + shardsAcknowledged);
            }
        }

        return true;
    }
}
