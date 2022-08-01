package com.example.demo;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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

		public Mono<GreetingResponse> greetOnce(GreetingRequest request){
			return Mono.just(greet(request.getName()));
		}

		public Flux<GreetingResponse> greetMany(GreetingRequest request){
			return Flux.fromStream(Stream.generate(() -> greet(request.getName())))
					.delayElements(Duration.ofSeconds(1))
					.subscribeOn(Schedulers.boundedElastic());
		}
	}


	@Bean
	public RouterFunction<ServerResponse> routes(GreetingService gs){
		return RouterFunctions.route()
				.GET("/greeting/{name}", r -> 
					ServerResponse.ok()
						.body(gs.greetOnce(new GreetingRequest(r.pathVariable("name"))), GreetingResponse.class))
				.GET("/greetings/{name}", r -> 
					ServerResponse.ok()
						.contentType(MediaType.TEXT_EVENT_STREAM)
						.body(gs.greetMany(new GreetingRequest(r.pathVariable("name"))), GreetingResponse.class))
				.build();
	}

}
