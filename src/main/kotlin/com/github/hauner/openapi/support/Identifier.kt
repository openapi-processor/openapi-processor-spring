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
 * create camel case from word breaks. A word break is any invalid character (i.e. it is not
 * allowed in a java identifier), an underscore or an upper case letter. Invalid characters
 * are dropped.
 *
 * All words are converted to lowercase and are capitalized and joined except the first word
 * that is no capitalized.
 *
 * @param src the source "string"
 * @return a valid camel case java identifier
 *
 * @author Martin Hauner
 */
fun toCamelCase(src: String): String {
    return joinCamelCase(joinSingleCharWords(splitAtWordBreaks(src)))
}


/**
 * converts a source string to a valid (camel case) java *class* identifier. One way, ie it is
 * not reversible.
 *
 * conversion rules:
 * create camel case from word breaks. A word break is any invalid character (i.e. it is not
 * allowed in a java identifier), an underscore or an upper case letter. Invalid characters
 * are dropped.
 *
 * All words are converted to lowercase and are capitalized and joined.
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
 * create camel case from word breaks. A word break is any invalid character (i.e. it is not
 * allowed in a java identifier), an underscore or an upper case letter. Invalid characters
 * are dropped.
 *
 * All words are converted to uppercase and joined by an underscore.
 *
 * @param src the source "string"
 *
 * @return a valid upper case enum java identifier
 *
 * @author Martin Hauner
 */
fun toEnum(src: String): String {
    return joinEnum(joinSingleCharWords(splitAtWordBreaks(src)))
}


/**
 * joins the given words to a single camel case string.
 *
 * The first word is lower case.
 *
 * @param words a list of words
 * @return a came case string
 *
 * @author Martin Hauner
 */
private fun joinCamelCase(words: ArrayList<String>): String {
    val sb = StringBuilder()

    words.forEachIndexed { idx, p ->
        if (idx == 0) {
            sb.append(p.toLowerCase())
        } else {
            sb.append(p.toLowerCase().capitalize())
        }
    }

    if (sb.isEmpty()) {
        return "invalid"
    }

    return sb.toString()
}

/**
 * joins the given words to a single uppercase string separated by underscore.
 *
 * @param words a list of words
 * @return an uppercase string
 *
 * @author Martin Hauner
 */
private fun joinEnum(words: ArrayList<String>): String {
    val result = words.joinToString("_") { it.toUpperCase() }

    if (result.isEmpty()) {
        return "INVALID"
    }

    return result
}

/**
 * joins two words if at least one has only a single character.
 *
 * this tries to avoid identifiers with multiple uppercase characters in a row.
 *
 * @param words a list of words
 * @return a list of words
 *
 * @author Martin Hauner
 */
private fun joinSingleCharWords(words: List<String>): ArrayList<String> {
    val merged = ArrayList<String>()
    val current = StringBuilder()

    words.forEachIndexed { idx, p ->
        if (idx == 0) {
            current.append(p)
        } else {
            if (current.last().isUpperCase() && (current.length == 1 || p.length == 1)) {
                current.append(p)
            } else {
                merged.add(current.toString())
                current.clear()
                current.append(p)
            }
        }
    }


    if (current.isNotEmpty()) {
        merged.add(current.toString())
    }

    return merged
}

/**
 * splits the given string at the word breaks.
 *
 * @param src the source "string"
 * @return a list of split words
 *
 * @author Martin Hauner
 */
private fun splitAtWordBreaks(src: String): List<String> {
    val words = ArrayList<String>()
    val current = StringBuilder()

    // clear illegal characters at at the beginning
    val trimmed = src.trimStart {
        !isValidStart(it)
    }

    trimmed.forEachIndexed { idx, c ->

        if (idx != 0 && isWordBreak(c)) {
            if (current.isEmpty()) {
                if (isValid(c)) {
                    current.append(c)
                }
            } else /* part.isNotEmpty() */ {
                words.add(current.toString())
                current.clear()

                if (isValid(c)) {
                    current.append(c)
                }
            }

        } else {
            current.append(c)
        }
    }

    if(current.isNotEmpty()) {
        words.add(current.toString())
    }

    return words
}


private val INVALID_WORD_BREAKS = listOf(' ', '-')
private val VALID_WORD_BREAKS = listOf('_')


private fun isValid(c: Char): Boolean {
    return isJavaIdentifierPart(c) && !isValidWordBreak(c)
}

private fun isValidStart(c: Char): Boolean {
    return isJavaIdentifierStart(c) && !isValidWordBreak(c)
}

private fun isWordBreak(c: Char): Boolean {
    return isWordBreakChar(c) || c.isUpperCase() || !isJavaIdentifierPart(c)
}

private fun isWordBreakChar(c: Char): Boolean {
    return isInvalidWordBreak(c) || isValidWordBreak(c)
}

private fun isValidWordBreak(c: Char): Boolean {
    return VALID_WORD_BREAKS.contains(c)
}

private fun isInvalidWordBreak(c: Char): Boolean {
    return INVALID_WORD_BREAKS.contains(c)
}
