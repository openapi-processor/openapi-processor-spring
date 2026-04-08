package generated.api;

import generated.model.Foo;
import generated.support.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.service.annotation.PostExchange;

@Generated(value = "openapi-processor-spring", version = "test")
public interface Api {

    @ResponseStatus(HttpStatus.CREATED)
    @PostExchange(url = "/foo", contentType = "application/json", accept = {"application/json"})
    Foo postFoo(@RequestBody Foo body);

}
