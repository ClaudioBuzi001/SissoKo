package com.pizzeria.pizzeriaservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pizzerias")
public class Pizzeria {

    @Id
    private String id;
    @NotBlank
    @Size(max = 80)
    private String name;
    @NotBlank
    @Size(max = 120)
    private String address;
    @NotBlank
    @Size(max = 60)
    private String city;
    @NotBlank
    @Size(max = 20)
    private String phoneNumber;
    @NotBlank
    @Size(max = 120)
    private String openingHours;
    private boolean deliveryAvailable;

    public Pizzeria() {
    }

    public Pizzeria(String name,
                    String address,
                    String city,
                    String phoneNumber,
                    String openingHours,
                    boolean deliveryAvailable) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.openingHours = openingHours;
        this.deliveryAvailable = deliveryAvailable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public boolean isDeliveryAvailable() {
        return deliveryAvailable;
    }

    public void setDeliveryAvailable(boolean deliveryAvailable) {
        this.deliveryAvailable = deliveryAvailable;
    }
}
