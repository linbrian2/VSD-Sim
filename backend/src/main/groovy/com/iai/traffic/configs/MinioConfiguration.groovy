package com.iai.traffic.configs

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(value= MinioConfiguration.PREFIX)
class MinioConfiguration {
    static final String PREFIX = "minio"
    String accessKey
    String secretKey
    String endpoint
}
