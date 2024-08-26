package io.github.sullis.playground.wiremock;

import com.github.tomakehurst.wiremock.common.InputStreamSource;
import com.github.tomakehurst.wiremock.common.StreamSources;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static com.github.tomakehurst.wiremock.client.WireMock.get;



public class WiremockTest {
  @RegisterExtension
  static WireMockExtension wiremock = WireMockExtension.newInstance()
      .options(wireMockConfig().dynamicPort())
      .build();

  @Test
  void helloWorld() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      final String body = UUID.randomUUID().toString();
      wiremock.stubFor(get("/").willReturn(ok().withBody(body)));
      URI uri = URI.create(wiremock.url("/"));
      HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      assertThat(response.statusCode()).isEqualTo(200);
      assertThat(response.body()).isEqualTo(body);
    }
  }

  @Test
  void testWithBodyFile() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(get("/").willReturn(ok().withBodyFile("foobar.txt")));
      URI uri = URI.create(wiremock.url("/"));
      HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      assertThat(response.statusCode()).isEqualTo(200);
      assertThat(response.body()).isEqualTo("Hello world.");
    }
  }
}
