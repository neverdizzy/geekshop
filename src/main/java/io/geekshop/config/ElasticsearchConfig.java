package io.geekshop.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ElasticSerach 本地Docker启动版本 7.11.2
 * @author bo.chen
 * @date 2021/10/26
 **/

@Configuration
// @ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchConfig {
    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port}")
    private int port;

    @Value("${elasticsearch.username}")
    private String userName;

    @Value("${elasticsearch.password}")
    private String password;

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client() {
        System.out.println("RestHighLevelClient Init Start!!!");
        // Elasticsearch集群需要basic auth验证
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        // 访问用户名和密码为Elasticsearch实例时设置的用户名和密码，也是Kibana控制台的登录用户名和密码
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(userName, password));

        // 通过builder创建rest client，配置http client的HttpClientConfigCallback。
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        // RestHighLevelClient实例通过REST low-level client builder进行构造。
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);
        return restHighLevelClient;
    }
}
