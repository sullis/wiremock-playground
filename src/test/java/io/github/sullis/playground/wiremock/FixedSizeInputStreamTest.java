package io.github.sullis.playground.wiremock;

import com.github.tomakehurst.wiremock.common.InputStreamSource;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FixedSizeInputStreamTest {
  @Test
  void simple() throws Exception {
    final char c = 'a';
    final int size = 54321;
    InputStreamSource source = StreamSourceSupport.forInputStream(new FixedSizeInputStream(c, size));
    String result = IOUtils.toString(source.getStream(), StandardCharsets.UTF_8);
    assertThat(result).isEqualTo(StringUtils.repeat(c, size));
  }
}
