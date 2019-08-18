package com.github.hauner.openapi.spring.model.datatypes

/**
 * OpenAPI named #/component/schemas type or an inline type.
 */
class CompositeDataType implements DataType {


    String name // obsolete

    String type  // java type, ie. name

    Map<String, DataType> properties = new LinkedHashMap<>()

    @Override
    List<String> getImports () {
        null
    }

    void addProperty(String name, DataType type) {
        properties.put (name, type)
    }

}
