package com.pizzeria.pizzeriaservice.controller;

import com.pizzeria.pizzeriaservice.model.Pizzeria;
import com.pizzeria.pizzeriaservice.service.PizzeriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pizzerias")
public class PizzeriaController {

    private final PizzeriaService service;

    public PizzeriaController(PizzeriaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pizzeria> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Pizzeria findOne(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pizzeria create(@Valid @RequestBody Pizzeria pizzeria) {
        return service.create(pizzeria);
    }

    @PutMapping("/{id}")
    public Pizzeria update(@PathVariable String id, @Valid @RequestBody Pizzeria pizzeria) {
        return service.update(id, pizzeria);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
