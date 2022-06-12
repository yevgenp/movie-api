package com.example.movieapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Episode {

  @JsonProperty("Episode")
  private String episode;

  @JsonProperty("Released")
  private String released;

  @JsonProperty("Title")
  private String title;

  private String imdbID;

  private String imdbRating;
}
