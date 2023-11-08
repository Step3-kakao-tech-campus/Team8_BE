package com.kakao.techcampus.wekiki._core.utils.redis;

import com.kakao.techcampus.wekiki.group.domain.Invitation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;


    public void setInvitationValues(String key, Invitation invitation, Duration lifetime) {
        redisTemplate.opsForValue().set(key, invitation, lifetime);
    }

    public void setGroupIdValues(String key, Long groupId, Duration lifetime) {
        redisTemplate.opsForValue().set(key, groupId, lifetime);
    }

    public Object getValues(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Set<String> getKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    public void saveKeyAndHashValue(String key, String hashKey, String value) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(key, hashKey, value);
    }

    public String getHashValue(String key, String hashKey) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        return hashOps.get(key, hashKey);
    }

    public void deleteHashValue(String key, String hashKey) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        hashOps.delete(key, hashKey);
    }
}
