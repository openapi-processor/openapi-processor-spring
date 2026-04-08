package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-spring", version = "test")
public class Foo {

    @JsonProperty("foo")
    private String foo;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

}
