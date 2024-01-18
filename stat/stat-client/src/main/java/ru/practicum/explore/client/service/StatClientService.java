package ru.practicum.explore.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.HitDto;
import dto.StatView;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
@RequiredArgsConstructor
public class StatClientService {

    @Value("${client.url}")
    private String url;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final WebClient webClient;
    public static final int TIMEOUT = 1000;
    private static final String PATTERN_FOR_DATETIME = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    public StatClientService(@Value("${client.url}") String url, ObjectMapper objectMapper) {
        this.url = url;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                    .responseTimeout(Duration.ofMillis(TIMEOUT))
                    .doOnConnected(conn ->
                            conn.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS))
                                    .addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS)));

        this.webClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultCookie("cookie-name", "cookie-value")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public HitDto createHit(HitDto hitDto) {
        log.info("POST request. Create a hit: " + hitDto.toString());
        return webClient.post()
                .uri("/hit")
                .body(Mono.just(hitDto), HitDto.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatus::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding")))
                .bodyToMono(HitDto.class).block();
    }

    public List<StatView> getAllStats(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris,
                                      Boolean unique) {
        log.info("GET request. Parameters: \n\tstart: {}\n\tend: {}\n\turis: {}\n\tunique: {}",
                start, end, uris, unique);
        String request = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats/")
                        .queryParam("start", start.format(DateTimeFormatter.ofPattern(PATTERN_FOR_DATETIME)))
                        .queryParam("end", end.format(DateTimeFormatter.ofPattern(PATTERN_FOR_DATETIME)))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatus::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding")))
                .bodyToMono(String.class)
                .block();
        try {
            return objectMapper.readValue(request, new TypeReference<List<StatView>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
