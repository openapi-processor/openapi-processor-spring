/*
 * Copyright 2019 https://github.com/hauner/openapi-spring-generator
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

package com.github.hauner.openapi.spring

class ApiOptions {
    /**
     * the root package of the generated interfaces/model. Interfaces and Models will be generated
     * to an "api" and "model" package inside the root package:
     *
     * - interfaces => "${packageName}.api"
     * - models => "${packageName}.model"
     */
    String packageName

    /**
     * the destination folder for generating interfaces & models.
     */
    String targetFolder
}
