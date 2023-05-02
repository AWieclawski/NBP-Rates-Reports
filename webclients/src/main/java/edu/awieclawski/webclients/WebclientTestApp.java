package edu.awieclawski.webclients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * For spring unit tests purposes only
 * 
 * @author awieclawski
 *
 */
@SpringBootApplication(scanBasePackages = { "edu.awieclawski.webclients", "edu.awieclawski.commons" })
public class WebclientTestApp {

	public static void main(String[] args) {
		SpringApplication.run(WebclientTestApp.class, args);
	}

}
