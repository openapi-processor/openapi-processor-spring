package generated.api;

import generated.model.Props;
import generated.support.Generated;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Generated(value = "openapi-processor-spring", version = "test")
public interface Api {

    @GetMapping(path = "/endpoint-object")
    void getEndpointObject(Props props);

    @GetMapping(path = "/endpoint-map")
    void getEndpointMap(@RequestParam Map<String, String> props);

}
