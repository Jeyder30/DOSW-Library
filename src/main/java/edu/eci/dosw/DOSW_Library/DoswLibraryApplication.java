package edu.eci.dosw.DOSW_Library;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.SpringApplication;

@SpringBootApplication(exclude = {
		JpaRepositoriesAutoConfiguration.class,
		MongoRepositoriesAutoConfiguration.class
})
public class DoswLibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoswLibraryApplication.class, args);
	}

}
