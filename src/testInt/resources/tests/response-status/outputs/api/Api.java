package generated.api;

import generated.support.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Generated(value = "openapi-processor-spring", version = "test")
public interface Api {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping(path = "/foo")
    void getFoo();

}
