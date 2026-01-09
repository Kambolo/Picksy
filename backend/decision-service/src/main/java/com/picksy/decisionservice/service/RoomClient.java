package com.picksy.decisionservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RoomClient {

  private final WebClient.Builder webClientBuilder;

    public Mono<Boolean> isParticipant(String roomCode, Long userId) {
        return webClientBuilder
                .build()
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .scheme("lb")
                                .host("room-service")
                                .path("/api/room/secure/participant/{roomCode}")
                                .queryParam("userId", userId)
                                .build(roomCode)
                )
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody ->
                                        Mono.error(new RuntimeException("Client error: " + errorBody)))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody ->
                                        Mono.error(new RuntimeException("Server error: " + errorBody)))
                )
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(throwable -> {
                    System.err.println("decision-service: " + throwable.getMessage());
                    return Mono.error(
                            new RuntimeException("Decision service unavailable", throwable));
                });
    }

}
