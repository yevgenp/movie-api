package com.example.movieapi.service;

import com.example.movieapi.model.Bookmarks;
import reactor.core.publisher.Mono;

public interface UserService {

  Mono<Bookmarks> findByUsername(String username);
}
