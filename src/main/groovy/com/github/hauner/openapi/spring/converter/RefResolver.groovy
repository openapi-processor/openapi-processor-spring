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

package com.github.hauner.openapi.spring.converter

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.media.Schema

/**
 * Resolves $ref objects from an OpenAPI.
 *
 * @author Martin Hauner
 */
class RefResolver {

    private Components components

    RefResolver(Components components) {
        this.components = components
    }

    Schema resolve (String ref) {
        components.schemas.get (getRefName (ref))
    }

    private String getRefName (String ref) {
        ref.substring (ref.lastIndexOf ('/') + 1)
    }

}
