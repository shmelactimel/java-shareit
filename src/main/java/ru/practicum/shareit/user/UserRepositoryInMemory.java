package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> storage = new HashMap<>();
    private final Set<String> userEmails = new HashSet<>();
    private Long counter = 1L;

    @Override
    public User getUser(Long id) {
        return storage.get(id);
    }

    @Override
    public List<User> getUsers() {
        return storage.values().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<User> addUser(Long userId, User user) {
        if (userId != null) {
            if (storage.get(userId) != null) {
                return Optional.empty();
            }
            user.setId(userId);
        } else {
            user.setId(counter);
            counter++;
        }
        storage.put(user.getId(), user);
        userEmails.add(user.getEmail());
        return Optional.of(user);
    }

    @Override
    public void updateUser(User updatedUser) {
        storage.put(updatedUser.getId(), updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean doesUserExist(Long userId) {
        return storage.containsKey(userId);
    }
}