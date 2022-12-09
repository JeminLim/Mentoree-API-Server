package com.mentoree.config.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    public <T> T save(String key, T data) {
        try {
            String value = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, value);
            return data;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to write to object as json string", e);
        }
    }

    public <T> Optional<T> get(String key, Class<T> classType)  {
        String value = redisTemplate.opsForValue().get(key);

        if(value == null)
            return Optional.empty();

        try {
            return Optional.of(objectMapper.readValue(value, classType));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON object mapping process is failed", e);
        }
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

}
