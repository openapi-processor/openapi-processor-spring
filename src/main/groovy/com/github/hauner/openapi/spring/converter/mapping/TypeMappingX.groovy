package com.github.hauner.openapi.spring.converter.mapping

import com.github.hauner.openapi.spring.converter.schema.SchemaInfo

interface TypeMappingX {
    boolean matches (SchemaInfo info)
}
