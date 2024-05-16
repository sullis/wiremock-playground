package io.github.sullis.playground.wiremock;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformerV2;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;


public class FooTransformer
    implements ResponseDefinitionTransformerV2 {

  private char c;
  private long size;

  public FooTransformer(char c, long size) {
    this.c = c;
    this.size = size;
  }

  @Override
  public ResponseDefinition transform(ServeEvent serveEvent) {
    return new ResponseDefinitionBuilder()
        .withHeader("MyHeader", "Transformed")
        .withStatus(200)
        .withBody("Transformed body")
        .build();
  }

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }
}
