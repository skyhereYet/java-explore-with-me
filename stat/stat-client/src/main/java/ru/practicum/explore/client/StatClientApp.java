package ru.practicum.explore.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.jdi.request.InvalidRequestStateException;
import dto.Hit;
import dto.StatView;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
public class StatClientApp {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String url;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StatClientApp(String url) {
        this.url = url;
    }

    public Hit createHit(Hit hitDto) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        URI uri = URI.create(url + "/hit");
        String str = "";
        try {
            str = objectMapper.writeValueAsString(hitDto);
        } catch (Exception e) {
            throw new InvalidRequestStateException(e.getMessage());
        }
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(str);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(body)
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), Hit.class);
        } catch (Exception e) {
            throw new InvalidRequestStateException(e.getMessage());
        }
    }

    public List<StatView> getAllStats(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris,
                                      Boolean unique) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        URI uri = URI.create(url + "/stats?start={start}&end={end}&uris={uris}&unique={unique}");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("start", String.valueOf(start))
                .header("end", String.valueOf(end))
                .header("uris", String.valueOf(uris))
                .header("unique", String.valueOf(unique))
                .GET()
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            TypeReference<List<StatView>> typeRef = new TypeReference<>() {};
            return objectMapper.readValue(response.body(), typeRef);
        } catch (Exception e) {
            throw new InvalidRequestStateException(request.toString());
        }
    }
}
