package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.model.Interface
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.Paths

class InterfaceCollector {

    /**
     * extracts the interface names used to group the endpoints from the open api specification.
     * The first tag of an operation (i.e. endpoint) is used as the name of the target interface.
     *
     * @param paths
     * @return list of interfaces to generate
     */
    List<Interface> collect(Paths paths) {
        Map<String, Interface> interfaces = new HashMap<> ()

        paths.each { Map.Entry<String, PathItem> entry ->
            def operations = collectOperations (entry.value)
            operations.each { op ->
                String targetInterfaceName = op.tags.first ()
                interfaces.put (targetInterfaceName, new Interface (name: targetInterfaceName))
            }
        }

        interfaces.values () as List
    }

    private List<Operation> collectOperations(PathItem item) {
        new OperationCollector ().collect (item)
    }

}
