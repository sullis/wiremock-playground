package io.github.sullis.playground.wiremock;

import java.io.IOException;
import java.io.InputStream;


public class FixedSizeInputStream extends InputStream {
  private long count = 0;
  private final char c;
  private final long size;

  public FixedSizeInputStream(char c, long size) {
    if (size < 1) {
      throw new IllegalArgumentException("size=" + size);
    }
    this.c = c;
    this.size = size;
  }

  @Override
  public int read()
      throws IOException {
    if (count >= size) {
      return -1;
    } else {
      count++;
      return c;
    }
  }
}
