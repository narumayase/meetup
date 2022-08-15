package com.baglietto.restservice;

import com.baglietto.restservice.dto.MeetupDTO;
import com.baglietto.restservice.utils.Validation;
import com.baglietto.service.AuthorizationService;
import com.baglietto.service.MeetupService;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/meetup")
@RestController
public class MeetupController {

    private final MeetupService service;

    private final AuthorizationService authorizationService;

    public MeetupController(MeetupService service, AuthorizationService authorizationService) {
        this.service = service;
        this.authorizationService = authorizationService;
    }

    @PostMapping()
    public @ResponseBody
    void add(@RequestHeader("authorization") String token, @RequestBody MeetupDTO meetup) throws Exception {
        try {
            authorizationService.isOk("admin", token);

            Validation.validateMeetup(meetup);

            service.calculateBeers(meetup);
            service.save(meetup);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping(path = "/beers")
    public @ResponseBody
    MeetupDTO calculateBeers(@RequestHeader("authorization") String token, @RequestParam String date, @RequestParam Integer guests) throws Exception {
        try {
            authorizationService.isOk("admin", token);

            MeetupDTO meetup = new MeetupDTO(date, guests);
            Validation.validateMeetup(meetup);

            return service.calculateBeers(meetup);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PatchMapping()
    public @ResponseBody
    void addMeToMeetup(@RequestHeader("authorization") String token, @RequestBody MeetupDTO meetup) {
        try {
            authorizationService.isOk("all", token);
            service.addMe(meetup.getId(), authorizationService.getUserByToken(token).getId(), false);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PatchMapping(path = "/checkin")
    public @ResponseBody
    void checking(@RequestHeader("authorization") String token, @RequestBody MeetupDTO meetup) {
        try {
            authorizationService.isOk("all", token);
            service.addMe(meetup.getId(), authorizationService.getUserByToken(token).getId(), true);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping()
    public @ResponseBody
    Iterable<MeetupDTO> getAll(@RequestHeader("authorization") String token) {
        try {
            authorizationService.isOk("all", token);
            return service.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}