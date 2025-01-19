package com.example.Pharmacy.service;

public interface RedisService {

    <T> T getData(String key,Class<T> responseClass);

    void setData(String key,Object o,Long ttl);

    void deleteData(String key);
}
