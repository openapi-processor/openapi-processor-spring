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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.support.Identifier

/**
 * Writer for POJO classes.
 *
 * @author Martin Hauner
 */
class DataTypeWriter {
    HeaderWriter headerWriter

    void write (Writer target, ObjectDataType dataType) {
        headerWriter.write (target)
        target.write ("package ${dataType.packageName};\n\n")

        List<String> imports = collectImports (dataType.packageName, dataType)
        imports.each {
            target.write ("import ${it};\n")
        }
        if (!imports.isEmpty ()) {
            target.write ("\n")
        }

        target.write ("public class ${dataType.type} {\n\n")

        def propertyNames = dataType.properties.keySet ()
        propertyNames.each {
            def javaPropertyName = Identifier.fromJson (it)
            def propDataType = dataType.getObjectProperty (it)
            target.write ("    @JsonProperty(\"$it\")\n")
            target.write ("    private ${propDataType.name} ${javaPropertyName};\n\n")
        }

        propertyNames.each {
            def javaPropertyName = Identifier.fromJson (it)
            def propDataType = dataType.getObjectProperty (it)
            target.write (getGetter (javaPropertyName, propDataType))
            target.write (getSetter (javaPropertyName, propDataType))
        }

        target.write ("}\n")
    }

    private String getGetter (String propertyName, DataType propDataType) {
        """\
    public ${propDataType.name} get${propertyName.capitalize ()} () {
        return ${propertyName};
    }

"""
    }

    private String getSetter (String propertyName, DataType propDataType) {
        """\
    public void set${propertyName.capitalize ()} (${propDataType.name} ${propertyName}) {
        this.${propertyName} = ${propertyName};
    }

"""
    }

    List<String> collectImports(String packageName, DataType dataType) {
        Set<String> imports = []
        imports.add ('com.fasterxml.jackson.annotation.JsonProperty')
        imports.addAll (dataType.referencedImports)

        new ImportFilter ().filter (packageName, imports)
            .sort ()
    }
}
