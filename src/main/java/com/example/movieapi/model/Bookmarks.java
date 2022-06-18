package com.example.movieapi.model;

import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
@Data
@NoArgsConstructor
public class Bookmarks {

  @Id
  private Long id;

  private String username;

  private Set<String> bookmarked;

  public Bookmarks(String username, Set<String> bookmarked) {
    this.username = username;
    this.bookmarked = bookmarked;
  }
}
