package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-spring", version = "test")
public record Props(
    @JsonProperty("prop1")
    String prop1,

    @JsonProperty("prop2")
    String prop2
) {}
