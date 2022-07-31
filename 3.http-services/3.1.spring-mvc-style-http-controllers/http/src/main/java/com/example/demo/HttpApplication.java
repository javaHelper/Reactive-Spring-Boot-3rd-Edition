package com.example.demo;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
	
	
	@RestController
	class GreetingController{
		@Autowired
		private GreetingService greetingService;
		
		
		@GetMapping("/greetings/{name}")
		public Mono<GreetingResponse> greet(@PathVariable("name") String name){
			return this.greetingService.greet(new GreetingRequest(name));
		}
	}
	
}
