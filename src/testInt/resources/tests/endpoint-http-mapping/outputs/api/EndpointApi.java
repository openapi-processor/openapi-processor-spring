package generated.api;

import generated.support.Generated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Generated(value = "openapi-processor-spring", version = "test")
public interface EndpointApi {

    @DeleteMapping(path = "/endpoint")
    void deleteEndpoint();

    @GetMapping(path = "/endpoint")
    void getEndpoint();

    @RequestMapping(path = "/endpoint", method = RequestMethod.HEAD)
    void headEndpoint();

    @RequestMapping(path = "/endpoint", method = RequestMethod.OPTIONS)
    void optionsEndpoint();

    @PatchMapping(path = "/endpoint")
    void patchEndpoint();

    @PostMapping(path = "/endpoint")
    void postEndpoint();

    @PutMapping(path = "/endpoint")
    void putEndpoint();

    @RequestMapping(path = "/endpoint", method = RequestMethod.TRACE)
    void traceEndpoint();

}
