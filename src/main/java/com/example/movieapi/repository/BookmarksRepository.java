package com.example.movieapi.repository;

import com.example.movieapi.model.Bookmarks;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BookmarksRepository extends R2dbcRepository<Bookmarks, UUID> {

  Mono<Bookmarks> findByUsername(String username);
}
