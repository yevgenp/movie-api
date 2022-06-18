package com.example.movieapi.controller;

import static com.example.movieapi.TestUtils.readFile;
import static com.example.movieapi.controller.EpisodesController.PATH;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.movieapi.model.Bookmarks;
import com.example.movieapi.repository.BookmarksRepository;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@WireMockTest(httpPort = 1234)
@TestPropertySource(properties = {
    "omdb.secure=false",
    "omdb.url=localhost:1234",
    "omdb.apikey=key"
})
@AutoConfigureWebTestClient
@SpringBootTest
class EpisodesControllerTest {

  @Autowired
  private WebTestClient webClient;

  @Autowired
  private BookmarksRepository bookmarksRepository;

  @BeforeEach
  void setUp() throws Exception {
    String response = readFile("episodes_response.json");
    WireMock.stubFor(get(urlPathEqualTo("/"))
        .withQueryParams(Map.of(
            "apikey", equalTo("key"),
            "t", equalTo("Game of Thrones"),
            "Season", equalTo("3")))
        .willReturn(okJson(response)));
  }

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
    Bookmarks bookmarks = new Bookmarks("user1",
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
    Bookmarks bookmarks = new Bookmarks("user2", Set.of("tt2178782", "tt2178802"));
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