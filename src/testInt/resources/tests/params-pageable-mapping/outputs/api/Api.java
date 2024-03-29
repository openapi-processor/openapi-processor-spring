package generated.api;

import generated.support.Generated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Generated(value = "openapi-processor-spring", version = "test")
public interface Api {

    @GetMapping(path = "/page", produces = {"application/json"})
    ResponseEntity<Page<String>> getPage(Pageable pageable);

    @GetMapping(path = "/page-inline", produces = {"application/json"})
    ResponseEntity<Page<String>> getPageInline(Pageable pageable);

}
