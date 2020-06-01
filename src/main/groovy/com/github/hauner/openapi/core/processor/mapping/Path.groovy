/*
 * Copyright 2020 the original authors
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

package com.github.hauner.openapi.core.processor.mapping

/**
 * a "paths:" entry in the mapping yaml
 *
 *  @author Martin Hauner
 */
@Deprecated
class Path {

    /**
     * path should be excluded
     */
    boolean exclude = false

    /**
     * path limited result mapping
     */
    Result result

    /**
     * path limited type mappings
     */
    List<Type> types

    /**
     * path limited parameter mappings
     */
    List<Parameter> parameters

    /**
     * path limited response mappings
     */
    List<Response> responses

}
