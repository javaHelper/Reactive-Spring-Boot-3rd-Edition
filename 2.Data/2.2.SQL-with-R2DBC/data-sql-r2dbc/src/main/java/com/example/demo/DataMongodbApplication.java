package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.r2dbc.core.DatabaseClient;

import io.r2dbc.spi.ConnectionFactory;

@SpringBootApplication
public class DataMongodbApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataMongodbApplication.class, args);
	}

	@Bean
	DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
	    return DatabaseClient.builder()
	        .connectionFactory(connectionFactory)
	        //.bindMarkers(() -> BindMarkersFactory.named(":", "", 20).create())
	        .namedParameters(true)
	        .build();
	}
}
