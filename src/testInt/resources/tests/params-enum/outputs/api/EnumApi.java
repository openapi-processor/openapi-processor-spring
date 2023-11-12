package generated.api;

import generated.model.Foo;
import generated.support.Generated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Generated(value = "openapi-processor-spring", version = "test")
public interface EnumApi {

    @GetMapping(path = "/endpoint")
    void getEndpoint(@RequestParam(name = "foo") Foo foo);

}
