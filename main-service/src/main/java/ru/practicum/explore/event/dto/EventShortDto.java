package ru.practicum.explore.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.categories.dto.CategoryDto;
import ru.practicum.explore.user.dto.UserShortDto;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Comparator;

@Getter
@Setter
@RequiredArgsConstructor
public class EventShortDto {
    private int id;
    @Size(min = 20, max = 2000)
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private String title;
    private boolean paid;
    private long views;

    public static final Comparator<EventShortDto> viewsComparator = new Comparator<EventShortDto>() {
        @Override
        public int compare(EventShortDto event1, EventShortDto event2) {
            return (int) (event1.getViews() - event2.getViews());
        }
    };

    public static final Comparator<EventShortDto> dateComparator = new Comparator<EventShortDto>() {
        @Override
        public int compare(EventShortDto event1, EventShortDto event2) {
            return event1.getEventDate().compareTo(event2.getEventDate());
        }
    };
}
