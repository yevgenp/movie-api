package com.example.movieapi.service;

import com.example.movieapi.model.Bookmarks;
import com.example.movieapi.repository.BookmarksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private BookmarksRepository repository;

  @Override
  public Mono<Bookmarks> findByUsername(String username) {
    return repository.findByUsername(username);
  }
}