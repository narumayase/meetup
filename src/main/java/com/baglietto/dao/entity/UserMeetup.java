package com.baglietto.dao.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserMeetup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer userId;
    private Integer meetupId;
    private Boolean checkin;

    public UserMeetup() {
    }

    public UserMeetup(Integer id, Integer userId, Integer meetupId, Boolean checkin) {
        this.id = id;
        this.userId = userId;
        this.meetupId = meetupId;
        this.checkin = checkin;
    }

    public UserMeetup(Integer userId, Integer meetupId, Boolean checkin) {
        this.userId = userId;
        this.meetupId = meetupId;
        this.checkin = checkin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMeetupId() {
        return meetupId;
    }

    public void setMeetupId(Integer meetupId) {
        this.meetupId = meetupId;
    }

    public Boolean getCheckin() {
        return checkin;
    }

    public void setCheckin(Boolean checkin) {
        this.checkin = checkin;
    }
}
