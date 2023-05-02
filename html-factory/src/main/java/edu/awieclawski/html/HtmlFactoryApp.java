package edu.awieclawski.html;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "edu.awieclawski" })
public class HtmlFactoryApp {
	public static void main(String[] args) {
		SpringApplication.run(HtmlFactoryApp.class, args);
	}

}
