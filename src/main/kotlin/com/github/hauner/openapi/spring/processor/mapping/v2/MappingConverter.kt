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

package com.github.hauner.openapi.spring.processor.mapping.v2

import com.github.hauner.openapi.spring.converter.mapping.*
import com.github.hauner.openapi.spring.processor.mapping.v2.Mapping as MappingV2


/**
 *  Converter for the type mapping from the mapping yaml. It converts the type mapping information
 *  into the format used by {@link com.github.hauner.openapi.spring.converter.DataTypeConverter}.
 *
 *  @author Martin Hauner
 */
class MappingConverter {
    companion object {
        private const val SEPARATOR_TYPE = " => "
        private const val SEPARATOR_FORMAT = ":"
        private val PATTERN_GENERICS = "(\\S+?)\\s*<(.+?)>".toPattern()
    }

    fun convert(mapping: MappingV2): List<Mapping> {
        val result = ArrayList<Mapping>()

        if(mapping.map.result != null) {
            result.add(convertResult(mapping.map.result))
        }

        // todo
        // single
        // multi

        mapping.map.types.forEach {
            result.add(convertType(it))
        }

        mapping.map.parameters.forEach {
            result.add (convertParameter (it))
        }

        mapping.map.responses.forEach {
            result.add (convertResponse (it))
        }

        mapping.map.paths.forEach() {
            result.add(convertPath (it.key, it.value))
        }

        return result
    }

    private fun convertResult (result: String): Mapping {
        return ResultTypeMapping (result)
    }

    private fun convertType(source: Type): Mapping {
        val (fromType, toType) = parseTypes(source.type)
        val (fromName, fromFormat) = parseFromType(fromType)
        val (toName, generics) = parseToType(toType, source.generics)

        return TypeMapping(fromName, fromFormat, toName, generics)
    }

    private fun convertParameter(source: Parameter): Mapping {
        if (source is RequestParameter) {
            val (name, toType) = parseTypes(source.name)
            val (toName, generics) = parseToType(toType, source.generics)

            return ParameterTypeMapping(name, TypeMapping(null, toName, generics))

        } else if (source is AdditionalParameter) {
            val (name, toType) = parseTypes(source.add)
            val (toName, generics) = parseToType(toType, source.generics)

            return AddParameterTypeMapping(name, TypeMapping(null, toName, generics))

        } else {
            throw Exception("unknown parameter mapping $source")
        }
    }

    private fun convertResponse(source: Response): Mapping {
        val (content, toType) = parseTypes(source.content)
        val (toName, generics) = parseToType(toType, source.generics)

        return ResponseTypeMapping(content, TypeMapping(null, toName, generics))
    }

    private fun convertPath(path: String, source: Path): Mapping {
        val result = ArrayList<Mapping>()

        if(source.result != null) {
            result.add(convertResult(source.result))
        }

        // todo
        // single
        // multi

        source.types.forEach {
            result.add(convertType(it))
        }

        source.parameters.forEach {
            result.add (convertParameter (it))
        }

        source.responses.forEach {
            result.add (convertResponse (it))
        }

        return EndpointTypeMapping(path, result, source.exclude)
    }

    private data class MappingTypes(val result: String, val format: String)
    private data class FromType(val name: String, val format: String?)
    private data class ToType(val name: String, val generics: List<String>)

    private fun parseTypes(type: String): MappingTypes {
        val split = type
                .split(SEPARATOR_TYPE)
                .map { it.trim() }

        return MappingTypes(
                split.component1(),
                split.component2())
    }

    private fun parseFromType(type: String): FromType {
        var name = type
        var format: String? = null

        if (type.contains(SEPARATOR_FORMAT)) {
            val split = type
                    .split(SEPARATOR_FORMAT)
                    .map { it.trim() }

            name = split.component1()
            format = split.component2()
        }

        return FromType(name, format)
    }

    private fun parseToType(type: String, typeGenerics: List<String>?): ToType {
        var name: String = type
        var generics = emptyList<String>()

        val matcher = PATTERN_GENERICS.matcher(type)
        if (matcher.find ()) {
            name = matcher.group (1)
            generics = matcher
                .group (2)
                .split (',')
                    .map { it.trim() }
                    .toList()

        } else if (typeGenerics != null) {
            generics = typeGenerics
        }

        return ToType(name, generics)
    }

}

