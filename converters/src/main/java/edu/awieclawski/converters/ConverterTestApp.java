package edu.awieclawski.converters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * For spring unit tests purposes only
 * @author awieclawski
 *
 */
@SpringBootApplication(scanBasePackages = "edu.awieclawski.converters")
public class ConverterTestApp {
    public static void main(String[] args) {
        SpringApplication.run(ConverterTestApp.class, args);
    }
}