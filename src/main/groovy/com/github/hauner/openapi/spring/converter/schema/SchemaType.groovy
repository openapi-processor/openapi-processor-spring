/*
 * Copyright 2019-2020 the original authors
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

import com.github.hauner.openapi.spring.converter.mapping.Mapping
import com.github.hauner.openapi.spring.converter.mapping.MappingSchemaType

import static com.github.hauner.openapi.spring.converter.mapping.Mapping.Level.*


interface SchemaType {

    List<Mapping> matchEndpointMapping (List<Mapping> typeMappings)
    List<Mapping> matchIoMapping (List<Mapping> typeMappings)
    List<Mapping> matchTypeMapping (List<Mapping> typeMappings)

}

abstract class BaseSchemaType implements SchemaType {

    protected SchemaInfo info

    BaseSchemaType (SchemaInfo info) {
        this.info = info
    }

    @Override
    List<Mapping> matchEndpointMapping (List<Mapping> typeMappings) {
        List<Mapping> ep = findEndpointMappings (typeMappings)

        List<Mapping> io = findIoMappings (ep)
        if (!io.empty) {
            return io
        }

        matchTypeMapping (ep)
    }

    List<Mapping> matchIoMapping (List<Mapping> typeMappings) {
        findIoMappings (typeMappings)
    }

    private List<Mapping> findEndpointMappings (List<Mapping> typeMappings) {
        typeMappings
            .findAll {
                it.matches (ENDPOINT, info)
            }
            .collectMany {
                it.childMappings
            }
    }

    private List<Mapping> findIoMappings (List<Mapping> typeMappings) {
        typeMappings
            .findAll {
                it.matches (IO, info)
            }.collectMany {
            it.childMappings
        }
    }

}

class ObjectSchemaType extends BaseSchemaType {

    ObjectSchemaType (SchemaInfo info) {
        super (info)
    }

    @Override
    List<Mapping> matchTypeMapping (List<Mapping> typeMappings) {
        typeMappings.findAll {
            it.matches (TYPE, info)
        }
    }

}

class ArraySchemaType extends BaseSchemaType {

    ArraySchemaType (SchemaInfo info) {
        super (info)
    }

    @Override
    List<Mapping> matchTypeMapping (List<Mapping> typeMappings) {
        def array = new SchemaInfo (name: 'array')
        typeMappings.findAll () {
            it.matches (TYPE, array)
        }
    }

}

class PrimitiveSchemaType extends BaseSchemaType {

    PrimitiveSchemaType (SchemaInfo info) {
        super (info)
    }

    @Override
    List<Mapping> matchTypeMapping (List<Mapping> typeMappings) {
        def schemaType = new MappingSchemaType () {

            @Override
            String getType () {
                return info.type
            }

            @Override
            String getFormat () {
                return info.format
            }
        }

        typeMappings.findAll () {
            it.matches (TYPE, schemaType)
        }
    }

}
