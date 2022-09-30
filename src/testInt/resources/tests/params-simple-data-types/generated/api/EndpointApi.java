package generated.api;

import generated.support.Generated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Generated(value = "openapi-processor-spring", version = "test")
public interface EndpointApi {

    @GetMapping(path = "/endpoint")
    void getEndpoint(@RequestParam(name = "foo") String foo);

    @GetMapping(path = "/endpoint-optional")
    void getEndpointOptional(
            @RequestParam(name = "foo", required = false, defaultValue = "bar") String foo);

}
