package org.triple.ClubMiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class ClubMilesApplication {

	public static void main(String[] args) {

		System.out.println("add code for test");
		SpringApplication.run(ClubMilesApplication.class, args);
	}

}
