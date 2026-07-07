package com.alg.crowler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CrowlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrowlerApplication.class, args);
	}

}
