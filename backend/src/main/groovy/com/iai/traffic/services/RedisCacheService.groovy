package com.iai.traffic.services

import com.iai.traffic.configs.RedisConfiguration
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import groovy.util.logging.Slf4j

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class RedisCacheService {
    @Inject RedisConfiguration mRedisConfig
    StatefulRedisConnection<String, String> mConnection
    RedisClient mRedisClient

    @PostConstruct
    def initialize() {
        log.info("Connecting to Redis ...")
        mRedisClient = RedisClient.create(mRedisConfig.uri)
        mConnection = mRedisClient.connect();
    }

    @PreDestroy
    def destroy() {
        log.info("Disconnecting from Redis ...")
        if(mConnection != null) {
            mConnection.close()
        }
        if (mRedisClient != null) {
            mRedisClient.shutdown()
        }
    }

    def putData(String key, String value, long ttl){
        RedisCommands<String, String> commands = mConnection.sync();
        commands.set(key,value);
        if (ttl > 0) {
            commands.expire(key, ttl);
        }
    }
    def getData(String key){
        RedisCommands<String, String> commands = mConnection.sync();
        return commands.get(key);
    }
}