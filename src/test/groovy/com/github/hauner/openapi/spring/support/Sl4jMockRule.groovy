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
