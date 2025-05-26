package generated.api;

import generated.model.Foo;
import generated.support.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Generated(value = "openapi-processor-spring", version = "test")
public interface EnumApi {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping(path = "/endpoint")
    void getEndpoint(@RequestParam(name = "foo") Foo foo);

}
