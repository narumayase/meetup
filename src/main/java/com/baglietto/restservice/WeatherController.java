package com.baglietto.restservice;

import com.baglietto.restservice.dto.MeetupDTO;
import com.baglietto.restservice.dto.WeatherDTO;
import com.baglietto.restservice.utils.Validation;
import com.baglietto.service.AuthorizationService;
import com.baglietto.service.WeatherService;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/weather")
@RestController
public class WeatherController {

    private final WeatherService service;
    private final AuthorizationService authorizationService;

    public WeatherController(WeatherService service, AuthorizationService authorizationService) {
        this.service = service;
        this.authorizationService = authorizationService;
    }

    @GetMapping()
    public WeatherDTO getWeather(@RequestHeader("authorization") String token, @RequestParam(value = "date") String date) throws Exception {
        try {
            authorizationService.isOk("all", token);

            MeetupDTO meetup = new MeetupDTO(date);
            Validation.validateMeetup(meetup);

            return service.getWeather(date);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
