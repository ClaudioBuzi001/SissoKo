package com.pizzeria.pizzeriaservice.config;

import com.pizzeria.pizzeriaservice.model.Pizzeria;
import com.pizzeria.pizzeriaservice.repository.PizzeriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!test")
public class PizzeriaDataInitializer implements CommandLineRunner {

    private final PizzeriaRepository repository;

    public PizzeriaDataInitializer(PizzeriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        List<Pizzeria> samples = List.of(
                new Pizzeria(
                        "La Bella Napoli",
                        "Via Roma 123",
                        "Milano",
                        "+39 02 1234 5678",
                        "Lun-Dom 12:00-23:00",
                        true
                ),
                new Pizzeria(
                        "Forno Romano",
                        "Piazza Navona 45",
                        "Roma",
                        "+39 06 9876 5432",
                        "Mar-Dom 11:30-22:30",
                        true
                ),
                new Pizzeria(
                        "Sapori di Napoli",
                        "Corso Italia 78",
                        "Torino",
                        "+39 011 2468 1357",
                        "Lun-Sab 12:00-21:30",
                        false
                )
        );

        repository.saveAll(samples);
    }
}
