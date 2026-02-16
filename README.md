# wiremock-playground

[![CI](https://github.com/sullis/wiremock-playground/actions/workflows/ci.yml/badge.svg)](https://github.com/sullis/wiremock-playground/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A playground project for experimenting with [WireMock](https://github.com/wiremock/wiremock), a flexible HTTP API mocking library for Java.

## Overview

This repository contains example code and tests demonstrating various WireMock features and capabilities, including:

- Basic HTTP mocking with WireMock JUnit 5 extension
- Response stubbing with body files
- Custom response transformers
- Working with fixed-size input streams
- Testing HTTP interactions with Java's HttpClient

## Prerequisites

- **Java 21** or later
- **Maven 3.x** or later

This project uses [SDKMAN!](https://sdkman.io/) for managing Java versions. The `.sdkmanrc` file specifies Java 21.0.9-tem.

## Building the Project

To build the project:

```bash
mvn clean install
```

## Running Tests

To run all tests:

```bash
mvn test
```

The project includes several test classes:

- `WiremockTest` - Basic WireMock usage examples including simple stubbing and body file responses
- `FixedSizeInputStreamTest` - Tests for handling fixed-size input streams with WireMock
- `FooTransformer` - Example custom response transformer implementation

## Project Structure

```
wiremock-playground/
├── src/
│   └── test/
│       ├── java/
│       │   └── io/github/sullis/playground/wiremock/
│       │       ├── WiremockTest.java
│       │       ├── FixedSizeInputStreamTest.java
│       │       ├── FixedSizeInputStream.java
│       │       ├── FooTransformer.java
│       │       └── StreamSourceSupport.java
│       └── resources/
│           ├── __files/
│           │   └── foobar.txt
│           └── logback-test.xml
├── pom.xml
└── README.md
```

## Key Dependencies

- **WireMock 4.0.0-beta.29** - HTTP API mocking
- **JUnit 5** - Testing framework
- **AssertJ** - Fluent assertions library
- **Apache Commons Lang3 & Commons IO** - Utility libraries

## Usage Examples

### Basic HTTP Stubbing

```java
@RegisterExtension
static WireMockExtension wiremock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

@Test
void helloWorld() throws Exception {
    try (HttpClient httpclient = HttpClient.newHttpClient()) {
        wiremock.stubFor(get("/").willReturn(ok().withBody("Hello world")));
        URI uri = URI.create(wiremock.url("/"));
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = httpclient.send(request, 
            HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }
}
```

### Response with Body Files

```java
@Test
void responseWithBodyFile() throws Exception {
    wiremock.stubFor(get("/").willReturn(ok().withBodyFile("foobar.txt")));
    // Body content is loaded from src/test/resources/__files/foobar.txt
}
```

## Related Resources

### gRPC and WireMock
- [Mocking gRPC Microservices](https://www.infoq.com/articles/mocking-grpc-microservices/) - InfoQ article
- [wiremock-grpc-extension](https://github.com/wiremock/wiremock-grpc-extension) - Official gRPC extension for WireMock

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
