package com.mentoree.config.redis;

import io.lettuce.core.ReadFrom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Profile({"local", "real"})
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private  final RedisProperties redisProperties;
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        List<String> nodes = redisProperties.getSentinel().getNodes();

        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master(redisProperties.getSentinel().getMaster());
        redisProperties.getSentinel().getNodes()
                .forEach(n -> sentinelConfig.sentinel("172.18.0.1", Integer.valueOf(n.split(":")[1])));
        sentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
        sentinelConfig.setSentinelPassword(RedisPassword.of(redisProperties.getPassword()));

        return new LettuceConnectionFactory(sentinelConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
