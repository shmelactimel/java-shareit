package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.EntityAlreadyExistException;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.getUser(id);
        if (user == null) {
            throw new EntityNotFoundException(String.format("User with id %d does not exist", id));
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.getUsers();
        return UserMapper.toUsersDto(users);
    }

    @Override
    public UserDto addUser(Long userId, UserDto userDto) throws EntityAlreadyExistException {
        String email = userDto.getEmail();
        Optional<User> existingUser = userRepository.getUserByEmail(email);
        if (existingUser.isPresent()) {
            throw new EntityAlreadyExistException(String.format("User with email %s already exists", email));
        }
        User user = UserMapper.toUser(userDto);
        Optional<User> createdUser = userRepository.addUser(userId, user);
        if (createdUser.isEmpty()) {
            throw new EntityAlreadyExistException(String.format("User with id %d already exists", userId));
        }
        return UserMapper.toUserDto(createdUser.get());
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) throws EntityAlreadyExistException {
        User oldUser = userRepository.getUser(id);
        if (oldUser == null) {
            throw new EntityNotFoundException(String.format("User with id %d does not exist", id));
        }
        User user = UserMapper.toUser(userDto);
        String newEmail = user.getEmail();
        Optional<User> existingUser = userRepository.getUserByEmail(newEmail);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new EntityAlreadyExistException(String.format("User with email %s already exists", newEmail));
        }
        User updatedUser = UserMapper.updateUserWithUser(oldUser, user);
        userRepository.updateUser(updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.getUser(id);
        if (user != null) {
            userRepository.deleteUser(id);
        }
    }
}
