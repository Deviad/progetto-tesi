package io.deviad.ripeti.client;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Controller
public class ResourceController {

  public static final String BASE_URL =
      "http://localhost:8884/auth/realms/ripeti/protocol/openid-connect";

  @Bean
  public RouterFunction<ServerResponse> htmlRouter() {
    Resource html = new ClassPathResource("react/index.html");
    return route(
        GET("/"), request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(html));
  }

  @Bean
  public RouterFunction<ServerResponse> customOauthRouter() {

    final String authPath = "/auth";
    final String tokenPath = "/token";

    return route(
        path("/custom-oauth/**"),
        request -> {
          if (!request.uri().getHost().equals("localhost")) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "You cannot make this request");
          }

          Mono<ServerResponse> resp = Mono.empty();

          if (request.path().contains(authPath)) {
            validateParam(request, "redirect_uri");
            var redirectUri = request.queryParam("redirect_uri").orElseThrow();

            final Map<String, String> params =
                Map.ofEntries(
                    Map.entry("response_type", "code"),
                    Map.entry("client_id", "ripeti-web"),
                    Map.entry("scope", "openId"),
                    Map.entry("redirect_uri", redirectUri));

            var fullUrl =
                params.keySet().stream()
                    .map(key -> key + "=" + params.get(key))
                    .collect(Collectors.joining("&", BASE_URL + "/auth/?", ""));

            resp =
                Mono.defer(
                    () ->
                        ServerResponse.permanentRedirect(URI.create(fullUrl))
                            .build()
                            .onErrorResume(Mono::error));
          } else if (request.path().contains(tokenPath)) {
            resp = Mono.defer(() -> getAuthToken(request));
          }

          // Folosesc operatorul defer astfel incat operatie ceruta sa fie executata aici.
          // Asa putem sa folosim o strategie diferita in functie de path pentru a crea o cerere
          // prin Webclient.
          return resp.map(x -> x)
              .switchIfEmpty(
                  Mono.error(
                      new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request")))
              .onErrorResume(Mono::error);
        });
  }

  private Mono<ServerResponse> getAuthToken(ServerRequest request) {
    return request
        .formData()
        .flatMap(formData -> Mono.just(buildDataToBeSent(formData)))
        .flatMap(
            fd -> {
              var client = getClientBuilder(BASE_URL + "/token").build();

              final LinkedMultiValueMap<String, String> data =
                  fd.entrySet().stream()
                      .collect(
                          LinkedMultiValueMap::new,
                          (m, e) -> m.put(e.getKey(), new ArrayList<>(List.of(e.getValue()))),
                          LinkedMultiValueMap::putAll);
              return client
                  .post()
                  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                  .accept(MediaType.APPLICATION_JSON)
                  .body(BodyInserters.fromFormData(data))
                  .retrieve()
                  .bodyToMono(String.class)
                  .onErrorResume(Mono::error);
            })
        .flatMap(r -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(r));
  }

  private Map<String, String> buildDataToBeSent(
      org.springframework.util.MultiValueMap<String, String> formData) {
    return Map.ofEntries(
        Map.entry("grant_type", "authorization_code"),
        Map.entry(
            "code",
            Optional.of(formData.get("code"))
                .orElseThrow(
                    () ->
                        new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "missing code request param"))
                .get(0)),
        Map.entry(
            "redirect_uri",
            Optional.of(formData.get("redirect_uri"))
                .orElseThrow(
                    () ->
                        new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "missing code request param"))
                .get(0)),
        Map.entry("client_id", "ripeti-web"),
        Map.entry("client_secret", "b8cc4bab-c1c5-4af4-9456-bec7f81a5bda"));
  }

  private void validateParam(ServerRequest request, String param) {
    request
        .queryParam(param)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Did not provide " + param));
  }

  private WebClient.Builder getClientBuilder(String fullUrl) {
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
        .baseUrl(fullUrl);
  }
}
