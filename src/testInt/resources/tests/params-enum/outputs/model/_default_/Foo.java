package generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import generated.support.Generated;
import java.util.function.Supplier;

@Generated(value = "openapi-processor-spring", version = "test")
public enum Foo implements Supplier<String> {
    FOO("foo"),
    FOO_2("foo-2"),
    FOO_FOO("foo-foo");

    private final String value;

    Foo(String value) {
        this.value = value;
    }

    @JsonValue
    public String get() {
        return this.value;
    }

    @JsonCreator
    public static Foo fromValue(String value) {
        for (Foo val : Foo.values()) {
            if (val.value.equals(value)) {
                return val;
            }
        }
        throw new IllegalArgumentException(value);
    }
}
