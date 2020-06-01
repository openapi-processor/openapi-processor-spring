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

package com.github.hauner.openapi.core.model

import com.github.hauner.openapi.core.model.datatypes.DataType

/**
 * Root of the internal model used to generate the api.
 *
 * @author Martin Hauner
 */
class Api {

    List<Interface> interfaces = []

    /**
     * named data types (i.e. $ref) used in the OpenAPI description.
     */
    private DataTypes models

    Api() {
        this.models = new DataTypes()
    }

    Api(DataTypes models) {
        this.models = models
    }

    DataTypes getModels() {
        models
    }

    @Deprecated // only used from test
    void setModels(List<DataType> dataTypes) {
        models.add(dataTypes)
    }

    Interface getInterface(String name) {
        interfaces.find { it.name.toLowerCase () == name.toLowerCase () }
    }

}
