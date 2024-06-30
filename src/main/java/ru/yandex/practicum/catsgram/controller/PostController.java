package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public Collection<Post> findAll(@RequestParam(defaultValue = "desc") String sort,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
          if (!sort.equals("desc")) {
              if (!sort.equals("asc")) {
                throw new ParameterNotValidException("sort",
                        "Неккоректный параметр сортировки. Актуальные значения: desc, asc");
              }
          }
          if (size <= 0) {
              throw new ParameterNotValidException("size",
                      "Некорректный размер выборки. Размер должен быть больше нуля");
          }
          if (from < 0) {
              throw new ParameterNotValidException("from",
                      "Некорректный идентификатор поста. Индентификатор не может быть отрицательным числом");
          }
        return postService.findAll(sort, from, size);
    }

    @GetMapping("/{postId}")
    public Optional<Post> findById(@PathVariable long postId) {
        return postService.findById(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}