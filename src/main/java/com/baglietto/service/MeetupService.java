package com.baglietto.service;

import com.baglietto.client.WeatherClient;
import com.baglietto.dao.entity.Meetup;
import com.baglietto.dao.entity.User;
import com.baglietto.dao.entity.UserMeetup;
import com.baglietto.dao.repository.MeetupRepository;
import com.baglietto.dao.repository.UserMeetupRepository;
import com.baglietto.dao.repository.UserRepository;
import com.baglietto.exception.MeetupNotFoundException;
import com.baglietto.exception.UserNotFoundException;
import com.baglietto.restservice.dto.MeetupDTO;
import com.baglietto.restservice.dto.UserDTO;
import com.baglietto.restservice.dto.WeatherDTO;
import com.baglietto.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MeetupService {

    public final Integer beersPerBox = 6;

    private final WeatherClient weatherClient;

    private final MeetupRepository meetupRepository;

    private final UserMeetupRepository userMeetupRepository;

    private final UserRepository userRepository;

    public MeetupService(WeatherClient weatherClient,
                         MeetupRepository meetupRepository,
                         UserMeetupRepository userMeetupRepository,
                         UserRepository userRepository) {
        this.weatherClient = weatherClient;
        this.meetupRepository = meetupRepository;
        this.userMeetupRepository = userMeetupRepository;
        this.userRepository = userRepository;
    }

    /**
     * Agregar una meetup.
     *
     * @param meetup
     * @return MeetupDTO
     * @throws Exception
     */
    public MeetupDTO save(MeetupDTO meetup) throws Exception {
        calculateBeers(meetup);

        Meetup entity = meetupRepository.save(new Meetup(
                meetup.getDescription(),
                meetup.getBoxes(),
                meetup.getTemperature(),
                DateUtils.getDateFromString(meetup.getDate())));

        List<Integer> userIds = getUserIds(meetup.getGuests());

        for (Integer id : userIds) {
            userMeetupRepository.save(new UserMeetup(id, entity.getId(), false));
        }
        return meetup;
    }

    /**
     * Agrega al usuario a la meetup solo si no estaba ya invitado a la misma. En el caso de estar invitado y haber cambiado el checkin, actualiza el registro.
     *
     * @param meetupId
     * @param userId
     * @param checkin
     */
    public UserMeetup addMe(Integer meetupId, Integer userId, boolean checkin) {

        Optional<Meetup> entity = meetupRepository.findById(meetupId);
        if (!entity.isPresent()) {
            throw new MeetupNotFoundException("No existe la meetup seleccionada");
        }

        List<UserMeetup> userMeetups = userMeetupRepository.findUserMeetupByMeetupId(meetupId);

        boolean isPresent = false;
        for (UserMeetup um : userMeetups) {
            if (um.getUserId().equals(userId)) {
                // ya existía
                isPresent = true;
                if (!um.getCheckin().equals(checkin)) {
                    UserMeetup userMeetup = new UserMeetup(um.getId(), userId, meetupId, checkin);
                    userMeetupRepository.save(userMeetup);
                    return userMeetup;
                }
            }
        }

        // la primera vez que lo agrega
        if (!isPresent) {
            UserMeetup userMeetup = new UserMeetup(userId, meetupId, checkin);
            userMeetupRepository.save(userMeetup);
            return userMeetup;
        }
        return null;
    }

    /**
     * Según la fecha asignada para la meetup, consulta el clima de ese día (máximo 16 días en adelante) y calcula la cantidad de cajas de cervezas de 6 unidades a comprar.
     *
     * @param meetup
     * @throws Exception
     */
    public MeetupDTO calculateBeers(MeetupDTO meetup) throws Exception {
        WeatherDTO weather = weatherClient.getSpecificDayWeather(DateUtils.getDateFromString(meetup.getDate()));
        if (weather == null) {
            throw new Exception("Ocurrió un error con el cliente.");
        }
        Integer guestsAmount = meetup.getGuestsAmount();
        if (guestsAmount == null) {
            guestsAmount = meetup.getGuests().size();
        }
        double beers;
        double boxes = 1;

        if (weather.getTemperature() < 24 && weather.getTemperature() > 20) {
            beers = guestsAmount;
        } else if (weather.getTemperature() <= 20) {
            beers = guestsAmount * 0.75;
        } else {
            beers = guestsAmount * 2;
        }
        if (beers >= beersPerBox) {
            boxes = beers / beersPerBox;
        }
        // mejor que sobre a que falte
        meetup.setBoxes((int) Math.round(boxes + 0.5));
        meetup.setTemperature(weather.getTemperature());
        return meetup;
    }

    /**
     * Obtener los ids de los usuarios seleccionados.
     *
     * @param users
     * @return List<Integer>
     */
    private List<Integer> getUserIds(List<UserDTO> users) {
        List<Integer> userIds = new ArrayList<>();
        for (UserDTO user : users) {
            List<User> u = userRepository.findUserByUsername(user.getUsername());

            if (u.size() == 0) {
                throw new UserNotFoundException("No existe el usuario " + user.getUsername());
            }
            userIds.add(u.get(0).getId());
        }
        return userIds;
    }

    /**
     * Obtener todas las meetups.
     *
     * @return
     */
    public Iterable<MeetupDTO> getAll() {

        Iterable<Meetup> meetups = meetupRepository.findAll();
        List<MeetupDTO> dtos = new ArrayList<>();

        for (Meetup m : meetups) {
            MeetupDTO newMetup = new MeetupDTO(m.getId(), m.getBeerBoxes(), m.getTemperature(), m.getDescription(), m.getDate().toString());
            List<UserMeetup> userMeetups = userMeetupRepository.findUserMeetupByMeetupId(m.getId());

            for (UserMeetup um : userMeetups) {
                Optional<User> user = userRepository.findById(um.getUserId());
                user.ifPresent(value -> newMetup.addGuest(new UserDTO(value.getUsername(), um.getCheckin())));
            }
            dtos.add(newMetup);
        }
        return dtos;
    }
}