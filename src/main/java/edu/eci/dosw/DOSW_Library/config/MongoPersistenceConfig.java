package edu.eci.dosw.DOSW_Library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@Profile("mongo")
@EnableMongoRepositories(basePackages = "edu.eci.dosw.DOSW_Library.tdd.persistence.repository.mongo")
public class MongoPersistenceConfig {
}
