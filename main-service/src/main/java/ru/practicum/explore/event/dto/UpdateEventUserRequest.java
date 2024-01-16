package ru.practicum.explore.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.explore.util.Create;
import ru.practicum.explore.util.Update;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class UpdateEventUserRequest {
    @Size(groups = {Create.class, Update.class}, min = 20, max = 2000)
    private String annotation;
    @Size(groups = {Create.class, Update.class}, min = 20, max = 7000)
    private String description;
    private Integer category;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    private Boolean requestModeration;
    private Integer participantLimit;
    private StateAction stateAction;
    @Size(groups = {Create.class, Update.class}, min = 3, max = 120)
    private String title;
}
