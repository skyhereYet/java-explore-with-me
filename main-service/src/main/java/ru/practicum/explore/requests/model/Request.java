package ru.practicum.explore.requests.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private LocalDateTime created;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne (fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "user_id", nullable = false)
    private User requester;
    @Enumerated(EnumType.STRING)
    private StateRequest status;
}
