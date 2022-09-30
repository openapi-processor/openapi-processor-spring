package generated.api;

import generated.support.Generated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Generated(value = "openapi-processor-spring", version = "test")
public interface EndpointApi {

    @GetMapping(path = "/endpoint")
    void getEndpoint();

    @PutMapping(path = "/endpoint")
    void putEndpoint();

    @PostMapping(path = "/endpoint")
    void postEndpoint();

    @PatchMapping(path = "/endpoint")
    void patchEndpoint();

}
