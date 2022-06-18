package com.example.movieapi.service;

import com.example.movieapi.dto.EpisodeResponse;
import com.example.movieapi.model.Bookmarks;
import com.example.movieapi.repository.BookmarksRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class EpisodeServiceImpl implements EpisodeService {

  private final WebClient omdbWebClient;
  private final UserService userService;
  private final BookmarksRepository bookmarksRepository;

  @Value("${omdb.url}")
  private String url;

  @Value("${omdb.apikey}")
  private String apiKey;


  @Override
  public Mono<EpisodeResponse> getEpisodes(String title, Integer season) {
    return omdbWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(url)
            .queryParam("apikey", apiKey)
            .queryParam("t", title)
            .queryParam("Season", season)
            .build())
        .retrieve()
        .bodyToMono(EpisodeResponse.class);
        //TODO: Handle error
  }

  @Override
  public Mono<EpisodeResponse> getBookmarked(String username, String title, Integer season) {
    return getEpisodes(title, season)
        .flatMap(resp -> userService.findByUsername(username)
            .map(Bookmarks::getBookmarked)
            .map(bookmarked -> {
              resp.getEpisodes().removeIf(e -> !bookmarked.contains(e.getImdbID()));
              return resp;
            }))
        .defaultIfEmpty(new EpisodeResponse());
  }

  @Transactional
  @Override
  public Mono<?> bookmark(String username, String episodeId) {
    return bookmarksRepository.findByUsername(username)
        .map(b -> {
          //TODO: Validate episodeId exists ?
          b.getBookmarked().add(episodeId);
          return b;
        })
        .flatMap(bookmarksRepository::save)
        .switchIfEmpty(bookmarksRepository.save(new Bookmarks(username, Set.of(episodeId))));

  }
}
