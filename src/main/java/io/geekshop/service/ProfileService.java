package io.geekshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.geekshop.common.Constant;
import io.geekshop.document.ProfileDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author bo.chen
 * @date 2021/10/27
 **/
@Service
@Slf4j
public class ProfileService {
    private RestHighLevelClient client;
    private ObjectMapper objectMapper;

    @Autowired
    public ProfileService(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    /**
     * 创建Profile文档
     * @param document
     * @return
     * @throws Exception
     */
    public String createProfileDocument(ProfileDocument document) throws Exception {
        if (StringUtils.isBlank(document.getId())) {
            UUID uuid = UUID.randomUUID();
            document.setId(uuid.toString());
        } else {
            document.setId(document.getId());
        }

        IndexRequest indexRequest = new IndexRequest(Constant.INDEX).id(document.getId()).source(convertProfileDocumentToMap(document));
        System.out.println("Map ProfileDocument: " + convertProfileDocumentToMap(document));

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse.getResult().name();
    }

    /**
     * 通过主键查询文档
     * @param id
     * @return
     * @throws Exception
     */
    public ProfileDocument findById(String id) throws Exception {
        GetRequest getRequest = new GetRequest(Constant.INDEX).id(id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> resultMap = getResponse.getSource();
        return convertMapToProfileDocument(resultMap);
    }

    /**
     * 修改文档
     * @param document
     * @return
     * @throws Exception
     */
    public String updateProfile(ProfileDocument document) throws Exception {
        ProfileDocument resultDocument = findById(document.getId());
        if (resultDocument == null || StringUtils.isBlank(resultDocument.getId()) ) {
            System.out.println("无法查询到内容，请确定ID是否正确");
            return "";
        }
        UpdateRequest updateRequest = new UpdateRequest(Constant.INDEX, resultDocument.getId());
        updateRequest.doc(convertProfileDocumentToMap(document));
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        return updateResponse
                .getResult()
                .name();
    }

    /**
     * 查找前10的文档列表
     * @return
     * @throws Exception
     */
    public List<ProfileDocument> findAll() throws Exception {
        SearchRequest searchRequest = buildSearchRequest(Constant.INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse);
    }

    /**
     * 根据name模糊查询 {@link ProfileDocument}
     * @param name
     * @return
     * @throws Exception
     */
    public List<ProfileDocument> findProfileByName(String name) throws Exception {
        SearchRequest searchRequest = buildSearchRequest(Constant.INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders
                .matchQuery("name", name)
                .operator(Operator.AND);
        searchSourceBuilder.query(matchQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse);
    }

    /**
     * 根据主键删除文档
     * @param id
     * @return
     * @throws Exception
     */
    public String deleteProfileDocument(String id) throws Exception {
        DeleteRequest deleteRequest = new DeleteRequest(Constant.INDEX).id(id);
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        return response
                .getResult()
                .name();
    }

    /**
     * 对象转换成Map(k-v)
     * @param profileDocument
     * @return
     */
    private Map<String, Object> convertProfileDocumentToMap(ProfileDocument profileDocument) {
        return objectMapper.convertValue(profileDocument, Map.class);
    }

    private ProfileDocument convertMapToProfileDocument(Map<String, Object> map) {
        return objectMapper.convertValue(map, ProfileDocument.class);
    }

    /**
     * 精确查询
     * @param technology
     * @return
     * @throws Exception
     */
    public List<ProfileDocument> searchByTechnology(String technology) throws Exception {
        SearchRequest searchRequest = buildSearchRequest(Constant.INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders
                .boolQuery()
                .must(QueryBuilders
                        .matchQuery("technologies.name", technology));
        searchSourceBuilder.query(QueryBuilders.nestedQuery("technologies", queryBuilder, ScoreMode.Avg));
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(response);
    }

    private List<ProfileDocument> getSearchResult(SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();
        List<ProfileDocument> profileDocuments = new ArrayList<>();
        for (SearchHit hit : searchHit) {
            profileDocuments
                    .add(objectMapper
                            .convertValue(hit
                                    .getSourceAsMap(), ProfileDocument.class));
        }
        return profileDocuments;
    }

    private SearchRequest buildSearchRequest(String index) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        return searchRequest;
    }
}
