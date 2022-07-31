package com.example.demo;

import java.time.Instant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class HttpApplication {

	public static void main(String[] args) {
		SpringApplication.run(HttpApplication.class, args);
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

	@Service
	class GreetingService {
		private GreetingResponse greet(String name) {
			return new GreetingResponse("Hello " + name + " @ " + Instant.now());
		}
		
		public Mono<GreetingResponse> greet(GreetingRequest request){
			return Mono.just(greet(request.getName()));
		}
	}
	
	
	@Bean
	public RouterFunction<ServerResponse> routes(GreetingService gs){
		return RouterFunctions.route()
				.GET("/greetings/{name}", r -> 
					ServerResponse.ok().body(gs.greet(new GreetingRequest(r.pathVariable("name"))), GreetingResponse.class))
				.build();
	}
	
}
