package edu.awieclawski.commons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "edu.awieclawski")
public class CommonsApp {
	public static void main(String[] args) {
		SpringApplication.run(CommonsApp.class, args);
	}

}
