package com.re.ss17.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "menus")
public class Menu {

    @Id
    private Long id;

    @Column(name = "dish_name", nullable = false)
    private String dishName;

    @Column(name = "price", nullable = false)
    private Long price;

    protected Menu() {
    }

    public Menu(Long id, String dishName, Long price) {
        this.id = id;
        this.dishName = dishName;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getDishName() {
        return dishName;
    }

    public Long getPrice() {
        return price;
    }
}
