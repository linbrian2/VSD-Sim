package com.iai.traffic.configs

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(value= AppConfiguration.PREFIX)
class AppConfiguration {
    public static final String PREFIX = "micronaut.application"

    private String version
    private String name
    private String description

    public AppConfiguration() {
        super()
    }

    public String getName() {
        return name
    }

    public String getVersion() {
        return version
    }

    public String getDescription() {
        return description
    }

    public void setVersion(String version) {
        this.version = version
    }

    public void setDescription(String description) {
        this.description = description
    }
}
