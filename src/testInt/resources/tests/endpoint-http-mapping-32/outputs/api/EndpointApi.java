package generated.api;

import generated.support.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Generated(value = "openapi-processor-spring", version = "test")
public interface EndpointApi {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "/endpoint", method = RequestMethod.QUERY)
    void queryEndpoint();

}
