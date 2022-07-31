package com.example.demo.init;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.demo.model.Reservation;
import com.example.demo.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class SampleDataInitializer {

	private final ReservationRepository repository;
	
	@EventListener(ApplicationReadyEvent.class)
	public void ready() {
		Flux<Reservation> reservationFlux = Flux.just("Laxmi", "Prateek", "Anosh", "Rajesh", "Bhupalee", "Neha", "Aravind")
			.map(name -> new Reservation(null, name))
			.flatMap(this.repository::save);
		
		this.repository.deleteAll()
			.thenMany(reservationFlux)
			.thenMany(this.repository.findAll())
			.subscribe(System.out::println);
	}
}
