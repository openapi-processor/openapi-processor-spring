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

package com.github.hauner.openapi.spring.model.datatypes

/**
 * @author Bastian Wilhelm
 */
class DataTypeHelper {
    static DataType createBoolean(DataTypeConstraints constraints) {
        create ('java.lang', 'Boolean', constraints)
    }

    static boolean isBoolean(DataType dataType) {
        dataType != null && dataType.packageName == 'java.lang' && dataType.name == 'Boolean'
    }

    static DataType createInteger(DataTypeConstraints constraints) {
        create ('java.lang', 'Integer', constraints)
    }

    static boolean isInteger(DataType dataType) {
        dataType != null && dataType.packageName == 'java.lang' && dataType.name == 'Integer'
    }

    static DataType createLong(DataTypeConstraints constraints) {
        create ('java.lang', 'Long', constraints)
    }

    static boolean isLong(DataType dataType) {
        dataType != null && dataType.packageName == 'java.lang' && dataType.name == 'Long'
    }

    static DataType createFloat(DataTypeConstraints constraints) {
        create ('java.lang', 'Float', constraints)
    }

    static boolean isFloat(DataType dataType) {
        dataType != null && dataType.packageName == 'java.lang' && dataType.name == 'Float'
    }

    static DataType createDouble(DataTypeConstraints constraints) {
        create ('java.lang', 'Double', constraints)
    }

    static boolean isDouble(DataType dataType) {
        dataType != null && dataType.packageName == 'java.lang' && dataType.name == 'Double'
    }

    static DataType createString(DataTypeConstraints constraints) {
        create ('java.lang', 'String', constraints)
    }

    static boolean isString(DataType dataType) {
        dataType != null && dataType.packageName == 'java.lang' && dataType.name == 'String'
    }

    static DataType createVoid() {
        create ('java.lang', 'Void', null)
    }

    static boolean isVoid(DataType dataType) {
        dataType != null && dataType.packageName == 'java.lang' && dataType.name == 'Void'
    }

    static DataType createCollection(DataTypeConstraints constraints, DataType genericType) {
        create ('java.util', 'Collection', constraints, genericType)
    }

    static boolean isCollection (DataType dataType) {
        dataType != null && dataType.packageName == 'java.util' && dataType.name == 'Collection'
    }

    static DataType createList(DataTypeConstraints constraints, DataType genericType) {
        create ('java.util', 'List', constraints, genericType)
    }

    static boolean isList (DataType dataType) {
        dataType != null && dataType.packageName == 'java.util' && dataType.name == 'List'
    }

    static DataType createSet(DataTypeConstraints constraints, DataType genericType) {
        create ('java.util', 'Set', constraints, genericType)
    }

    static boolean isSet (DataType dataType) {
        dataType != null && dataType.packageName == 'java.util' && dataType.name == 'Set'
    }

    static boolean isMap (DataType dataType) {
        dataType != null && (
            (dataType.packageName == 'java.util' && dataType.name == 'Map')
            || (dataType.packageName == 'org.springframework.util' && dataType.name == 'MultiValueMap')
        )
    }

    static DataType createArray(DataTypeConstraints constraints, DataType genericType) {
        new ArrayDataType (
            constraints: constraints,
            generics: [genericType]
        )
    }

    static boolean isArray(DataType dataType) {
        dataType != null && dataType instanceof ArrayDataType
    }

    static DataType create(String packageName, String name, DataTypeConstraints constraints, DataType... generics) {
        new DefaultDataType(
            packageName: packageName,
            name: name,
            constraints: constraints,
            generics: generics
        )
    }

    static DataType createMapped(String packageName, String name, DataTypeConstraints constraints, DataType... generics) {
        new MappedDataType (
            packageName: packageName,
            name: name,
            constraints: constraints,
            generics: generics
        )
    }
}
