/*
 * Copyright 2019 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hauner.openapi.spring.converter.schema

import com.github.hauner.openapi.spring.converter.mapping.EndpointTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping


interface SchemaType {

    List<?> findEndpointMappings (List<EndpointTypeMapping> typeMappings)
    List<?> findGlobalMappings (List<?> typeMappings)
    List<?> findGlobalTypeMappings (List<?> typeMappings)

}

abstract class SchemaTypeBase implements SchemaType {

    protected SchemaInfo info

    SchemaTypeBase(SchemaInfo info) {
        this.info = info
    }

    @Override
    List<?> findEndpointMappings (List<EndpointTypeMapping> typeMappings) {
        // endpoint mappings matching by path
        def all = typeMappings.findAll {
            it.matches (info)
        }.collect {
            it.typeMappings
        }.flatten ()

        // find global parameter, response & type mappings
        def global = all.findAll {
            it.matches (info)
        }

        // global parameter or response mappings
        def mappings = global.findAll {
            ! (it instanceof TypeMapping)
        }.collect {
            it.mapping
        }

        if (!mappings.empty) {
            return mappings
        }

        global.findAll {
            it instanceof TypeMapping
        }
    }

    @Override
    List<?> findGlobalMappings (List<?> typeMappings) {
        typeMappings.findAll {
            it.matches (info)
        }.collect {
            it.mapping
        }
    }

}

class ObjectSchemaType extends SchemaTypeBase {

    ObjectSchemaType (SchemaInfo info) {
        super (info)
    }

    @Override
    List<?> findGlobalTypeMappings (List<?> typeMappings) {
        typeMappings.findAll {
            it.matches (info)
        }
    }

}

class ArraySchemaType extends SchemaTypeBase {

    ArraySchemaType (SchemaInfo info) {
        super (info)
    }

    @Override
    List<?> findGlobalTypeMappings (List<?> typeMappings) {
        typeMappings.findAll () {
            it.matches (new SchemaInfo (null, null,'array'))
        }
    }
    
}


