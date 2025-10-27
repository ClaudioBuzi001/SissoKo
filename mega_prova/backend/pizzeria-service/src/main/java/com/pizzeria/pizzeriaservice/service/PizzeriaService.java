package com.pizzeria.pizzeriaservice.service;

import com.pizzeria.pizzeriaservice.exception.PizzeriaNotFoundException;
import com.pizzeria.pizzeriaservice.geocoding.GeocodingClient;
import com.pizzeria.pizzeriaservice.model.Pizzeria;
import com.pizzeria.pizzeriaservice.repository.PizzeriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PizzeriaService {

    private final PizzeriaRepository repository;
    private final GeocodingClient geocodingClient;

    public PizzeriaService(PizzeriaRepository repository, GeocodingClient geocodingClient) {
        this.repository = repository;
        this.geocodingClient = geocodingClient;
    }

    public List<Pizzeria> findAll() {
        return repository.findAll();
    }

    public Pizzeria findById(String id) {
        return repository.findById(id).orElseThrow(() -> new PizzeriaNotFoundException(id));
    }

    public Pizzeria create(Pizzeria pizzeria) {
        applyGeocodingIfMissing(pizzeria);
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
        existing.setLatitude(update.getLatitude());
        existing.setLongitude(update.getLongitude());
        applyGeocodingIfMissing(existing);
        return repository.save(existing);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new PizzeriaNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private void applyGeocodingIfMissing(Pizzeria target) {
        if (target.getLatitude() != null && target.getLongitude() != null) {
            return;
        }
        if (target.getAddress() == null || target.getAddress().isBlank()) {
            return;
        }

        geocodingClient.geocode(target.getAddress(), target.getCity())
                .ifPresent(coords -> {
                    target.setLatitude(coords.latitude());
                    target.setLongitude(coords.longitude());
                });
    }
}
