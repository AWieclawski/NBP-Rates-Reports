package edu.awieclawski.htmltopdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * For spring unit tests purposes only
 * @author awieclawski
 *
 */
@SpringBootApplication(scanBasePackages = {"edu.awieclawski.htmltopdf"})
public class HtmltopdfApplication {

	public static void main(String[] args) {
		SpringApplication.run(HtmltopdfApplication.class, args);
	}

}
