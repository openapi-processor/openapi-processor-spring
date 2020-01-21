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

import com.github.hauner.openapi.spring.converter.ApiOptions
import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.Interface

/**
 * Writer for Java interfaces.
 *
 * @author Martin Hauner
 * @authro Bastian Wilhelm
 */
class InterfaceWriter {
    ApiOptions apiOptions
    HeaderWriter headerWriter
    MethodWriter methodWriter
    BeanValidationFactory beanValidationWriter

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

                    if (apiOptions.beanValidation) {
                        imports.addAll(beanValidationWriter.collectImports (p.dataType))
                    }
                }

                imports.addAll (p.dataTypeImports)
            }

            ep.requestBodies.each { b ->
                imports.add (b.annotationWithPackage)
                imports.addAll (b.imports)
                if (apiOptions.beanValidation) {
                    imports.addAll(beanValidationWriter.collectImports (b.requestBodyType))
                }
            }

            if (!ep.response.empty) {
                imports.addAll (ep.response.imports)
            }
        }

        new ImportFilter ()
            .filter (packageName, imports)
            .sort ()
    }
}
