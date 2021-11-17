package com.smarsh.preindex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;

@ComponentScan(basePackages = "com.smarsh.preindex")
@EnableAutoConfiguration
@SpringBootApplication
@EnableRetry
public class PreIndexAppV1Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext appContext = SpringApplication.run(PreIndexAppV1Application.class, args);
		
		appContext.close();
	}

}
