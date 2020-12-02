package io.deviad.ripeti.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootConfiguration
@ComponentScan(basePackages = "io.deviad.ripeti.webapp")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableElasticsearchRepositories(basePackages = "io.deviad.ripeti.webapp.eventstore")
public class WebappApplication {


    public static void main(String[] args) {
        SpringApplication.run(WebappApplication.class, args);
    }


}
