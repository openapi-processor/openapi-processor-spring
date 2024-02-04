package generated.api;

import generated.model.Props;
import generated.support.Generated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Generated(value = "openapi-processor-spring", version = "test")
public interface Api {

    @GetMapping(path = "/prop/{id}", produces = {"application/json"})
    Mono<ResponseEntity<Mono<Props>> getPropId(@PathVariable(name = "id") Integer id);

    @GetMapping(path = "/props", produces = {"application/json"})
    Mono<ResponseEntity<Flux<Props>> getProps();

}
