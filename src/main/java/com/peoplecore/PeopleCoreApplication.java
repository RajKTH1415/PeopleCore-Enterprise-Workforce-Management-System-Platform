//package com.peoplecore;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.EnableScheduling;
//
//@SpringBootApplication
//@EnableJpaAuditing(
//		auditorAwareRef = "auditorAware"
//)
//@EnableAsync
//@EntityScan("com.peoplecore.module")
//@EnableJpaRepositories("com.peoplecore.repository")
//@EnableScheduling
//public class PeopleCoreApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(PeopleCoreApplication.class, args);
//	}
//
//
//}


package com.peoplecore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.peoplecore.controller",
		"com.peoplecore.service",
		"com.peoplecore.config",
		"com.peoplecore.security",
		"com.peoplecore.repository",
		"com.peoplecore.util",
		"com.peoplecore.exception"
})
@EntityScan(basePackages = {
		"com.peoplecore.module"
})
@EnableJpaRepositories(basePackages = {
		"com.peoplecore.repository"
})
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
@EnableScheduling
public class PeopleCoreApplication {

	public static void main(String[] args) {

		SpringApplication application =
				new SpringApplication(PeopleCoreApplication.class);

		application.setApplicationStartup(
				new BufferingApplicationStartup(2048)
		);

		application.run(args);
	}
}
