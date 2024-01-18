package ru.practicum.explore.event.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
public class LocationDto {
    private float lat;
    private float lon;
}
