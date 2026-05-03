package com.mifica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Classe principal da aplicação Spring Boot.
 * Inicializa o contexto Spring, escaneia os pacotes de entidades
 * e repositórios, e sobe o servidor embarcado Tomcat na porta 8080.
 */
@SpringBootApplication
@EntityScan(basePackages = {"com.mifica.entity", "com.mifica.blockchain"})
@EnableJpaRepositories(basePackages = {"com.mifica.repository", "com.mifica.blockchain"})
public class MificaApplication {

    // ICP-TOTAL: 0
    // Classe de bootstrap: apenas inicializa o contexto Spring Boot e habilita o escaneamento dos pacotes.

    public static void main(String[] args) {
        SpringApplication.run(MificaApplication.class, args);
    }
}
