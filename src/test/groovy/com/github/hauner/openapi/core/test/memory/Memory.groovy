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

package com.github.hauner.openapi.core.test.memory

/**
 * in-memory content by path.
 */
class Memory {
    private static final Map<String, byte[]> contents = new HashMap<> ()

    static byte[] get (String path) {
        contents.get (path)
    }

    static void add (String path, String data) {
        add (path, data.getBytes ("UTF-8"))
    }

    static void add (String path, byte[] data) {
        contents.put (path, data)
    }

}
