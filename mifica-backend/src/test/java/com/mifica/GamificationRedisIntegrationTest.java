package com.mifica;

import com.mifica.redis.GamificationPublisher;
import com.mifica.repository.UserRepository;
import com.mifica.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379"
})
@EnabledIfEnvironmentVariable(named = "RUN_REDIS_INTEGRATION_TESTS", matches = "true")
public class GamificationRedisIntegrationTest {

    @Autowired
    private GamificationPublisher publisher;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testPublisherSubscriberIntegration() throws InterruptedException {
        // Arrange: cria usuário inicial no banco
        User user = new User();
        user.setName("Gabriel");
        user.setPoints(0);
        user.setLevel(1);
        user = userRepository.save(user);

        // Act: publica evento no Redis Pub/Sub
        publisher.publishEvent(user.getId(), 50);

        // Espera um pouco para o Subscriber processar
        Thread.sleep(2000);

        // Assert: verifica se pontos foram atualizados
        User updatedUser = userRepository.findById(java.util.Objects.requireNonNull(user.getId())).orElseThrow();
        assertThat(updatedUser.getPoints()).isEqualTo(50);
        assertThat(updatedUser.getLevel()).isEqualTo(1); // ainda não atingiu 100 pontos
    }
}
