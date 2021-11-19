package io.geekshop.service.helpers.es;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 索引管理
 * @author bo.chen
 * @date 2021/11/8
 **/
@Slf4j
@Service
public class EsIndexOperation {

    @Autowired
    // @Qualifier("client")
    private RestHighLevelClient client ;
    private final RequestOptions options = RequestOptions.DEFAULT;

    /**
     * 判断索引是否存在
     */
    public boolean checkIndex (String index) {
        try {
            return client.indices().exists(new GetIndexRequest(index), options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Boolean.FALSE ;
    }

    /**
     * 创建索引
     */
    public boolean createIndex (String indexName , Map<String, Object> columnMap, String aliasName, int shardsNumber, int replicasNumber){
        try {
            if(!checkIndex(indexName)){
                CreateIndexRequest request = new CreateIndexRequest(indexName);
                if (columnMap != null && columnMap.size()>0) {
                    Map<String, Object> source = new HashMap<>(1);
                    source.put("properties", columnMap);
                    request.mapping(source);
                }
                request.settings(Settings.builder()
                        .put("index.number_of_shards", shardsNumber == 0 ? 3 : shardsNumber)
                        .put("index.number_of_replicas", replicasNumber == 0 ? 1 : replicasNumber));
                request.alias(new Alias(aliasName));
                CreateIndexResponse response = this.client.indices().create(request, options);

                boolean acknowledged = response.isAcknowledged();
                boolean shardsAcknowledged = response.isShardsAcknowledged();
                System.out.println("acknowledged: " + acknowledged + ",shardsAcknowledged: " + shardsAcknowledged);
                return Boolean.TRUE ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    /**
     * 删除索引
     */
    public boolean deleteIndex(String indexName) {
        try {
            if(checkIndex(indexName)){
                DeleteIndexRequest request = new DeleteIndexRequest(indexName);
                AcknowledgedResponse response = client.indices().delete(request, options);
                System.out.println("deleteIndex AcknowledgedResponse: " + response.toString());
                return response.isAcknowledged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }


}
