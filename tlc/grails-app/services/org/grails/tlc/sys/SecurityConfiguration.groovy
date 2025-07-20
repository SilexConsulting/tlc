package org.grails.tlc.sys

class SecurityConfiguration {
    private static final String EXCHANGE_API_KEY_ENV_VAR = "EXCHANGE_API_KEY"

    static String getExchangeApiKey() {
        def apiKey = System.getenv(EXCHANGE_API_KEY_ENV_VAR)
        if (!apiKey) {
            throw new IllegalStateException("Missing required environment variable: ${EXCHANGE_API_KEY_ENV_VAR}")
        }
        return apiKey
    }
}
