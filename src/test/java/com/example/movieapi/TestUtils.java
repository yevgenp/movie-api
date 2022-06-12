package com.example.movieapi;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {

  static public String readFile(String path) throws IOException, URISyntaxException {
    return Files.readString(Path.of(TestUtils.class.getClassLoader().getResource(path).toURI()));
  }
}
