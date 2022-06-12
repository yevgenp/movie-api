package com.example.movieapi.controller;

import static com.example.movieapi.controller.EpisodesController.PATH;
import static org.springframework.http.HttpStatus.CREATED;

import com.example.movieapi.dto.EpisodeResponse;
import com.example.movieapi.service.EpisodeService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping(PATH)
@RestController
@RequiredArgsConstructor
public class EpisodesController {

  public static final String PATH = "/episodes";
  private final EpisodeService episodeService;

  @GetMapping
  Mono<EpisodeResponse> getEpisodesForSeason(@RequestParam String title,
      @RequestParam(required = false, defaultValue = "1") Integer season) {
    return episodeService.getEpisodes(title, season);
  }

  @GetMapping("bookmarked")
  Mono<EpisodeResponse> getBookmarked(Principal principal,
      @RequestParam String title,
      @RequestParam Integer season) {
    return episodeService.getBookmarked(principal.getName(), title, season);
  }

  @PostMapping("bookmarked")
  Mono<ResponseEntity<?>> bookmark(Principal principal, @RequestParam String id) {
    return episodeService.bookmark(principal.getName(), id)
        .map(response -> ResponseEntity.status(CREATED).build());
  }

}
