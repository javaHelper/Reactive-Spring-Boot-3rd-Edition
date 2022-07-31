package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.Assert;

import com.example.demo.model.Reservation;
import com.example.demo.repository.ReservationRepository;

import reactor.core.publisher.Flux;

@Service
public class ReservationService {
	@Autowired
	private ReservationRepository repository;
	@Autowired
	private TransactionalOperator transactionalOperator;
	
	@Transactional
	public Flux<Reservation> saveAll(String... names){
		Flux<Reservation> reservationFlux = Flux.fromArray(names)
				.map(name -> new Reservation(null, name))
				.flatMap(this.repository::save)
				.doOnNext(this::isValid);
		
		// Use this when not using annotations (@Transactional)
		//return this.transactionalOperator.transactional(reservationFlux);
		
		return reservationFlux;
	}
	
	private void isValid(Reservation r) {
		Assert.isTrue(!r.getName().isEmpty() 
				&& Character.isUpperCase(r.getName().charAt(0)), 
				"Name must start with capital letters");
	}
}
