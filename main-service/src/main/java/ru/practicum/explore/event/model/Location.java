package ru.practicum.explore.event.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "locations")
@Getter
@Setter
@RequiredArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "lat")
    private float lat;
    @Column(name = "lon")
    private float lon;
}
