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

import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.Interface

/**
 * Writer for Java interfaces.
 *
 * @author Martin Hauner
 */
class InterfaceWriter {
    HeaderWriter headerWriter
    MethodWriter methodWriter

    void write (Writer target, Interface itf) {
        headerWriter.write (target)
        target.write ("package ${itf.packageName};\n\n")

        List<String> imports = collectImports (itf.packageName, itf.endpoints)
        imports.each {
            target.write ("import ${it};\n")
        }
        target.write ("\n")

        target.write ("public interface ${itf.interfaceName} {\n\n")

        itf.endpoints.each {
            methodWriter.write(target, it)
            target.write ("\n")
        }

        target.write ("}\n")
    }

    List<String> collectImports(String packageName, List<Endpoint> endpoints) {
        Set<String> imports = []

        imports.add ('org.springframework.http.ResponseEntity')

        endpoints.each { ep ->
            imports.add (ep.method.classNameWithPackage)

            ep.parameters.each { p ->
                if (p.withAnnotation()) {
                    imports.add (p.annotationWithPackage)
                }

                imports.add (p.dataTypeImport)
            }

            if (!ep.response.empty) {
                imports.add (ep.response.import)
            }
        }

        new ImportFilter ()
            .filter (packageName, imports)
            .sort ()
    }
}
