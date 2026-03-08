package com.mifica.config;

import com.mifica.redis.GamificationSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.lang.NonNull;

@Configuration
public class RedisConfig {

    public static final String GAMIFICATION_CHANNEL = "gamification-events";

    @Bean
    public StringRedisTemplate stringRedisTemplate(@NonNull RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public ChannelTopic gamificationTopic() {
        return new ChannelTopic(GAMIFICATION_CHANNEL);
    }

    @Bean
    public MessageListenerAdapter gamificationListenerAdapter(@NonNull GamificationSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @NonNull RedisConnectionFactory connectionFactory,
            @NonNull MessageListenerAdapter gamificationListenerAdapter,
            @NonNull ChannelTopic gamificationTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(gamificationListenerAdapter, gamificationTopic);
        return container;
    }
}
