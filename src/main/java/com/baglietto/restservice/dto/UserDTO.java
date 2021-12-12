package com.baglietto.restservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDTO {

    private String username;
    private String password;
    private String role;
    private Integer id;
    private boolean checkin;

    public UserDTO(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public UserDTO(String username) {
        this.username = username;
    }

    public UserDTO(String username, boolean checkin) {
        this.username = username;
        this.checkin = checkin;
    }

    public UserDTO(String username, Integer id) {
        this.username = username;
        this.id = id;
    }

    public boolean isCheckin() {
        return checkin;
    }

    public void setCheckin(boolean checkin) {
        this.checkin = checkin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
