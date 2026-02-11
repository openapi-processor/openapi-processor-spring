package generated.api;

import generated.model.Foo;
import generated.support.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Generated(value = "openapi-processor-spring", version = "test")
public interface Api {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(path = "/foo/params")
    void postFooParams(
            @RequestParam(name = "foo") String foo,
            @RequestParam(name = "bar") String bar);

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(path = "/foo/object", consumes = {"application/x-www-form-urlencoded"})
    void postFooObject(Foo body);

}
