package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<String, User> usersEmail = new HashMap<>();
    private final Map<Long, User> usersId = new HashMap<>();
    private long idCounter;

    public Collection<User> findAll() {
        return usersEmail.values();
    }

    public User create(User user) {
        if (user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (usersEmail.containsKey(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        usersEmail.put(user.getEmail(), user);
        usersId.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        User existUser = usersId.get(user.getId());
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (user.getEmail() != null) {
            if (usersEmail.containsKey(user.getEmail())) {
                if (!existUser.getId().equals(user.getId())) {
                    throw new DuplicatedDataException("Этот имейл уже используется");
                }
            }
            usersEmail.remove(existUser.getEmail());
            existUser.setEmail(user.getEmail());
        }
        if (user.getUsername() != null) {
            existUser.setUsername(user.getUsername());
        }
        if (user.getPassword() != null) {
            existUser.setPassword(user.getPassword());
        }
        usersEmail.put(existUser.getEmail(), existUser);
        usersId.put(existUser.getId(), existUser);
        return existUser;
    }

    public Optional<User> findUserById(long id) {
        return Optional.ofNullable(usersId.get(id));
    }
    private long getNextId() {
        return ++idCounter;
    }
}
