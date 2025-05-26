package generated.api;

import generated.support.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@Generated(value = "openapi-processor-spring", version = "test")
public interface EndpointApi {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping(path = "/endpoint/{foo}")
    void getEndpointFoo(@PathVariable(name = "foo") String foo);

}
