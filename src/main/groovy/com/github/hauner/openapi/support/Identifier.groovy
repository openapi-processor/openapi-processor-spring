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

package com.github.hauner.openapi.support

import groovy.transform.CompileStatic

/**
 * Identifier support to create valid java identifiers.
 *
 * @author Martin Hauner
 */
@Deprecated
@CompileStatic
class Identifier {

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
     */
    static String toCamelCase (String src) {
        def sb = new StringBuilder()

        def wordSplit = false
        src.toCharArray ().eachWithIndex { char c, int idx ->

            if (idx == 0) {
                if (isValidStart (c)) {
                    sb.append (c)
                }
            } else {
                if (isValidPart (c)) {
                    if (wordSplit) {
                        sb.append (c.toUpperCase ())
                        wordSplit = false
                    } else {
                        sb.append (c)
                    }
                } else {
                    wordSplit = true
                }
            }
        }

        sb.toString ()
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
     */
    static String toClass (String src) {
        toCamelCase (src).capitalize ()
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
     */
    static String toEnum (String src) {
        def sb = new StringBuilder()

        def wordSplit = false
        src.toCharArray ().eachWithIndex { char c, int idx ->

            def cu = c.toUpperCase ()
            if (idx == 0) {
                if (isValidStart (c)) {
                    sb.append (cu)
                }
            } else {
                if (isValidPart (c)) {
                    if (wordSplit) {
                        sb.append ("_")
                        sb.append (cu)
                        wordSplit = false
                    } else {
                        sb.append (cu)
                    }
                } else {
                    wordSplit = true
                }
            }
        }

        sb.toString ()
    }

    private static boolean isValidStart (char c) {
        Character.isJavaIdentifierStart (c) && !isWordSplitPart (c)
    }

    private static boolean isValidPart (char c) {
        Character.isJavaIdentifierPart (c) && !isWordSplitPart (c)
    }

    private static boolean isWordSplitPart(char c) {
        c == '_' as char  // split at underscore
    }

}
