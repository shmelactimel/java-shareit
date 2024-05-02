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
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final Set<String> userEmails = new HashSet<>();

    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.getUser(id);
        if (user == null) {
            throw new EntityNotFoundException(String.format("user id: %d ,do not exist", id));
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
        if (userEmails.contains(email)) {
            throw new EntityAlreadyExistException(String.format("User with email %s already exists", email));
        }
        User user = UserMapper.toUser(userDto);
        userEmails.add(email);
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
        if (!oldUser.getEmail().equals(newEmail)) {
            if (userEmails.contains(newEmail)) {
                throw new EntityAlreadyExistException(String.format("User with email %s already exists", newEmail));
            }
            userEmails.remove(oldUser.getEmail());
            userEmails.add(newEmail);
        }
        User updatedUser = UserMapper.updateUserWithUser(oldUser, user);
        userRepository.updateUser(updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.getUser(id);
        if (user != null) {
            userEmails.remove(user.getEmail());
            userRepository.deleteUser(id);
        }
    }
}