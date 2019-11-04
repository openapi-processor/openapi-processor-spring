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

package com.github.hauner.openapi.spring.converter.mapping

/**
 * Used with {@link EndpointTypeMapping} to configure the java type that represents the schema of
 * the parameter of the endpoint.
 *
 * @author Martin Hauner
 */
class ParameterTypeMapping {

    /**
     * The parameter name of this mapping. Must match 1:1 with what is written in the api.
     */
    String parameterName

    /**
     * The OpenAPI schema type that should be mapped to the {@link #targetTypeName} java type.
     */
    @Deprecated // use mapping
    String sourceTypeName

    /**
     * The fully qualified java type name that will be used for {@link #parameterName}.
     */
    @Deprecated // use mapping
    String targetTypeName

    /**
     * Type mapping valid only for requests with parameter {@link #parameterName}.
     */
    TypeMapping mapping

}
