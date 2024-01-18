package ru.practicum.explore.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.explore.event.model.Location;
import ru.practicum.explore.util.Create;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class NewEventDto {
    @Size(groups = {Create.class}, min = 20, max = 2000)
    @NotNull(groups = {Create.class})
    private String annotation;
    @Size(groups = {Create.class}, min = 20, max = 7000)
    @NotNull(groups = {Create.class})
    private String description;
    private Integer category;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private int participantLimit;
    private Boolean requestModeration;
    @Size(groups = {Create.class}, min = 3, max = 120)
    @NotNull(groups = {Create.class})
    private String title;
}
