package com.example.movieapi.model;

import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table
@Data
@NoArgsConstructor
public class Bookmarks implements Persistable<UUID> {

  @Id
  private UUID id;

  private String username;

  private Set<String> bookmarked;

  @Transient
  private boolean isNew;

  public Bookmarks(boolean isNew, String username, Set<String> bookmarked) {
    this.username = username;
    this.bookmarked = bookmarked;
    this.isNew = isNew;

    if (isNew) {
      id = UUID.randomUUID();
    }
  }

  @Override
  public boolean isNew() {
    return this.isNew;
  }
}
