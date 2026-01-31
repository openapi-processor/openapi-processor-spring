package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-spring", version = "test")
public record Foo(
    @JsonProperty("foo")
    String foo,

    @JsonProperty("bar")
    String bar
) {}
