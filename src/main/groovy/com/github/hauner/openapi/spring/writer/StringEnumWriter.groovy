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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.core.writer.TargetWriter
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.model.datatypes.StringEnumDataType
import com.github.hauner.openapi.support.Identifier

/**
 * Writer for String enum.
 *
 * @author Martin Hauner
 */
class StringEnumWriter {

    TargetWriter headerWriter

    void write (Writer target, StringEnumDataType dataType) {
        headerWriter.write (target)
        target.write ("package ${dataType.packageName};\n\n")

        List<String> imports = collectImports (dataType.packageName, dataType)
        imports.each {
            target.write ("import ${it};\n")
        }
        if (!imports.isEmpty ()) {
            target.write ("\n")
        }

        target.write ("public enum ${dataType.type} {\n")

        def values = []
        dataType.values.each {
            values.add ("    ${Identifier.toEnum (it)}(\"${it}\")")
        }
        target.write (values.join (",\n") + ";\n\n")
        target.write("    private final String value;\n\n")

        target.write ("""\
    private ${dataType.type}(String value) {
        this.value = value;
    }

""")

        target.write("""\
    @JsonValue
    public String getValue() {
        return this.value;
    }

""")

        target.write("""\
    @JsonCreator
    public static ${dataType.type} fromValue(String value) {
        for (${dataType.type} val : ${dataType.type}.values()) {
            if (val.value.equals(value)) {
                return val;
            }
        }
        throw new IllegalArgumentException(value);
    }

""")

        target.write ("}\n")
    }

    List<String> collectImports(String packageName, DataType dataType) {
        Set<String> imports = []
        imports.add ('com.fasterxml.jackson.annotation.JsonCreator')
        imports.add ('com.fasterxml.jackson.annotation.JsonValue')
        imports.addAll (dataType.referencedImports)

        new ImportFilter ().filter (packageName, imports)
            .sort ()
    }

}
