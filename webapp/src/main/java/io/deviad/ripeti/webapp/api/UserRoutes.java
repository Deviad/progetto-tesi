package io.deviad.ripeti.webapp.api;

import io.deviad.ripeti.webapp.api.command.RegistrationRequest;
import io.deviad.ripeti.webapp.api.service.UserWriteService;
import io.deviad.ripeti.webapp.domain.event.command.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
@Configuration
@ComponentScan(basePackageClasses = {UserWriteService.class})
public class UserRoutes {

   @Autowired
   @Lazy
   UserWriteService userWriteService;

   @Bean
   @Lazy
   public RouterFunction<ServerResponse> routes() {
       return route()
                .POST("/api/user/register", this::handleRegistration).build();
    }

   public Mono<ServerResponse> handleRegistration(ServerRequest request) {

     return request.bodyToMono(RegistrationRequest.class)
               .doOnNext(r-> userWriteService.register(new Register(r.username(), r.password(), r.firstName(), r.lastName(), r.address())))
               .flatMap(r->ServerResponse.ok().build());
    }

}
