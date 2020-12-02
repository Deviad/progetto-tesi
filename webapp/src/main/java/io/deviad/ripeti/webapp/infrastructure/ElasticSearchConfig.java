//package io.deviad.ripeti.webapp.infrastructure;
//
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.RestClients;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
//
//import javax.annotation.Priority;
//
//@Configuration
//@EnableElasticsearchRepositories(basePackages = "io.deviad.ripeti.webapp.eventstore")
//@ComponentScan(basePackages = { "io.deviad.ripeti.webapp.api.service" })
//public class ElasticSearchConfig {
//
//    @Bean
//    @Primary
//    public RestHighLevelClient client() {
//        ClientConfiguration clientConfiguration
//                = ClientConfiguration.builder()
//                .connectedTo("localhost:9201")
//                .build();
//
//        return RestClients.create(clientConfiguration).rest();
//    }
//
//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate(RestHighLevelClient client) {
//        return new ElasticsearchRestTemplate(client);
//    }
//}
