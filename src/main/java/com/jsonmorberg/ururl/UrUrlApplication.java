package com.jsonmorberg.ururl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UrUrlApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrUrlApplication.class, args);
	}

}
