package io.github.sullis.playground.wiremock;

import com.github.tomakehurst.wiremock.common.InputStreamSource;
import java.io.InputStream;


public class StreamSourceSupport {

  public static InputStreamSource forFixedSizeInputStream(char c, long size) {
    return new InputStreamSource() {
      @Override
      public InputStream getStream() {
        return new FixedSizeInputStream(c, size);
      }
    };
  }
}
