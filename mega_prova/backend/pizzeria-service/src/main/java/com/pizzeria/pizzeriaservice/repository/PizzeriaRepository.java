package com.pizzeria.pizzeriaservice.repository;

import com.pizzeria.pizzeriaservice.model.Pizzeria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PizzeriaRepository extends MongoRepository<Pizzeria, String> {
}
