package generated.api;

import generated.support.Generated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Generated(value = "openapi-processor-spring", version = "test")
public interface Api {

    @PostMapping(
            path = "/multipart/single-file",
            consumes = {"multipart/form-data"})
    ResponseEntity<Void> postMultipartSingleFile(
            @RequestParam(name = "file") MultipartFile file,
            @RequestParam(name = "other") String other);

    @PostMapping(
            path = "/multipart/multiple-files",
            consumes = {"multipart/form-data"})
    ResponseEntity<Void> postMultipartMultipleFiles(
            @RequestParam(name = "files") MultipartFile[] files,
            @RequestParam(name = "other") String other);

}
