package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.model.Schema
import io.swagger.v3.oas.models.media.Schema as OaSchema

class SchemaCollector {

    List<Schema> collect(Map<String, OaSchema> schemas) {
        Map<String, Schema> models = new HashMap<> ()

        schemas.each { Map.Entry<String, OaSchema> entry ->
            String name = entry.key
            OaSchema oaSchema = entry.value

            def props = []
            oaSchema.properties.each { Map.Entry<String, OaSchema> propEntry ->
                String propName = propEntry.key
                OaSchema propSchema = propEntry.value

                def prop = new Schema(name: propName, type: propSchema.type)
                props.add (prop)
            }

            def schema = new Schema(name: name, type: oaSchema.type, properties: props)
            models.put (name, schema)
        }

        models.values () as List
    }
}
