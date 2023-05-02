package edu.awieclawski.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import edu.awieclawski.data.configs.TransactionsConfig;
/**
 * For spring unit tests purposes only
 * 
 * @author awieclawski
 *
 */
@EnableJpaRepositories("edu.awieclawski.data.daos")
@EntityScan("edu.awieclawski.models.entities")
@SpringBootApplication(scanBasePackages = "edu.awieclawski.data")
public class RepositoriesTestApp {
	public static void main(String[] args) {
		SpringApplication.run(new Class<?>[]{RepositoriesTestApp.class, TransactionsConfig.class}, args);
	}
}
