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

package com.github.hauner.openapi.spring.support

import org.junit.rules.ExternalResource
import org.slf4j.Logger

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Mock @Sl4j logger
 *
 * based on https://stackoverflow.com/a/25031713
 */
class Sl4jMockRule extends ExternalResource {
    Field logField

    Logger mockLogger
    Logger orgLogger

    Sl4jMockRule(Class target, Logger logger) {
        logField = target.getDeclaredField("log")
        mockLogger = logger
    }

    @Override
    protected void before () throws Throwable {
        logField.accessible = true

        Field modifiersField = Field.getDeclaredField("modifiers")
        modifiersField.accessible = true
        modifiersField.setInt(logField, (logField.getModifiers() & ~Modifier.FINAL) as int)

        orgLogger = (Logger) logField.get(null)
        logField.set(null, mockLogger)
    }

    @Override
    protected void after () {
        logField.set (null, orgLogger)
    }
}
