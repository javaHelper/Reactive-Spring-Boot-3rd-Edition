package com.example.demo;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class GreetingClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreetingClientApplication.class, args);
	}
	
	@Bean
	public WebClient webClient() {
		return WebClient.builder().baseUrl("http://localhost:8080").build();
	}
}



@Component
@RequiredArgsConstructor
class Client{
	private final WebClient webClient;
	private final ReactiveCircuitBreakerFactory circuitBreakerFactory;
	
	@EventListener(ApplicationReadyEvent.class)
	public void ready() {
		var name = "Spring Fans!";
		
		ReactiveCircuitBreaker reactiveCircuitBreaker = circuitBreakerFactory.create("greeting");
		
		reactiveCircuitBreaker.run(this.webClient
				.get()
				.uri("/greeting/{name}", name)
				.retrieve()
				.bodyToMono(GreetingResponse.class)
				.timeout(Duration.ofSeconds(10))
				.map(GreetingResponse::getMessage),
				throwable -> Mono.just("EEEK!")
		).subscribe(gr -> System.out.println("Mono: "+ gr));

		
//		this.webClient
//			.get()
//			.uri("/greetings/{name}", name)
//			.retrieve()
//			.bodyToFlux(GreetingResponse.class)
//			.subscribe(gs -> System.out.println("Flux : "+ gs.getMessage()));
			
	}
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingResponse {
	private String message;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingRequest {
	private String name;
}
