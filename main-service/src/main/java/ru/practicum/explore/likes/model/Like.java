package ru.practicum.explore.likes.model;


import lombok.*;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.user.model.User;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "likes")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "is_Like")
    private boolean like;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "like_owner_id", nullable = false)
    private User likeOwner;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
