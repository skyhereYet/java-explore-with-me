package ru.practicum.explore.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.exception.UserExistException;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.user.dto.UserMapper;
import ru.practicum.explore.user.model.QUser;
import ru.practicum.explore.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceDao implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = false)
    public UserDto createOrThrow(UserDto userDto) {
        log.info("Create User: {}", userDto.toString());
        return UserMapper.toUserDto(
                userRepository.save(UserMapper.toUser(userDto))
        );
    }

    @Override
    public List<UserDto> getAllUsers(Integer[] ids, PageRequest pageRequest) {
        if (ids == null) {
            return userRepository.findAll(pageRequest).stream()
                    .map(user -> UserMapper.toUserDto(user))
                    .collect(Collectors.toList());
        }
        return userRepository.findAll(QUser.user.id.in(ids), pageRequest).stream()
                .map(user -> UserMapper.toUserDto(user)).collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = false)
    public UserDto deleteOrThrow(int id) {
        UserDto userDto = UserMapper.toUserDto(
                userRepository.findById(id)
                        .orElseThrow(() -> new UserExistException("User not exist in the repository, ID: "  + id)));
        userRepository.deleteById(id);
        return userDto;
    }
}
