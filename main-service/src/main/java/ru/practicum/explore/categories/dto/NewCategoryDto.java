package ru.practicum.explore.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.util.Create;
import ru.practicum.explore.util.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {
    @NotBlank(groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class, Update.class})
    @Size(groups = {Create.class, Update.class}, min = 2, max = 50)
    private String name;
}
