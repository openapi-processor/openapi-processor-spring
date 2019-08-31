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

        def propertyNames = dataType.sortedPropertyNames
        propertyNames.each {
            def propDataType = dataType.getObjectProperty (it)
            target.write ("    private ${propDataType.name} ${it};\n")
        }
        if (!propertyNames.empty) {
            target.write ("\n")
        }

        propertyNames.each {
            def propDataType = dataType.getObjectProperty (it)

            target.write ("""\
    public ${propDataType.name} get${it.capitalize ()} () {
        return $it;
    }

    public void set${it.capitalize ()} (${propDataType.name} $it) {
        this.$it = $it;
    }

""")
        }

        target.write ("}\n")
    }

    List<String> collectImports(String packageName, DataType dataType) {
        Set<String> imports = []
        imports.addAll (dataType.imports)

        new ImportFilter ().filter (packageName, imports)
            .sort ()
    }
}
