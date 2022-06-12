package com.example.movieapi.config;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class OmdbClientConfig {

  public static final int TIMEOUT = 5000;

  @Value("${omdb.url}")
  private String url;

  @Bean
  public WebClient omdbWebClient() {
    var httpClient =
        HttpClient.create()
            .secure()
            .option(CONNECT_TIMEOUT_MILLIS, TIMEOUT)
            .compress(true)
            .doOnConnected(
                connection -> {
                  connection.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
                  connection.addHandlerLast(
                      new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
                });
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }
}
