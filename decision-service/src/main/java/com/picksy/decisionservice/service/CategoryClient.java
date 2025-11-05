package com.picksy.decisionservice.service;

import com.picksy.decisionservice.model.OptionDTO;
import com.picksy.decisionservice.util.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CategoryClient {

    private final WebClient.Builder webClientBuilder;

    public Flux<OptionDTO> getOptionsByCategory(Long categoryId) {
        return webClientBuilder.build()
                .get()
                .uri("lb://category-service/api/option/public/{catId}", categoryId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Client error: " + errorBody)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Server error: " + errorBody)))
                )
                .bodyToFlux(OptionDTO.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(throwable -> {
                    System.err.println("Błąd pobierania z category-service: " + throwable.getMessage());
                    return Flux.error(new RuntimeException("Category service unavailable", throwable));
                });
    }

    public Mono<CategoryType> getCategoryType(Long categoryId) {
        return webClientBuilder.build()
                .get()
                .uri("lb://category-service/api/category/public/{catId}/type", categoryId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new RuntimeException("Client Error: " + response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException("Server Error: " + response.statusCode())))
                .bodyToMono(CategoryType.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(throwable -> {
                    System.err.println("Błąd pobierania z category-service: " + throwable.getMessage());
                    return Mono.error(new RuntimeException("Category service unavailable", throwable));
                });

    }
}
