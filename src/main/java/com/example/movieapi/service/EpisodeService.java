package com.example.movieapi.service;

import com.example.movieapi.dto.EpisodeResponse;
import reactor.core.publisher.Mono;

public interface EpisodeService {

  Mono<EpisodeResponse> getEpisodes(String title, Integer season);

  Mono<EpisodeResponse> getBookmarked(String username, String title, Integer season);

  Mono<?> bookmark(String username, String episodeId);
}
