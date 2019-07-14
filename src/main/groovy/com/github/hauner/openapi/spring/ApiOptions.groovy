package com.github.hauner.openapi.spring

class ApiOptions {
    /**
     * the root package of the generated interfaces/model. Interfaces and Models will be generated
     * to an "api" and "model" package inside the root package:
     *
     * - interfaces => "${packageName}.api"
     * - models => "${packageName}.model"
     */
    String packageName

    /**
     * the destination folder for generating interfaces & models.
     */
    String targetFolder
}
