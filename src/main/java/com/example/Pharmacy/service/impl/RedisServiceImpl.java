package com.example.Pharmacy.service.impl;

import com.example.Pharmacy.service.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate redisTemplate;
    private ObjectMapper objectMapper = null;

    /**
     * Get ObjectMapper
     *
     * @return
     */
    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            return new ObjectMapper();
        }
        return objectMapper;
    }

    /**
     * Get data from Redis Cache
     *
     * @param key
     * @param responseClass
     * @param <T>
     * @return
     */
    @Override
    public <T> T getData(String key, Class<T> responseClass) {
        try {
            // Get data from Redis Cache
            Object data = redisTemplate.opsForValue().get(key);
            if (data != null) {
                ObjectMapper mapper = getObjectMapper();
                if (responseClass.equals(List.class)) {
                    // Convert data to List
                    return (T) mapper.readValue(data.toString(), new TypeReference<List<T>>() {
                    });
                } else {
                    // Convert data to responseClass
                    return mapper.readValue(data.toString(), responseClass);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Set data in Redis Cache
     *
     * @param key
     * @param o
     * @param ttl
     */
    @Override
    public void setData(String key, Object o, Long ttl) {
        try {
            ObjectMapper mapper = getObjectMapper();
            // Convert object to JSON
            String jsonValue = mapper.writeValueAsString(o);
            // Set data in Redis Cache
            redisTemplate.opsForValue().set(key, jsonValue, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete data from Redis Cache
     *
     * @param key
     */
    @Override
    public void deleteData(String key) {
        try {
            // Delete data from Redis Cache
            redisTemplate.opsForValue().getAndDelete(key);
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}
