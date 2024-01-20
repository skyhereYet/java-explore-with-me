package ru.practicum.explore.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.util.Create;

import javax.validation.constraints.*;
import java.util.Comparator;

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
    private int likes;
    private int dislikes;

    public static final Comparator<UserDto> likesComparator = new Comparator<UserDto>() {
        @Override
        public int compare(UserDto user1, UserDto user2) {
            return (int) (user2.getLikes() - user1.getLikes());
        }
    };
}
