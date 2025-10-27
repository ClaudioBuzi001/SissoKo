package com.pizzeria.pizzeriaservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PizzeriaNotFoundException extends RuntimeException {

    public PizzeriaNotFoundException(String id) {
        super("Pizzeria not found with id: " + id);
    }
}
