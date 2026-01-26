package com.mifica;

import com.mifica.service.GamificationEventProducer;
import com.mifica.repository.UserRepository;
import com.mifica.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "gamification-events" }, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GamificationKafkaIntegrationTest {

    @Autowired
    private GamificationEventProducer producer;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testProducerConsumerIntegration() throws InterruptedException {
        // Arrange: cria usuário inicial no banco
        User user = new User();
        user.setName("Gabriel");
        user.setPoints(0);
        user.setLevel(1);
        user = userRepository.save(user);

        // Act: envia evento para Kafka
        producer.publishEvent(user.getId(), 50);

        // Espera um pouco para o Consumer processar
        Thread.sleep(2000);

        // Assert: verifica se pontos foram atualizados
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getPoints()).isEqualTo(50);
        assertThat(updatedUser.getLevel()).isEqualTo(1); // ainda não atingiu 100 pontos
    }
}
