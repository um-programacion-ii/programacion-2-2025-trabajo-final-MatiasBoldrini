package com.eventos.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Aplicación principal del servicio Proxy.
 * 
 * Este servicio actúa como intermediario entre el backend y los servicios de la
 * cátedra:
 * - Kafka: Consume eventos de actualización del tópico "eventos-actualizacion"
 * - Redis: Consulta estado de asientos bloqueados y vendidos
 */
@SpringBootApplication
@EnableKafka
public class ProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }

}
