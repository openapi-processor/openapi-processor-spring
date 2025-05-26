package generated.api;

import generated.model.Props;
import generated.support.Generated;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Generated(value = "openapi-processor-spring", version = "test")
public interface Api {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping(path = "/endpoint-object")
    void getEndpointObject(Props props);

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping(path = "/endpoint-map")
    void getEndpointMap(@RequestParam Map<String, String> props);

}
