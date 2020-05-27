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

package com.github.hauner.openapi.spring.processor.mapping

/**
 * general options
 *
 *  @author Martin Hauner
 */
@Deprecated
class Options {

    /**
     * the root package name of the generated interfaces & models (required)
     *
     * Interfaces and models will be generated into the `api` and `model` subpackages of `packageName`.
     * - so the final package name of the generated interfaces will be `"${packageName}.api"`
     * - and the final package name of the generated models will be `"${packageName}.model"`
     */
    String packageName

    /**
     * bean validation (optional)
     */
    Boolean beanValidation

}
