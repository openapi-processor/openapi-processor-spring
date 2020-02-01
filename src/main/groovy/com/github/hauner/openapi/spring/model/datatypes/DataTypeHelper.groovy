package com.github.hauner.openapi.spring.model.datatypes

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

    static DataType createMap(String packageName, String name, DataTypeConstraints constraints, DataType... generics) {
        new DefaultDataType(
            packageName: packageName,
            name: name,
            constraints: constraints,
            generics: generics,
            isMap: true
        )
    }

    static boolean isMap (DataType dataType) {
        dataType != null && dataType.isMap
    }

    static DataType create(String packageName, String name, DataTypeConstraints constraints, DataType... generics) {
        new DefaultDataType(
            packageName: packageName,
            name: name,
            constraints: constraints,
            generics: generics
        )
    }
}
