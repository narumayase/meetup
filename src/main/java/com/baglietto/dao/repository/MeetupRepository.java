package com.baglietto.dao.repository;

import com.baglietto.dao.entity.Meetup;
import org.springframework.data.repository.CrudRepository;

public interface MeetupRepository extends CrudRepository<Meetup, Integer> {
}
