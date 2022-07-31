package com.example.demo.init;

import java.util.function.BiFunction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import com.example.demo.model.Reservation;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.service.ReservationService;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import reactor.core.publisher.Flux;

@Component
public class SampleDataInitializer {

	@Autowired
	private  ReservationRepository repository;
	@Autowired
	private DatabaseClient databaseClient;
	@Autowired
	private ReservationService reservationService;

	public static final BiFunction<Row, RowMetadata, Reservation> MAPPING_FUNCTION = (row, rowMetaData) -> Reservation.builder()
			.id(row.get("id", Long.class))
			.name(row.get("name", String.class))
			.build();

	@EventListener(ApplicationReadyEvent.class)
	public void ready() {

		this.databaseClient
			.sql("select * from reservation")
			.map(MAPPING_FUNCTION)
			.all()
			.subscribe(System.out::println);

		Flux<Reservation> reservationFlux = reservationService.
				saveAll("Laxmi", "Prateek", "Anosh", "Rajesh", "Bhupalee", "Neha", "Aravind");

		this.repository.deleteAll()
		.thenMany(reservationFlux)
		.thenMany(this.repository.findAll())
		.subscribe(System.out::println);
	}
}
