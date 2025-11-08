package com.picksy.roomservice.service;

import com.picksy.roomservice.model.PollDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class DecisionClient {
    private final WebClient.Builder webClientBuilder;

    public Flux<PollDTO> getResults(String roomCode) {
        return webClientBuilder.build()
                .get()
                .uri("lb://decision-service/api/decision/room/{roomCode}", roomCode)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Client error: " + errorBody)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Server error: " + errorBody)))
                )
                .bodyToFlux(PollDTO.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(throwable -> {
                    System.err.println("decision-service: " + throwable.getMessage());
                    return Flux.error(new RuntimeException("Decision service unavailable", throwable));
                });
    }
}
