package generated.api;

import generated.support.Generated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Generated(value = "openapi-processor-spring", version = "test")
public interface EndpointApi {

    @GetMapping(path = "/endpoint/{foo}")
    void getEndpointFoo(@PathVariable(name = "foo") String foo);

}
