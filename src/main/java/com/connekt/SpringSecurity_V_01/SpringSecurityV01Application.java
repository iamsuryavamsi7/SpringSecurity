package com.connekt.SpringSecurity_V_01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringSecurityV01Application {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(SpringSecurityV01Application.class, args);

		System.out.println("\n\n\nProject is fine ... \n\n\n");

	}

}
