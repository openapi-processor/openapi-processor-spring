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

package com.github.hauner.openapi.core.converter.mapping

import com.github.hauner.openapi.core.converter.SchemaInfo

/**
 * find mapping in type mapping list for a schema info.
 *
 * @author Martin Hauner
 */
class MappingFinder {

    private List<Mapping> typeMappings = []


    class BaseVisitor implements MappingVisitor {
        MappingSchema schema

        @Override
        boolean match (EndpointTypeMapping mapping) {
            false
        }

        @Override
        boolean match (ParameterTypeMapping mapping) {
            false
        }

        @Override
        boolean match (ResponseTypeMapping mapping) {
            false
        }

        @Override
        boolean match (TypeMapping mapping) {
            false
        }

        @Override
        boolean match (AddParameterTypeMapping mapping) {
            false
        }

        @Override
        boolean match (ResultTypeMapping mapping) {
            false
        }

    }

    class EndpointMatcher extends BaseVisitor {

        @Override
        boolean match (EndpointTypeMapping mapping) {
            mapping.path == schema.path
        }

    }

    class IoMatcher extends BaseVisitor {

        @Override
        boolean match (ParameterTypeMapping mapping) {
            mapping.parameterName == schema.name
        }

        @Override
        boolean match (ResponseTypeMapping mapping) {
            mapping.contentType == schema.contentType
        }

    }

    class TypeMatcher extends BaseVisitor {

        @Override
        boolean match (TypeMapping mapping) {
            if (schema.isPrimitive ()) {
                mapping.sourceTypeName == schema.type && mapping.sourceTypeFormat == schema.format

            } else if (schema.isArray ()) {
                mapping.sourceTypeName == 'array'

            } else {
                mapping.sourceTypeName == schema.name
            }
        }

    }

    class ResultTypeMatcher extends BaseVisitor {

        @Override
        boolean match (ResultTypeMapping mapping) {
            true
        }

    }

    class SingleTypeMatcher extends BaseVisitor {

        @Override
        boolean match (TypeMapping mapping) {
            mapping.sourceTypeName == 'single'
        }

    }

    class MultiTypeMatcher extends BaseVisitor {

        @Override
        boolean match (TypeMapping mapping) {
            mapping.sourceTypeName == 'multi'
        }

    }

    class AddParameterMatcher extends BaseVisitor {

        @Override
        boolean match (AddParameterTypeMapping mapping) {
            true
        }

    }

    class MappingSchemaEndpoint implements MappingSchema {
        String path

        @Override
        String getPath () {
            path
        }

        @Override
        String getName () {
            null
        }

        @Override
        String getContentType () {
            null
        }

        @Override
        String getType () {
            return null
        }

        @Override
        String getFormat () {
            return null
        }

    }

    /**
     * find any matching endpoint mapping for the given schema info.
     *
     * @param info schema info of the OpenAPI schema.
     * @return list of matching mappings
     */
    List<Mapping> findEndpointMappings (SchemaInfo info) {
        List<Mapping> ep = filterMappings (new EndpointMatcher (schema: info), typeMappings)

        List<Mapping> io = filterMappings (new IoMatcher (schema: info), ep)
        if (!io.empty) {
            return io
        }

        filterMappings (new TypeMatcher (schema: info), ep)
    }

    /**
     * find any matching (global) io mapping for the given schema info.
     *
     * @param info schema info of the OpenAPI schema.
     * @return list of matching mappings
     */
    List<Mapping> findIoMappings (SchemaInfo info) {
        filterMappings (new IoMatcher (schema: info), typeMappings)
    }

    /**
     * find any matching (global) type mapping for the given schema info.
     *
     * @param info schema info of the OpenAPI schema.
     * @return list of matching mappings
     */
    List<Mapping> findTypeMappings (SchemaInfo info) {
        filterMappings (new TypeMatcher (schema: info), typeMappings)
    }

    /**
     * find additional parameter mappings for the given endpoint.
     *
     * @param path the endpoint path
     * @return list of matching mappings
     */
    List<Mapping> findAdditionalEndpointParameter (String path) {
        def info = new MappingSchemaEndpoint(path: path)
        List<Mapping> ep = filterMappings (new EndpointMatcher (schema: info), typeMappings)

        def matcher = new AddParameterMatcher (schema: info)
        def add = ep.findAll {
            it.matches (matcher)
        }

        if (!add.empty) {
            return add
        }

        []
    }

    /**
     * find endpoint result type mapping.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the result type mapping.
     */
    List<Mapping> findEndpointResultMapping (SchemaInfo info) {
        List<Mapping> ep = filterMappings (new EndpointMatcher (schema: info), typeMappings)

        def matcher = new ResultTypeMatcher (schema: info)
        def result = ep.findAll {
            it.matches (matcher)
        }

        if (!result.empty) {
            return result
        }

        []
    }

    /**
     * find (global) result type mapping.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the result type mapping.
     */
    List<Mapping> findResultMapping (SchemaInfo info) {
        List<Mapping> ep = filterMappings (new ResultTypeMatcher (schema: info), typeMappings)

        if (!ep.empty) {
            return ep
        }

        []
    }

    /**
     * find endpoint single type mapping.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the single type mapping.
     */
    List<Mapping> findEndpointSingleMapping (SchemaInfo info) {
        List<Mapping> ep = filterMappings (new EndpointMatcher (schema: info), typeMappings)

        def matcher = new SingleTypeMatcher (schema: info)
        def result = ep.findAll {
            it.matches (matcher)
        }

        if (!result.empty) {
            return result
        }

        []
    }

    /**
     * find (global) single type mapping.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the single type mapping.
     */
    List<Mapping> findSingleMapping (SchemaInfo info) {
        List<Mapping> ep = filterMappings (new SingleTypeMatcher (schema: info), typeMappings)

        if (!ep.empty) {
            return ep
        }

        []
    }

    /**
     * find endpoint multi type mapping.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the multi type mapping.
     */
    List<Mapping> findEndpointMultiMapping (SchemaInfo info) {
        List<Mapping> ep = filterMappings (new EndpointMatcher (schema: info), typeMappings)

        def matcher = new MultiTypeMatcher (schema: info)
        def result = ep.findAll {
            it.matches (matcher)
        }

        if (!result.empty) {
            return result
        }

        []
    }

    /**
     * find (global) multi type mapping.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the multi type mapping.
     */
    List<Mapping> findMultiMapping (SchemaInfo info) {
        List<Mapping> ep = filterMappings (new MultiTypeMatcher (schema: info), typeMappings)


        if (!ep.empty) {
            return ep
        }

        []
    }

    /**
     * check if the given endpoint should b excluded.
     *
     * @param path the endpoint path
     * @return true/false
     */
    boolean isExcludedEndpoint (String path) {
        def info = new MappingSchemaEndpoint(path: path)
        def matcher = new EndpointMatcher (schema: info)

        def ep = typeMappings.findAll {
            it.matches (matcher)
        }


        if (!ep.empty) {
            if (ep.size () != 1) {
                throw new AmbiguousTypeMappingException (ep)
            }

            def match = ep.first () as EndpointTypeMapping
            return match.exclude
        }

        false
    }

    private List<Mapping> filterMappings (MappingVisitor visitor, List<Mapping> mappings) {
        mappings
            .findAll {
                it.matches (visitor)
            }
            .collectMany {
                it.childMappings
            }
    }

}
