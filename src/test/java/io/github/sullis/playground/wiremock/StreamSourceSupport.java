package io.github.sullis.playground.wiremock;

import com.github.tomakehurst.wiremock.common.InputStreamSource;
import java.io.InputStream;


public class StreamSourceSupport {

  public static InputStreamSource forInputStream(final InputStream input) {
    return new InputStreamSource() {
      @Override
      public InputStream getStream() {
        return input;
      }
    };
  }
}
