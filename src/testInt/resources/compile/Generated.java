package generated.support;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Documented
@Retention(CLASS)
@Target({TYPE, METHOD})
@Generated(value = "openapi-processor-spring", version = "test")
public @interface Generated {
    /**
     * The name of the source code generator, i.e. openapi-processor-*.
     *
     * @return name of the generator
     */
    String value();

    /**
     * @return version of the generator
     */
    String version();

    /**
     * The date &amp; time of generation (ISO 8601) or "-" if no date was set.
     *
     * @return date of generation
     */
    String date() default "-";

    /**
     * The url of the generator.
     *
     * @return url of generator
     */
    String url() default "https://openapiprocessor.io";
}
