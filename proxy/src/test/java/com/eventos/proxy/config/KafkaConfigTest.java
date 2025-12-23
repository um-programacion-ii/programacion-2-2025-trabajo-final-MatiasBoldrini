package com.eventos.proxy.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = { KafkaConfig.class },
    properties = { "spring.kafka.bootstrap-servers=localhost:9092", "spring.kafka.consumer.group-id=test-group" }
)
class KafkaConfigTest {

    @Autowired
    private KafkaConfig kafkaConfig;

    @Test
    void kafkaListenerContainerFactoryDebeExistir() {
        assertThat(kafkaConfig.kafkaListenerContainerFactory()).isNotNull();
    }
}


