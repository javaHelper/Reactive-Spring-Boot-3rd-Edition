package com.example.demo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.example.demo.model.Reservation;

import reactor.core.publisher.Flux;

public interface ReservationRepository extends ReactiveCrudRepository<Reservation, String>{
	Flux<Reservation> findByName(String name);
}
