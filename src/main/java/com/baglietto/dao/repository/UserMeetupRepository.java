package com.baglietto.dao.repository;

import com.baglietto.dao.entity.UserMeetup;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserMeetupRepository extends CrudRepository<UserMeetup, Integer> {

    List<UserMeetup> findUserMeetupByMeetupId(Integer id);
}
