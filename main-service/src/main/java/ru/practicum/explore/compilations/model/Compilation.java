package ru.practicum.explore.compilations.model;

import lombok.*;
import ru.practicum.explore.event.model.Event;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "compilations")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Title empty. You can't do that")
    @Size(min = 1, max = 50)
    @Column(name = "title")
    private String title;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "compilations_events",
            joinColumns = @JoinColumn(name = "comp_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id")
    )
    List<Event> events;
    @Column(name = "pinned")
    boolean pinned;
}
