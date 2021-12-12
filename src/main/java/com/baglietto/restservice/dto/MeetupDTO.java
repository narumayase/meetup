package com.baglietto.restservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MeetupDTO {

    private Integer id;
    private Integer boxes;
    private Double temperature;
    private String description;
    private String date;

    private List<UserDTO> guests = new ArrayList<>();
    private Integer guestsAmount;

    public MeetupDTO() {
    }

    public MeetupDTO(Integer id) {
        this.id = id;
    }

    public MeetupDTO(String date, Integer guestsAmount) {
        this.date = date;
        this.guestsAmount = guestsAmount;
    }

    public MeetupDTO(Integer boxes, Double temperature, String description, String date) {
        this.boxes = boxes;
        this.temperature = temperature;
        this.description = description;
        this.date = date;
    }

    public MeetupDTO(Integer boxes, Double temperature, String description, String date, Integer guestsAmount) {
        this.boxes = boxes;
        this.temperature = temperature;
        this.description = description;
        this.date = date;
        this.guestsAmount = guestsAmount;
    }

    public MeetupDTO(String date) {
        this.date = date;
    }

    public MeetupDTO(Integer id, Integer boxes, Double temperature, String description, String date) {
        this.id = id;
        this.boxes = boxes;
        this.temperature = temperature;
        this.description = description;
        this.date = date;
    }

    public MeetupDTO(String description, List<UserDTO> guests, String date) {
        this.description = description;
        this.date = date;
        this.guests = guests;
    }

    public void addGuest(UserDTO user) {
        guests.add(user);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getBoxes() {
        return boxes;
    }

    public void setBoxes(Integer boxes) {
        this.boxes = boxes;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public List<UserDTO> getGuests() {
        return guests;
    }

    public void setGuests(List<UserDTO> guests) {
        this.guests = guests;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getGuestsAmount() {
        return guestsAmount;
    }

    public void setGuestsAmount(Integer guestsAmount) {
        this.guestsAmount = guestsAmount;
    }
}