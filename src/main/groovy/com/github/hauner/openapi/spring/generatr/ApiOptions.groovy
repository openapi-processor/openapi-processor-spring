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

package com.github.hauner.openapi.spring.generatr

/**
 * Options of the generatr.
 *
 * @author Martin Hauner
 */
class ApiOptions {

    /**
     * the path to the open api yaml file.
     */
    String apiPath

    /**
     * the destination folder for generating interfaces & models. This is the parent of the
     * {@link #packageName} folder tree.
     */
    String targetDir

    /**
     * the root package of the generated interfaces/model. The package folder tree will be created
     * inside {@link #targetDir}. Interfaces and models will be placed into the "api" and "model"
     * subpackages of packageName:
     * - interfaces => "${packageName}.api"
     * - models => "${packageName}.model"
     */
    String packageName

    /**
     * show warnings from the open api parser.
     */
    boolean showWarnings

    /**
     * map OpenAPI type names to fully qualified java class names. All usages of the given type
     * in the api description will be replaced with the given java class in the generated code and
     * no model class will be generated for the OpenAPI type.
     */
    Map<String, String> typeMappings

}
