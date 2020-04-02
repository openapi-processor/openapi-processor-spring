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


@Deprecated
interface SchemaType {

    /**
     * find all mappings from the given mapping list that match the current endpoint.
     * 
     * @param typeMappings source list of type mappings
     * @return list of matching type mappings
     */
    List<Mapping> matchEndpointMapping (List<Mapping> typeMappings)
    List<Mapping> matchIoMapping (List<Mapping> typeMappings)
    List<Mapping> matchTypeMapping (List<Mapping> typeMappings)
    Mapping matchResultMapping (List<Mapping> typeMappings)

    // todo....
}
