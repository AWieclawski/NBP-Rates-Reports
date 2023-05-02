package edu.awieclawski.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * For spring unit tests purposes only
 * @author awieclawski
 *
 */
@SpringBootApplication(scanBasePackages = "edu.awieclawski")
public class CoreTestApp {
	public static void main(String[] args) {
		SpringApplication.run(CoreTestApp.class, args);
	}
}
