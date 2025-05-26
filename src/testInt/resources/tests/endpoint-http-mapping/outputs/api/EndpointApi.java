package generated.api;

import generated.support.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Generated(value = "openapi-processor-spring", version = "test")
public interface EndpointApi {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/endpoint")
    void deleteEndpoint();

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping(path = "/endpoint")
    void getEndpoint();

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "/endpoint", method = RequestMethod.HEAD)
    void headEndpoint();

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "/endpoint", method = RequestMethod.OPTIONS)
    void optionsEndpoint();

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(path = "/endpoint")
    void patchEndpoint();

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(path = "/endpoint")
    void postEndpoint();

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(path = "/endpoint")
    void putEndpoint();

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "/endpoint", method = RequestMethod.TRACE)
    void traceEndpoint();

}
