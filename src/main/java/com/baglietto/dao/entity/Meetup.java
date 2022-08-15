package com.baglietto.dao.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Meetup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String description;
    private Integer beerBoxes;
    private Double temperature;
    private Date date;

    public Meetup() {
    }

    public Meetup(String description, Integer beerBoxes, Double temperature, Date date) {
        this.description = description;
        this.beerBoxes = beerBoxes;
        this.temperature = temperature;
        this.date = date;
    }

    public Meetup(Integer id, String description, Integer beerBoxes, Double temperature, Date date) {
        this.id = id;
        this.description = description;
        this.beerBoxes = beerBoxes;
        this.temperature = temperature;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getBeerBoxes() {
        return beerBoxes;
    }

    public void setBeerBoxes(Integer beerBoxes) {
        this.beerBoxes = beerBoxes;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
