package ru.practicum.explore.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.jdi.request.InvalidRequestStateException;
import dto.HitDto;
import dto.StatView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@PropertySource({"classpath:application.properties"})
public class StatClientService {

    private final HttpClient client = HttpClient.newHttpClient();
    @Value("${service-stat.url}")
    private String url;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public StatClientService(@Value("${service-stat.url}") String url) {
        this.url = url;
    }

    public HitDto createHit(HitDto hitDto) {
        log.info("POST request. Create a hit: " + hitDto.toString());
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
            return objectMapper.readValue(response.body(), HitDto.class);
        } catch (Exception e) {
            throw new InvalidRequestStateException(e.getMessage());
        }
    }

    public List<StatView> getAllStats(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris,
                                      Boolean unique) {
        log.info("GET request. Parameters: \n\tstart: {}\n\tend: {}\n\turis: {}\n\tunique: {}",
                start, end, uris, unique);
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
