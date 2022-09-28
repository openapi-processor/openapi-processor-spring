package generated.api;

import generated.support.Generated;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Generated(
        value = "openapi-processor-spring",
        version = "test",
        url = "https://openapiprocessor.io")
public interface Api {

    @GetMapping(path = "/foo")
    void getFoo(@RequestParam(name = "id") UUID id);

}
