package com.pizzeria.pizzeriaservice.service;

import com.pizzeria.pizzeriaservice.exception.PizzeriaNotFoundException;
import com.pizzeria.pizzeriaservice.model.Pizzeria;
import com.pizzeria.pizzeriaservice.repository.PizzeriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PizzeriaService {

    private final PizzeriaRepository repository;

    public PizzeriaService(PizzeriaRepository repository) {
        this.repository = repository;
    }

    public List<Pizzeria> findAll() {
        return repository.findAll();
    }

    public Pizzeria findById(String id) {
        return repository.findById(id).orElseThrow(() -> new PizzeriaNotFoundException(id));
    }

    public Pizzeria create(Pizzeria pizzeria) {
        return repository.save(pizzeria);
    }

    public Pizzeria update(String id, Pizzeria update) {
        Pizzeria existing = findById(id);
        existing.setName(update.getName());
        existing.setAddress(update.getAddress());
        existing.setCity(update.getCity());
        existing.setPhoneNumber(update.getPhoneNumber());
        existing.setOpeningHours(update.getOpeningHours());
        existing.setDeliveryAvailable(update.isDeliveryAvailable());
        return repository.save(existing);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new PizzeriaNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
