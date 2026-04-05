package edu.eci.dosw.DOSW_Library.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile("jpa")
@EntityScan(basePackages = "edu.eci.dosw.DOSW_Library.tdd.persistence.relational.entity")
@EnableJpaRepositories(basePackages = "edu.eci.dosw.DOSW_Library.tdd.persistence.relational.repository")
public class JpaPersistenceConfig {
}
