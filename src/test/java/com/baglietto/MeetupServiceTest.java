package com.baglietto;

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
import com.baglietto.service.MeetupService;
import com.baglietto.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MeetupServiceTest {

    @InjectMocks
    private MeetupService meetupService;

    @Mock
    private WeatherClient client;

    @Mock
    private MeetupRepository meetupRepository;

    @Mock
    private UserMeetupRepository userMeetupRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    public void calculateBeersWithTemperatureLessThan20_sucessfully() throws Exception {
        // < 20
        Double temperature = 19.0;

        WeatherDTO weather = new WeatherDTO(temperature);
        Mockito.when(client.getSpecificDayWeather(any())).thenReturn(weather);

        MeetupDTO meetup = new MeetupDTO(getActualDateString(), 25);
        meetupService.calculateBeers(meetup);

        assertThat(meetup.getBoxes()).isEqualTo(4);
        assertThat(meetup.getTemperature()).isEqualTo(temperature);
    }

    @Test
    public void calculateBeersWithTemperature20_sucessfully() throws Exception {
        // = 20
        Double temperature = 20.0;

        WeatherDTO weather = new WeatherDTO(temperature);
        Mockito.when(client.getSpecificDayWeather(any())).thenReturn(weather);

        MeetupDTO meetup = new MeetupDTO(getActualDateString(), 25);
        meetupService.calculateBeers(meetup);

        assertThat(meetup.getBoxes()).isEqualTo(4);
        assertThat(meetup.getTemperature()).isEqualTo(temperature);
    }

    @Test
    public void calculateBeersWithTemperatureMoreThan24_sucessfully() throws Exception {
        // > 24
        Double temperature = 25.0;

        WeatherDTO weather = new WeatherDTO(temperature);
        Mockito.when(client.getSpecificDayWeather(any())).thenReturn(weather);

        MeetupDTO meetup = new MeetupDTO(getActualDateString(), 25);
        meetupService.calculateBeers(meetup);

        assertThat(meetup.getBoxes()).isEqualTo(9);
        assertThat(meetup.getTemperature()).isEqualTo(temperature);
    }

    @Test
    public void calculateBeersWithTemperature24_sucessfully() throws Exception {
        // = 24
        Double temperature = 24.0;

        WeatherDTO weather = new WeatherDTO(temperature);
        Mockito.when(client.getSpecificDayWeather(any())).thenReturn(weather);

        MeetupDTO meetup = new MeetupDTO(getActualDateString(), 25);
        meetupService.calculateBeers(meetup);

        assertThat(meetup.getBoxes()).isEqualTo(9);
        assertThat(meetup.getTemperature()).isEqualTo(temperature);
    }

    private String getActualDateString() {
        return DateUtils.getDateString(Calendar.getInstance().getTime());
    }

    @Test
    public void calculateBeersWithTemperatureBeetween20and24_sucessfully() throws Exception {
        // < 24 && > 20
        Double temperature = 23.0;

        WeatherDTO weather = new WeatherDTO(temperature);
        Mockito.when(client.getSpecificDayWeather(any())).thenReturn(weather);

        MeetupDTO meetup = new MeetupDTO(getActualDateString(), 25);
        meetupService.calculateBeers(meetup);

        assertThat(meetup.getBoxes()).isEqualTo(5);
        assertThat(meetup.getTemperature()).isEqualTo(temperature);
    }

    @Test
    public void calculateBeersWithTemperatureMoreThan24AndInvitedGuests_sucessfully() throws Exception {
        // > 24
        Double temperature = 25.0;

        WeatherDTO weather = new WeatherDTO(temperature);
        Mockito.when(client.getSpecificDayWeather(any())).thenReturn(weather);

        List<UserDTO> guests = getUserDTOs();

        MeetupDTO meetup = new MeetupDTO("just a test", guests, getActualDateString());
        meetupService.calculateBeers(meetup);

        assertThat(meetup.getBoxes()).isEqualTo(4);
        assertThat(meetup.getTemperature()).isEqualTo(temperature);
    }

    @Test(expected = Exception.class)
    public void calculateBeers_errorWithClient() throws Exception {
        Mockito.when(client.getSpecificDayWeather(any())).thenReturn(null);

        meetupService.calculateBeers(new MeetupDTO(getActualDateString(), 25));
    }

    @Test
    public void addMeToAnAlreadyInvitedMeetup_sucessfully() {
        // el usuario ya estaba en esta meetup y se volviò a agregar, no deberìa hacer nada.

        Integer meetupId = 2;
        Integer userId = 3;

        Meetup meetup = new Meetup(meetupId, "test", null, null, new Date());
        Optional<Meetup> entity = Optional.of(meetup);

        Mockito.when(meetupRepository.findById(meetupId)).thenReturn(entity);

        List<UserMeetup> userMeetups = new ArrayList<>();
        userMeetups.add(new UserMeetup(1, userId, meetupId, false));
        Mockito.when(userMeetupRepository.findUserMeetupByMeetupId(meetup.getId())).thenReturn(userMeetups);

        UserMeetup userMeetup = meetupService.addMe(meetupId, userId, false);

        assertThat(userMeetup).isNull();
    }

    @Test
    public void addMeToANotInvitedMeetup_sucessfully() {
        // el usuario no estaba en esta meetup y se agregó.

        Integer meetupId = 5;
        Integer userId = 6;

        Meetup meetup = new Meetup(meetupId, "test", null, null, new Date());
        Optional<Meetup> entity = Optional.of(meetup);

        Mockito.when(meetupRepository.findById(meetupId)).thenReturn(entity);

        List<UserMeetup> userMeetups = new ArrayList<>();
        Mockito.when(userMeetupRepository.findUserMeetupByMeetupId(meetup.getId())).thenReturn(userMeetups);

        UserMeetup userMeetup = meetupService.addMe(meetupId, userId, false);

        assertThat(userMeetup.getCheckin()).isEqualTo(false);
        assertThat(userMeetup.getMeetupId()).isEqualTo(meetupId);
        assertThat(userMeetup.getUserId()).isEqualTo(userId);
    }

    @Test(expected = MeetupNotFoundException.class)
    public void addMeToANotInvitedMeetup_meetupNotFound() {
        Integer meetupId = 5;
        Integer userId = 6;

        Optional<Meetup> entity = Optional.empty();
        Mockito.when(meetupRepository.findById(meetupId)).thenReturn(entity);

        meetupService.addMe(meetupId, userId, false);
    }

    @Test
    public void checkinMeToANotInvitedMeetup_sucessfully() {
        // el usuario no estaba en esta meetup e hizo checkin en ella.

        Integer meetupId = 4;
        Integer userId = 10;

        Meetup meetup = new Meetup(meetupId, "test", null, null, new Date());
        Optional<Meetup> entity = Optional.of(meetup);

        Mockito.when(meetupRepository.findById(meetupId)).thenReturn(entity);

        List<UserMeetup> userMeetups = new ArrayList<>();
        Mockito.when(userMeetupRepository.findUserMeetupByMeetupId(meetup.getId())).thenReturn(userMeetups);

        UserMeetup userMeetup = meetupService.addMe(meetupId, userId, true);

        assertThat(userMeetup.getCheckin()).isEqualTo(true);
        assertThat(userMeetup.getMeetupId()).isEqualTo(meetupId);
        assertThat(userMeetup.getUserId()).isEqualTo(userId);
    }

    @Test
    public void checkinMeToAnInvitedMeetup_sucessfully() {
        // el usuario estaba estaba en esta meetup e hizo checkin en ella.

        Integer meetupId = 3;
        Integer userId = 2;

        Meetup meetup = new Meetup(meetupId, "test", null, null, new Date());
        Optional<Meetup> entity = Optional.of(meetup);

        Mockito.when(meetupRepository.findById(meetupId)).thenReturn(entity);

        List<UserMeetup> userMeetups = new ArrayList<>();
        userMeetups.add(new UserMeetup(1, userId, meetupId, false));
        Mockito.when(userMeetupRepository.findUserMeetupByMeetupId(meetup.getId())).thenReturn(userMeetups);

        UserMeetup userMeetup = meetupService.addMe(meetupId, userId, true);

        assertThat(userMeetup.getCheckin()).isEqualTo(true);
        assertThat(userMeetup.getMeetupId()).isEqualTo(meetupId);
        assertThat(userMeetup.getUserId()).isEqualTo(userId);
    }

    private List<UserDTO> getUserDTOs() {
        List<UserDTO> guests = new ArrayList<>();
        guests.add(new UserDTO("juan", 1));
        guests.add(new UserDTO("juan2", 2));
        guests.add(new UserDTO("juan3", 3));
        guests.add(new UserDTO("juan4", 4));
        guests.add(new UserDTO("juan5", 5));
        guests.add(new UserDTO("juan6", 6));
        guests.add(new UserDTO("juan7", 7));
        guests.add(new UserDTO("juan8", 8));
        guests.add(new UserDTO("juan9", 9));
        guests.add(new UserDTO("juan10", 10));
        return guests;
    }

    private List<User> getUserEntities() {
        List<User> guestsEntity = new ArrayList<>();
        guestsEntity.add(new User(1, "juan", "", "user"));
        guestsEntity.add(new User(2, "juan2", "", "user"));
        guestsEntity.add(new User(3, "juan3", "", "user"));
        guestsEntity.add(new User(4, "juan4", "", "user"));
        guestsEntity.add(new User(5, "juan5", "", "user"));
        guestsEntity.add(new User(6, "juan6", "", "user"));
        guestsEntity.add(new User(7, "juan7", "", "user"));
        guestsEntity.add(new User(8, "juan8", "", "user"));
        guestsEntity.add(new User(9, "juan9", "", "user"));
        guestsEntity.add(new User(10, "juan10", "", "user"));
        return guestsEntity;
    }

    @Test
    public void saveMeetup_sucessfully() throws Exception {
        // = 20
        Double temperature = 20.0;

        Mockito.when(client.getSpecificDayWeather(any())).thenReturn(new WeatherDTO(temperature));

        List<UserDTO> guests = getUserDTOs();

        Date date = Calendar.getInstance().getTime();
        String dateString = new SimpleDateFormat(DateUtils.formatDate).format(date);

        MeetupDTO meetup = new MeetupDTO("let's get some beer", guests, dateString);
        meetup.setTemperature(temperature);
        meetup.setBoxes(2);

        Meetup entity = new Meetup(1, "let's get some beer", 4, temperature, date);
        Mockito.when(meetupRepository.save(any())).thenReturn(entity);

        List<User> guestsEntity = getUserEntities();

        Mockito.when(userRepository.findUserByUsername(any())).thenReturn(guestsEntity);

        meetupService.save(meetup);

        assertThat(meetup.getBoxes()).isEqualTo(2);
        assertThat(meetup.getTemperature()).isEqualTo(temperature);
    }

    @Test(expected = UserNotFoundException.class)
    public void saveMeetup_userNotFound() throws Exception {
        // = 20
        Double temperature = 20.0;

        Mockito.when(client.getSpecificDayWeather(any())).thenReturn(new WeatherDTO(temperature));

        List<UserDTO> guests = getUserDTOs();

        Date date = Calendar.getInstance().getTime();
        String dateString = new SimpleDateFormat(DateUtils.formatDate).format(date);

        MeetupDTO meetup = new MeetupDTO("let's get some beer", guests, dateString);
        meetup.setTemperature(temperature);
        meetup.setBoxes(2);

        Meetup entity = new Meetup(1, "let's get some beer", 4, temperature, date);
        Mockito.when(meetupRepository.save(any())).thenReturn(entity);

        List<User> entities = new ArrayList<>();
        Mockito.when(userRepository.findUserByUsername(any())).thenReturn(entities);

        meetupService.save(meetup);
    }
}
