package ru.practicum.explore.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.util.Create;

import javax.validation.constraints.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDto {
    private int id;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    @Size(groups = {Create.class}, min = 2, max = 250)
    private String name;
    @Email(groups = {Create.class})
    @NotNull(groups = {Create.class})
    @Size(groups = {Create.class}, min = 6, max = 254)
    private String email;
}
