package ru.practicum.explore.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@RequiredArgsConstructor
public class UserShortDto {
        private final int id;
        @NotBlank
        private final String name;
}
