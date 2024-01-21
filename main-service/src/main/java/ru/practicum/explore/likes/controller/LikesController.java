package ru.practicum.explore.likes.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.dto.EventShortDto;
import ru.practicum.explore.likes.dto.LikeDto;
import ru.practicum.explore.likes.dto.NewLikeDto;
import ru.practicum.explore.likes.service.LikesService;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.util.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/likes")
@Validated
@RequiredArgsConstructor
@Slf4j
public class LikesController {

    @Autowired
    private final LikesService likesService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public LikeDto create(@Validated({Create.class}) @RequestBody NewLikeDto newLikeDto) {
        log.info("\nPOST request. Like create: \n\t {}", newLikeDto.toString());
        return likesService.createOrThrow(newLikeDto);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public LikeDto deleteUserById(@PathVariable int id) {
        log.info("\nDELETE request. Delete like by ID: {}", id);
        return likesService.deleteOrThrow(id);
    }

    @GetMapping(value = "/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsLikeSort(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("\nGET request. Get all events like sort");
        return likesService.getEventsLikeSort(PageRequest.of(from / size, size));
    }

    @GetMapping(value = "/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsersLikeSort(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("\nGET request. Get all users like sort");
        return likesService.getUsersLikeSort(PageRequest.of(from / size, size));
    }
}
