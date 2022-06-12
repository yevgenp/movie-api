package com.example.movieapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class EpisodeResponse {

  @JsonProperty("Title")
  private String title;

  @JsonProperty("Season")
  private String season;

  private String totalSeasons;

  @JsonProperty("Episodes")
  private List<Episode> episodes;
}
