package io.geekshop.service.helpers.es;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 操作ES 基础类
 * @author bo.chen
 * @date 2021/11/19
 **/

@Slf4j
public abstract class BaseEsService<T> {
    /**
     * 索引名称
     */
    private String indexName;
    private BaseEsService(){}

    protected BaseEsService(String indexName) {
        this.indexName = indexName;
    }

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     */
    public boolean createIndex(T t) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 5) // 分片
                //副本,单机相同shard不允许同时分配到一个节点上，单机的话0,以副本分片数的最大值是 n -1（其中 n 为节点数）。
                .put("index.number_of_replicas", 0)
                .put("refresh_interval", "10s")
        );
        Map<String, Object> properties = EsUtils.getProperties(t);
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);
        request.mapping(mapping);
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        return createIndexResponse.isAcknowledged();
    }

    /**
     * 删除索引
     *
     * @return
     */
    public boolean delIndex() throws IOException {
        if (existsIndex()) {
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            AcknowledgedResponse deleteIndexResponse = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
            return deleteIndexResponse.isAcknowledged();
        }
        return true;
    }

    /**
     * 判断索引是否存在
     *
     * @return
     */
    public boolean existsIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 添加文档
     *
     * @param id
     * @param data
     * @return
     */
    public boolean saveDoc(String id, T data) throws IOException {
        IndexRequest indexRequest = new IndexRequest(indexName).id(id).source(JSON.toJSONString(data, FastJsonHumpSerialize.getSerializeConfig()), XContentType.JSON);
        indexRequest.opType(DocWriteRequest.OpType.CREATE);
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        return true;
    }

    /**
     * 批量新增
     *
     * @param list
     * @return
     * @throws IOException
     */
    public boolean batchSaveDoc(List<T> list) throws IOException {
        if (list == null || list.size() < 1) return false;
        BulkRequest request = new BulkRequest();
        for (T t : list) {
            IndexRequest indexRequest = new IndexRequest(indexName).id(getTId(t)).source(JSON.toJSONString(t, FastJsonHumpSerialize.getSerializeConfig()), XContentType.JSON);
            request.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    public boolean saveDocByMap(String id, Map<String, Object> docMap) throws IOException {
        IndexRequest indexRequest = new IndexRequest(indexName).id(id).source(docMap);
        indexRequest.opType(DocWriteRequest.OpType.CREATE);
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        return true;
    }

    /**
     * 判断文档是否存在
     *
     * @param id
     * @return
     */
    public boolean existsDoc(String id) throws IOException {
        GetRequest request = new GetRequest(indexName, id);
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        return restHighLevelClient.exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 删除文档
     *
     * @param id
     * @return
     */
    public boolean delDocById(String id) throws IOException {
        DeleteResponse delete = restHighLevelClient.delete(new DeleteRequest(indexName, id), RequestOptions.DEFAULT);
        return DocWriteResponse.Result.DELETED.equals(delete.getResult());
    }

    /**
     * 批量删除文档
     *
     * @param ids id数组
     * @return
     * @throws IOException
     */
    public boolean batchDelDocById(String... ids) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        if (ids != null && ids.length > 0) {
            for (String id : ids) {
                if (id == null || "".equals(id.trim())) continue;
                bulkRequest.add(new DeleteRequest(indexName, id));
            }
            BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            return !bulk.hasFailures();
        }
        return false;
    }

    /**
     * 更新文档
     *
     * @param id
     * @param data
     * @return
     */
    public boolean updateDocById(String id, T data) throws IOException {
        UpdateRequest request = new UpdateRequest(indexName, id).doc(JSON.toJSONString(data, FastJsonHumpSerialize.getSerializeConfig()), XContentType.JSON);
        UpdateResponse update = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        return true;
    }

    /**
     * Script更新数字字段
     * @param id
     * @param fieldName
     * @param oprateNum
     * @return
     * @throws IOException
     */
    public boolean updateFieldNum(String id,String fieldName,int oprateNum) throws IOException {
        UpdateRequest request = new UpdateRequest(this.indexName,id).script(new Script("ctx._source."+fieldName+" += "+oprateNum));
        UpdateResponse update = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        return update.getShardInfo().getFailed()==0;
    }

    /**
     * 批量更新
     *
     * @param list
     * @return
     */
    public boolean batchUpdateDoc(List<T> list) throws IOException {
        if (list == null || list.size() < 1) return false;
        BulkRequest request = new BulkRequest();
        for (T t : list) {
            UpdateRequest updateRequest = new UpdateRequest(indexName, getTId(t)).doc(JSON.toJSONString(t, FastJsonHumpSerialize.getSerializeConfig()), XContentType.JSON);
            request.add(updateRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }


    /**
     * 获取文档
     *
     * @param id
     * @return
     */
    public Map<String, Object> getDocMapById(String id) throws IOException {
        GetRequest request = new GetRequest(indexName, id);
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        Map<String, Object> docMap = response.getSourceAsMap();
        return docMap;
    }

    /**
     * 获取文档
     *
     * @param id
     * @return
     */
    public T getDocById(String id) throws IOException {
        GetRequest request = new GetRequest(indexName, id);
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        T result = JSON.parseObject(response.getSourceAsString(), getTClass(), FastJsonHumpSerialize.getParserConfig());
        return result;
    }

    /**
     * 所有数据
     *
     * @param size 指定大小,默认10个
     * @return
     * @throws IOException
     */
    public List<T> searchAll(int size) throws IOException {
        List<T> results = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(size);
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchResponse response = excuteSearch(searchRequest, searchSourceBuilder);
        addItem(results, response.getHits().getHits());
        return results;
    }

    /**
     * 获取Id 列表
     *
     * @param ids
     * @return
     * @throws IOException
     */
    public List<T> searchIds(String... ids) throws IOException {
        List<T> results = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.idsQuery().addIds(ids));
        SearchRequest searchRequest = new SearchRequest().indices(indexName).source(searchSourceBuilder);
        SearchResponse response = excuteSearch(searchRequest, searchSourceBuilder);
        addItem(results, response.getHits().getHits());
        return results;
    }

    /**
     * 精确匹配相当于MySQL的 =,只有key 为keyword类型才可
     *
     * @param key
     * @param value
     * @throws IOException
     */
    public List<T> searchByTerm(String key, Object... value) throws IOException {
        List<T> results = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termsQuery(key, value));//这里注意下term 和 terms
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchResponse response = excuteSearch(searchRequest, searchSourceBuilder);
        addItem(results, response.getHits().getHits());
        return results;
    }

    /**
     * 以点为圆心，disance范围查询
     * @param field 字段，类型是geo_point
     * @param distance 距离
     * @param lat 纬度
     * @param lon 经度
     * @return
     * @throws IOException
     */
    public List<T> searchGeo(String field, String distance,double lat,double lon) throws IOException {
        List<T> results = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //以某点为中心，搜索指定范围
        searchSourceBuilder.query(QueryBuilders.geoDistanceQuery(field)
                .point(lat,lon).distance(distance, DistanceUnit.KILOMETERS));//distance km
//        GeoDistanceSortBuilder geoDistanceSort = SortBuilders.geoDistanceSort("location", new GeoPoint(dto.getLat(), dto.getLon()))
//                .order(SortOrder.ASC).geoDistance(GeoDistance.ARC).unit(DistanceUnit.KILOMETERS);
//        if(StrUtil.isNotEmpty(dto.getUnit())){
//            geoDistanceSort.unit(DistanceUnit.parseUnit(dto.getUnit(),DistanceUnit.KILOMETERS));
//        }
//        searchSourceBuilder.sort(geoDistanceSort);
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchResponse response = excuteSearch(searchRequest, searchSourceBuilder);
        addItem(results, response.getHits().getHits());
        return results;
    }

    private void addItem(List<T> results, SearchHit[] hits) {
        if (hits != null && hits.length > 0) {
            for (SearchHit hit : hits) {
                T item = JSON.parseObject(hit.getSourceAsString(), getTClass(), FastJsonHumpSerialize.getParserConfig());
                results.add(item);
            }
        }
    }

    /**
     * 这个主要是为了让子类自实现其他复杂的查询
     * 还有就是查询器公共配置
     *
     * @param request
     * @param sourceBuilder
     * @return
     * @throws IOException
     */
    public SearchResponse excuteSearch(SearchRequest request, SearchSourceBuilder sourceBuilder) throws IOException {
        // 设置超时
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // 按查询评分降序 排序支持四种：Field-, Score-, GeoDistance-, ScriptSortBuilder
        // 默认的评分排序
        sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        // 自定义脚本排序
        Script script = new Script("doc['comment_num'].value + doc['praise_num'].value");
        sourceBuilder.sort(SortBuilders.scriptSort(script, ScriptSortBuilder.ScriptSortType.NUMBER).order(SortOrder.DESC));
        // 字段排序
        sourceBuilder.sort(new FieldSortBuilder("create_date").order(SortOrder.DESC));
        // 设置查询器
        request.source(sourceBuilder);
        return restHighLevelClient.search(request, RequestOptions.DEFAULT);
    }

    /**
     * 获取text分词结果
     * @param text
     * @return
     * @throws IOException
     */
    public List<AnalyzeResponse.AnalyzeToken> getAnalyzeToken(String...text) throws IOException {
//        AnalyzeRequest request = AnalyzeRequest.withField(this.indexName,"content",text);
        AnalyzeRequest request = AnalyzeRequest.withIndexAnalyzer(this.indexName,"ik_max_word",text);
        AnalyzeResponse analyze = this.restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);
        return analyze.getTokens();
    }
    public String getAnalyzeText(String...text) throws IOException {
        AnalyzeRequest request = AnalyzeRequest.withIndexAnalyzer(this.indexName,"ik_max_word",text);
        AnalyzeResponse analyze = this.restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);
        StringBuilder reslt = new StringBuilder();
        analyze.getTokens().forEach(i->reslt.append(i.getTerm()).append(","));
        return reslt.toString().substring(0,reslt.toString().length()-1);
    }

    /**
     * 设置高亮
     * @param sourceBuilder
     * @param field 字段名称
     */
    public void setHighLigt(SearchSourceBuilder sourceBuilder,String field){
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field contentField = new HighlightBuilder.Field(field);
        contentField.preTags("<em1>","<em2>");
        contentField.postTags("</em1>","</em2>");
        //unified、plain、fvh。默认unified
        contentField.highlighterType("plain");
        highlightBuilder.field(contentField);
        sourceBuilder.highlighter(highlightBuilder);
    }

    /**
     * 获取T 的class
     *
     * @return
     */
    private Class<T> getTClass() {
        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }
    /**
     * 反射获取泛型对象ID
     *
     * @param t
     * @return
     */
    public String getTId(T t) {
        try {
            Class<? extends Object> tClass = t.getClass();
            //整合出 getId() 属性这个方法
            Method m = tClass.getMethod("getId");
            //调用这个整合出来的get方法，强转成自己需要的类型
            Object id = m.invoke(t);
            return id.toString();
        } catch (Exception e) {
            log.info("没有这个属性");
            return null;
        }
    }
}
