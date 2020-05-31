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

package com.github.hauner.openapi.core.framework

/**
 * details of an annotation.
 *
 * @author Martin Hauner
 */
class FrameworkAnnotation {
    private String name
    private String pkg

    /**
     * The plain name of the annotation for this parameter (ie. without the @).
     *
     * @return the name of the annotation
     */
    String getName () {
        name
    }

    /**
     * The fully qualified class name of the annotation.
     *
     * @return the fully qualified class name of the annotation
     */
    String getFullyQualifiedName () {
        "${pkg}.${name}"
    }

    /**
     * The full annotation name with a leading @.
     *
     * @return the full annotation name with a leading @
     */
    String getAnnotationName () {
        "@${name}"
    }

}
