package generated.api;

import generated.model.Book;
import generated.support.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Generated(value = "openapi-processor-spring", version = "test")
public interface Api {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/book", consumes = {"application/json"}, produces = {"application/json"})
    Book postBook(@RequestBody Book body);

}
