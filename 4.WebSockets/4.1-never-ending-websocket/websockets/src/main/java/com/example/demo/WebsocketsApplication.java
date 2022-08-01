package com.example.demo;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class WebsocketsApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketsApplication.class, args);
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

		public Flux<GreetingResponse> greet(GreetingRequest request){
			return Flux.fromStream(Stream.generate(() -> greet(request.getName())))
					.delayElements(Duration.ofSeconds(1));
		}
	}

	@Configuration
	class GreetingWebSocketConfiguration{
		@Bean
		public SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler webSocketHandler) {
			return new SimpleUrlHandlerMapping(Map.of("/ws/greetings", webSocketHandler), 10);
		}


		@Bean
		public WebSocketHandler webSocketHandler(GreetingService greetingService) {
			return new WebSocketHandler() {

				@Override
				public Mono<Void> handle(WebSocketSession session) {
					Flux<WebSocketMessage> webSocketMessageFlux = session.receive()
							.map(WebSocketMessage::getPayloadAsText)
							.map(GreetingRequest::new)
							.flatMap(greetingService::greet)
							.map(GreetingResponse::getMessage)
							.map(session::textMessage)
							.doOnEach(signal -> System.out.println(signal.getType()))
							.doFinally(signal -> System.out.println("Finally : "+ signal.toString()));

					return session.send(webSocketMessageFlux);
				}
			};
		}

		@Bean 
		public WebSocketHandlerAdapter socketHandlerAdapter() {
			return new WebSocketHandlerAdapter();
		}
	}

}
