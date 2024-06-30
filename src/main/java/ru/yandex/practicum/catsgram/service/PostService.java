package ru.yandex.practicum.catsgram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;

@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();

    private final UserService userService;
    private long idCounter;

    @Autowired
    public PostService(UserService userService) {
        this.userService = userService;
    }

    public List<Post> findAll(String sort, int from, int size) {
        List<Post> postsList = new ArrayList<>(posts.values());
        if (from >= postsList.size()) {
            throw new ParameterNotValidException("from", "за границами списка");
        }
        if (SortOrder.from(sort) == SortOrder.ASCENDING) {
            postsList.sort(Comparator.comparing(Post::getPostDate));
        } else if (SortOrder.from(sort) == SortOrder.DESCENDING) {
            postsList.sort((p1, p2) -> p2.getPostDate().compareTo(p1.getPostDate()));
        }
        int to = from + size + 1;
        if (to > postsList.size() - 1) {
            to = postsList.size();
        }
        return postsList.subList(from, to);
    }

    public Optional<Post> findById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public Post create(Post post) {
        if (userService.findById(post.getAuthorId()).isEmpty()) {
            throw new NotFoundException("Автор с id = " + post.getAuthorId() + " не найден");
        }
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        return ++idCounter;
    }
}