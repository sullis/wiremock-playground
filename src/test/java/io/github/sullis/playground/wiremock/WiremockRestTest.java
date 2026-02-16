package io.github.sullis.playground.wiremock;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive WireMock REST API test coverage.
 * Tests various HTTP methods, request/response features, and stubbing capabilities.
 */
public class WiremockRestTest {
  
  @RegisterExtension
  static WireMockExtension wiremock = WireMockExtension.newInstance()
      .options(wireMockConfig().dynamicPort())
      .build();

  @Test
  void testPostWithJsonBody() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      String requestBody = "{\"name\":\"John\",\"age\":30}";
      String responseBody = "{\"id\":123,\"status\":\"created\"}";
      
      wiremock.stubFor(post("/users")
          .withHeader("Content-Type", equalTo("application/json"))
          .withRequestBody(equalToJson(requestBody))
          .willReturn(created()
              .withHeader("Content-Type", "application/json")
              .withBody(responseBody)));
      
      URI uri = URI.create(wiremock.url("/users"));
      HttpRequest request = HttpRequest.newBuilder()
          .POST(HttpRequest.BodyPublishers.ofString(requestBody))
          .uri(uri)
          .header("Content-Type", "application/json")
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(201);
      assertThat(response.body()).isEqualTo(responseBody);
      assertThat(response.headers().firstValue("Content-Type")).hasValue("application/json");
    }
  }

  @Test
  void testPutWithRequestBodyMatching() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      String requestBody = "{\"name\":\"Jane\",\"age\":25}";
      
      wiremock.stubFor(put("/users/123")
          .withRequestBody(containing("Jane"))
          .willReturn(ok()
              .withBody("{\"id\":123,\"status\":\"updated\"}")));
      
      URI uri = URI.create(wiremock.url("/users/123"));
      HttpRequest request = HttpRequest.newBuilder()
          .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
          .uri(uri)
          .header("Content-Type", "application/json")
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(200);
      assertThat(response.body()).contains("updated");
    }
  }

  @Test
  void testDeleteRequest() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(delete("/users/123")
          .willReturn(noContent()));
      
      URI uri = URI.create(wiremock.url("/users/123"));
      HttpRequest request = HttpRequest.newBuilder()
          .DELETE()
          .uri(uri)
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(204);
    }
  }

  @Test
  void testPatchRequest() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      String patchBody = "{\"age\":26}";
      
      wiremock.stubFor(patch("/users/123")
          .withRequestBody(equalToJson(patchBody))
          .willReturn(ok()
              .withBody("{\"id\":123,\"age\":26}")));
      
      URI uri = URI.create(wiremock.url("/users/123"));
      HttpRequest request = HttpRequest.newBuilder()
          .method("PATCH", HttpRequest.BodyPublishers.ofString(patchBody))
          .uri(uri)
          .header("Content-Type", "application/json")
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(200);
      assertThat(response.body()).contains("\"age\":26");
    }
  }

  @Test
  void testRequestHeadersMatching() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(get("/secure")
          .withHeader("Authorization", equalTo("Bearer secret-token"))
          .willReturn(ok()
              .withBody("Authorized")));
      
      URI uri = URI.create(wiremock.url("/secure"));
      HttpRequest request = HttpRequest.newBuilder()
          .GET()
          .uri(uri)
          .header("Authorization", "Bearer secret-token")
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(200);
      assertThat(response.body()).isEqualTo("Authorized");
    }
  }

  @Test
  void testQueryParameters() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(get(urlPathEqualTo("/search"))
          .withQueryParam("q", equalTo("wiremock"))
          .withQueryParam("page", equalTo("1"))
          .willReturn(ok()
              .withBody("{\"results\":[\"result1\",\"result2\"]}")));
      
      URI uri = URI.create(wiremock.url("/search?q=wiremock&page=1"));
      HttpRequest request = HttpRequest.newBuilder()
          .GET()
          .uri(uri)
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(200);
      assertThat(response.body()).contains("result1");
    }
  }

  @Test
  void testNotFoundResponse() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(get("/nonexistent")
          .willReturn(notFound()
              .withBody("{\"error\":\"Resource not found\"}")));
      
      URI uri = URI.create(wiremock.url("/nonexistent"));
      HttpRequest request = HttpRequest.newBuilder()
          .GET()
          .uri(uri)
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(404);
      assertThat(response.body()).contains("not found");
    }
  }

  @Test
  void testServerErrorResponse() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(get("/error")
          .willReturn(serverError()
              .withBody("{\"error\":\"Internal server error\"}")));
      
      URI uri = URI.create(wiremock.url("/error"));
      HttpRequest request = HttpRequest.newBuilder()
          .GET()
          .uri(uri)
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(500);
      assertThat(response.body()).contains("Internal server error");
    }
  }

  @Test
  void testBadRequestResponse() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(post("/users")
          .willReturn(badRequest()
              .withBody("{\"error\":\"Invalid request\"}")));
      
      URI uri = URI.create(wiremock.url("/users"));
      HttpRequest request = HttpRequest.newBuilder()
          .POST(HttpRequest.BodyPublishers.ofString("{}"))
          .uri(uri)
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(400);
      assertThat(response.body()).contains("Invalid request");
    }
  }

  @Test
  void testResponseWithCustomHeaders() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(get("/custom-headers")
          .willReturn(ok()
              .withHeader("X-Custom-Header", "CustomValue")
              .withHeader("X-Request-Id", "12345")
              .withBody("Response with custom headers")));
      
      URI uri = URI.create(wiremock.url("/custom-headers"));
      HttpRequest request = HttpRequest.newBuilder()
          .GET()
          .uri(uri)
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(200);
      assertThat(response.headers().firstValue("X-Custom-Header")).hasValue("CustomValue");
      assertThat(response.headers().firstValue("X-Request-Id")).hasValue("12345");
    }
  }

  @Test
  void testUrlPathMatching() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(get(urlPathMatching("/api/users/[0-9]+"))
          .willReturn(ok()
              .withBody("{\"id\":123,\"name\":\"John\"}")));
      
      URI uri = URI.create(wiremock.url("/api/users/123"));
      HttpRequest request = HttpRequest.newBuilder()
          .GET()
          .uri(uri)
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(200);
      assertThat(response.body()).contains("\"id\":123");
    }
  }

  @Test
  void testRequestVerification() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(post("/events")
          .willReturn(created()));
      
      URI uri = URI.create(wiremock.url("/events"));
      HttpRequest request = HttpRequest.newBuilder()
          .POST(HttpRequest.BodyPublishers.ofString("{\"event\":\"test\"}"))
          .uri(uri)
          .header("Content-Type", "application/json")
          .build();
      
      httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      // Verify the request was received
      wiremock.verify(postRequestedFor(urlEqualTo("/events"))
          .withHeader("Content-Type", equalTo("application/json")));
    }
  }

  @Test
  void testMultipleRequestsToSameEndpoint() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(get("/counter")
          .willReturn(ok()
              .withBody("Counter response")));
      
      URI uri = URI.create(wiremock.url("/counter"));
      HttpRequest request = HttpRequest.newBuilder()
          .GET()
          .uri(uri)
          .build();
      
      // Make multiple requests
      httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      // Verify exactly 3 requests were made
      wiremock.verify(3, getRequestedFor(urlEqualTo("/counter")));
    }
  }

  @Test
  void testJsonPathMatching() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(post("/validate")
          .withRequestBody(matchingJsonPath("$.email"))
          .withRequestBody(matchingJsonPath("$.name"))
          .willReturn(ok()
              .withBody("{\"valid\":true}")));
      
      String requestBody = "{\"email\":\"test@example.com\",\"name\":\"Test User\"}";
      URI uri = URI.create(wiremock.url("/validate"));
      HttpRequest request = HttpRequest.newBuilder()
          .POST(HttpRequest.BodyPublishers.ofString(requestBody))
          .uri(uri)
          .header("Content-Type", "application/json")
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(200);
      assertThat(response.body()).contains("\"valid\":true");
    }
  }

  @Test
  void testAcceptedResponse() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(post("/async-job")
          .willReturn(status(202)
              .withHeader("Location", "/jobs/123")
              .withBody("{\"jobId\":123,\"status\":\"processing\"}")));
      
      URI uri = URI.create(wiremock.url("/async-job"));
      HttpRequest request = HttpRequest.newBuilder()
          .POST(HttpRequest.BodyPublishers.ofString("{\"action\":\"process\"}"))
          .uri(uri)
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(202);
      assertThat(response.headers().firstValue("Location")).hasValue("/jobs/123");
      assertThat(response.body()).contains("processing");
    }
  }

  @Test
  void testUnauthorizedResponse() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
      wiremock.stubFor(get("/secure")
          .withHeader("Authorization", absent())
          .willReturn(unauthorized()
              .withBody("{\"error\":\"Unauthorized\"}")));
      
      URI uri = URI.create(wiremock.url("/secure"));
      HttpRequest request = HttpRequest.newBuilder()
          .GET()
          .uri(uri)
          .build();
      
      HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
      
      assertThat(response.statusCode()).isEqualTo(401);
      assertThat(response.body()).contains("Unauthorized");
    }
  }
}
