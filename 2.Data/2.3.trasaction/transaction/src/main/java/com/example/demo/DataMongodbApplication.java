package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

import io.r2dbc.spi.ConnectionFactory;

@SpringBootApplication
@EnableTransactionManagement
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
	
	@Bean
	public ReactiveTransactionManager r2dbcReactiveTransactionManager(ConnectionFactory cf) {
		return new R2dbcTransactionManager(cf);
	}
	
	@Bean
	public TransactionalOperator transactionalOperator(ReactiveTransactionManager rtm) {
		return TransactionalOperator.create(rtm);
	}
}
