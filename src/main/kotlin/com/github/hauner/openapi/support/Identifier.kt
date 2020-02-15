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

//@file:JvmName("Identifiers")

package com.github.hauner.openapi.support

import java.lang.Character.isJavaIdentifierPart
import java.lang.Character.isJavaIdentifierStart

/**
 * converts a source string to a valid (camel case) java identifier. One way, ie it is not
 * reversible.
 *
 * conversion rules:
 * characters that are not valid java identifiers will be removed. The characters " ", "_",
 * "-" (valid or not) are interpreted as word separators and the next character will be
 * converted to upper case.
 *
 * @param src the source "string"
 *
 * @return a valid camel case java identifier
 *
 * @author Martin Hauner
 */
fun toCamelCase(src: String): String {
    val sb = StringBuilder()

    var wordSplit = false
    for ((idx, char) in src.toCharArray().withIndex()) {
        if (idx == 0) {
            if (isValidStart(char)) {
                sb.append(char)
            }
        } else {
            if (isValidPart(char)) {
                if (wordSplit) {
                    sb.append(char.toUpperCase())
                    wordSplit = false
                } else {
                    sb.append(char)
                }
            } else {
                wordSplit = true
            }
        }
    }

    return sb.toString()
}


/**
 * converts a source string to a valid (camel case) java class identifier. One way, ie it is
 * not reversible.
 *
 * conversion rules:
 * characters that are not valid java identifiers will be removed. The characters " ", "_",
 * "-" (valid or not) are removed and interpreted as word separators. Each words first character
 * will be converted to upper case.
 *
 * @param src the source string
 *
 * @return a valid camel case java class identifier
 *
 * @author Martin Hauner
 */
fun toClass(src: String): String {
    return toCamelCase(src).capitalize()
}

/**
 * converts a source string to a valid (all upper case) java enum identifier. One way, ie it is
 * not reversible.
 *
 * conversion rules:
 * characters that are not valid java identifiers will be removed. The characters " ", "_",
 * "-" (valid or not) are interpreted as word separators and are replaced by "_" and the words
 * are converted to upper case.
 *
 * @param src the source "string"
 *
 * @return a valid upper case enum java identifier
 *
 * @author Martin Hauner
 */
fun toEnum(src: String): String {
    val sb = StringBuilder()

    var wordSplit = false
    for ((idx, char) in src.toCharArray().withIndex()) {
        val cu = char.toUpperCase()
        if (idx == 0) {
            if (isValidStart(char)) {
                sb.append(cu)
            }
        } else {
            if (isValidPart(char)) {
                if (wordSplit) {
                    sb.append("_")
                    sb.append(cu)
                    wordSplit = false
                } else {
                    sb.append(cu)
                }
            } else {
                wordSplit = true
            }
        }
    }

    return sb.toString()
}

private fun isValidStart(c: Char): Boolean {
    return isJavaIdentifierStart(c) && !isWordSplitPart(c)
}

private fun isValidPart(c: Char): Boolean {
    return isJavaIdentifierPart(c) && !isWordSplitPart(c)
}

private fun isWordSplitPart(c: Char): Boolean {
    return c == '_'  // split at underscore
}
