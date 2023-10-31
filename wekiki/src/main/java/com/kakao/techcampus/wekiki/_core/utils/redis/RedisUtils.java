package com.kakao.techcampus.wekiki._core.utils.redis;

import com.kakao.techcampus.wekiki.group.domain.Invitation;
import lombok.RequiredArgsConstructor;
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
}
