package com.mifica.redis;

import com.mifica.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class GamificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(GamificationPublisher.class);

    private final StringRedisTemplate redisTemplate;

    public GamificationPublisher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publishEvent(Long userId, int points) {
        String message = "User:" + userId + " Points:" + points;
        redisTemplate.convertAndSend(RedisConfig.GAMIFICATION_CHANNEL, message);
        log.info("📤 Evento publicado no Redis: {}", message);
    }
}
