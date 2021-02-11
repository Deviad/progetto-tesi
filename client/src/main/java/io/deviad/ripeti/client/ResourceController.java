package io.deviad.ripeti.client;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Controller
public class ResourceController {

    @Bean
    public RouterFunction<ServerResponse> htmlRouter() {
        Resource html = new ClassPathResource("react/index.html");
        return route(GET("/"), request
                -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(html)
        );
    }
}
