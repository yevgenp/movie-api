package com.example.movieapi.controller;

import static com.example.movieapi.TestUtils.readFile;
import static com.example.movieapi.controller.EpisodesController.PATH;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.movieapi.model.Bookmarks;
import com.example.movieapi.repository.BookmarksRepository;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
@SpringBootTest
class EpisodesControllerTest {

  @Autowired
  private WebTestClient webClient;

  @Autowired
  private BookmarksRepository bookmarksRepository;

  @Test
  void getEpisodesForSeason() throws Exception {
    webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(PATH)
            .queryParam("title", "Game of Thrones")
            .queryParam("season", "3")
            .build())
        .exchange()
        .expectStatus().isOk()
        .expectBody().json(readFile("episodes_response.json"));
  }

  @WithMockUser(username = "user1")
  @Test
  void getBookmarked() throws Exception {
    //given
    Bookmarks bookmarks = new Bookmarks(true, "user1",
        Set.of("tt2178782", "tt2178802", "tt2178784"));
    bookmarks = bookmarksRepository.save(bookmarks).block();
    //when
    webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(PATH + "/bookmarked")
            .queryParam("title", "Game of Thrones")
            .queryParam("season", "3")
            .build())
        .exchange()
        .expectStatus().isOk()
        .expectBody().json(readFile("bookmarked_response.json"));
  }

  @WithMockUser(username = "no_bookmarks")
  @Test
  void getBookmarkedEmpty() throws Exception {
    //when
    webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(PATH + "/bookmarked")
            .queryParam("title", "Game of Thrones")
            .queryParam("season", "3")
            .build())
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .json("{\"totalSeasons\":null,\"Title\":null,\"Season\":null,\"Episodes\":null}");
  }

  @WithMockUser(username = "user2")
  @Test
  void bookmarkSuccess() throws Exception {
    //given
    Bookmarks bookmarks = new Bookmarks(true, "user2", Set.of("tt2178782", "tt2178802"));
    bookmarks = bookmarksRepository.save(bookmarks).block();
    //when
    webClient.post()
        .uri(uriBuilder -> uriBuilder
            .path(PATH + "/bookmarked")
            .queryParam("id", "tt2178784")
            .build())
        .exchange()
        .expectStatus().isCreated();
    //then
    bookmarks = bookmarksRepository.findById(bookmarks.getId()).block();
    assertThat(bookmarks.getBookmarked())
        .containsExactlyInAnyOrder("tt2178782", "tt2178802", "tt2178784");
  }

  @WithMockUser(username = "first")
  @Test
  void bookmarkFirstTime() throws Exception {
    //when
    webClient.post()
        .uri(uriBuilder -> uriBuilder
            .path(PATH + "/bookmarked")
            .queryParam("id", "tt2178784")
            .build())
        .exchange()
        .expectStatus().isCreated();
    //then
    Bookmarks bookmarks = bookmarksRepository.findByUsername("first").block();
    assertThat(bookmarks.getBookmarked())
        .containsExactlyInAnyOrder("tt2178784");
  }

  @Test
  void endpointsRequireAuthentication() throws Exception {
    //when
    webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(PATH + "/bookmarked")
            .build())
        .exchange()
        .expectStatus().isUnauthorized();
    //when
    webClient.post()
        .uri(uriBuilder -> uriBuilder
            .path(PATH + "/bookmarked")
            .build())
        .exchange()
        .expectStatus().isUnauthorized();
  }

}