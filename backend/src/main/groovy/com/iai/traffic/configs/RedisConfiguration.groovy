package com.iai.traffic.configs

import io.micronaut.context.annotation.ConfigurationProperties

import java.time.Duration

@ConfigurationProperties(value= RedisConfiguration.PREFIX)
class RedisConfiguration {
    static final String PREFIX = "redis"
    String   uri
    Duration timeout
}
