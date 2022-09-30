package generated.api;

import generated.model.Book;
import generated.support.Generated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Generated(value = "openapi-processor-spring", version = "test")
public interface Api {

    @PostMapping(
            path = "/book",
            consumes = {"application/json"},
            produces = {"application/json"})
    Book postBook(@RequestBody Book body);

}
