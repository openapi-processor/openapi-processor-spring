package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.ApiOptions
import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.model.ApiEndpoint
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem

/**
 * converts the open api model to a new model that is better suited for generating source files
 * from the open api specification.
 */
class ApiConverter {

    private ApiOptions options

    ApiConverter(ApiOptions options) {
        this.options = options
    }

    /**
     * converts the openapi model to the source generation model
     *
     * @param api the open api model
     * @return source generation model
     */
    Api convert (OpenAPI api) {
        def target = new Api ()

        collectInterfaces (api, target)
        addEndpointsToInterfaces (api, target)

        target
    }

    private Map<String, PathItem> addEndpointsToInterfaces (OpenAPI api, Api target) {
        api.paths.each { Map.Entry<String, PathItem> entry ->
            def ops = new OperationCollector ().collect (entry.value)
            ops.each { op ->
                def itf = target.getInterface (op.tags.first ())
                ApiEndpoint ep = new ApiEndpoint (path: entry.key, method: op.httpMethod)
                itf.endpoints.push (ep)
            }
        }
    }

    private void collectInterfaces (OpenAPI api, Api target) {
        target.interfaces = new InterfaceCollector ()
            .collect (api.paths)
    }
}
