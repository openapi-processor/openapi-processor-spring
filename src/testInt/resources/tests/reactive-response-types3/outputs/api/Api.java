package generated.api;

import generated.model.Props;
import generated.support.Generated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Generated(value = "openapi-processor-spring", version = "test")
public interface Api {

    @GetMapping(path = "/prop/{id}", produces = {"application/json"})
    Mono<Props> getPropId(@PathVariable(name = "id") Integer id);

    @GetMapping(path = "/props", produces = {"application/json"})
    Flux<Props> getProps();

}
