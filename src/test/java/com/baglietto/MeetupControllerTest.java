package com.baglietto;

import com.baglietto.dao.entity.UserMeetup;
import com.baglietto.exception.TokenNotFoundException;
import com.baglietto.exception.UserWithoutRoleException;
import com.baglietto.restservice.MeetupController;
import com.baglietto.restservice.dto.MeetupDTO;
import com.baglietto.restservice.dto.UserDTO;
import com.baglietto.service.AuthorizationService;
import com.baglietto.service.MeetupService;
import com.baglietto.utils.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(SpringRunner.class)
@WebMvcTest(MeetupController.class)
public class MeetupControllerTest {

    @MockBean
    private MeetupService meetupService;

    @MockBean
    private AuthorizationService authorizationService;

    @Autowired
    private MockMvc mvc;

    private static final String tokenString = "cde6f7c1-f7f5-49d2-9f88-23c08f9360a8";

    @Test
    public void testCalculateBeers_successfully() throws Exception {
        Mockito.when(authorizationService.isOk("admin", tokenString)).thenReturn(true);

        String dateString = DateUtils.getDateString(Calendar.getInstance().getTime());
        MeetupDTO meetup = getMeetupDTOWithoutGuests(dateString);

        Mockito.when(meetupService.calculateBeers(any())).thenReturn(meetup);

        mvc.perform(get("/meetup/beers")
                .header("authorization", tokenString)
                .param("date", dateString)
                .param("guests", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("\"guestsAmount\":10")))
                .andExpect(content().string(containsString("\"boxes\":2")))
                .andExpect(content().string(containsString("\"temperature\":19.0")));
    }

    @Test
    public void testCalculateBeers_badDateFormat() throws Exception {
        Mockito.when(authorizationService.isOk("admin", tokenString)).thenReturn(true);

        String dateString = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());

        mvc.perform(get("/meetup/beers")
                .header("authorization", tokenString)
                .param("date", dateString)
                .param("guests", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCalculateBeers_isPastDate() throws Exception {
        Mockito.when(authorizationService.isOk("admin", tokenString)).thenReturn(true);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = DateUtils.getDateString(cal.getTime());

        mvc.perform(get("/meetup/beers")
                .header("authorization", tokenString)
                .param("date", yesterday)
                .param("guests", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCalculateBeers_is16DaysInFutureDate() throws Exception {
        Mockito.when(authorizationService.isOk("admin", tokenString)).thenReturn(true);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 16);
        String dateString = DateUtils.getDateString(cal.getTime());

        mvc.perform(get("/meetup/beers")
                .header("authorization", tokenString)
                .param("date", dateString)
                .param("guests", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCalculateBeers_userWithoutCorrectRole() throws Exception {
        // en el caso de que el usuario no tenga los roles necesarios devuelve una UserWithoutRoleException
        Mockito.when(authorizationService.isOk(eq("admin"), eq(tokenString))).thenThrow(UserWithoutRoleException.class);

        mvc.perform(get("/meetup/beers")
                .header("authorization", tokenString)
                .param("date", DateUtils.getDateString(Calendar.getInstance().getTime()))
                .param("guests", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateMeetup_successfully() throws Exception {
        Mockito.when(authorizationService.isOk(any(), any())).thenReturn(true);

        MeetupDTO meetup = getMeetupDTOWithGuests(DateUtils.getDateString(Calendar.getInstance().getTime()));

        Mockito.when(meetupService.calculateBeers(any())).thenReturn(meetup);

        mvc.perform(post("/meetup")
                .header("authorization", tokenString)
                .content(asJsonString(meetup))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private MeetupDTO getMeetupDTOWithoutGuests(String dateString) {
        return new MeetupDTO(2, 19.0, "just a test", dateString, 10);
    }

    private MeetupDTO getMeetupDTOWithGuests(String dateString) {
        MeetupDTO meetup = new MeetupDTO(1, 19.0, "just another test", dateString);
        meetup.addGuest(new UserDTO("juan.padilla"));
        return meetup;
    }

    @Test
    public void testCreateMeetup_userWithoutCorrectRole() throws Exception {
        // en el caso de que el usuario no tenga los roles necesarios devuelve una UserWithoutRoleException
        Mockito.when(authorizationService.isOk("admin", tokenString)).thenThrow(UserWithoutRoleException.class);

        MeetupDTO meetup = getMeetupDTOWithGuests(DateUtils.getDateString(Calendar.getInstance().getTime()));

        mvc.perform(post("/meetup")
                .header("authorization", tokenString)
                .content(asJsonString(meetup))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAddMeToMeetup_successfully() throws Exception {
        Mockito.when(authorizationService.isOk("all", tokenString)).thenReturn(true);

        UserDTO userDTO = new UserDTO("juan.padilla", 1);

        Mockito.when(authorizationService.getUserByToken(tokenString)).thenReturn(userDTO);

        MeetupDTO meetup = new MeetupDTO(1);

        UserMeetup um = new UserMeetup(1, 1, 1, false);
        Mockito.when(meetupService.addMe(1, 1, false)).thenReturn(um);

        mvc.perform(patch("/meetup")
                .header("authorization", tokenString)
                .content(asJsonString(meetup))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddMeToMeetup_withBadToken() throws Exception {
        // en el caso de que el token no existe devuelve una TokenNotFoundException
        Mockito.when(authorizationService.isOk("all", tokenString)).thenThrow(TokenNotFoundException.class);

        mvc.perform(patch("/meetup")
                .header("authorization", tokenString)
                .content(asJsonString(new MeetupDTO()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCheckinToMeetup_successfully() throws Exception {
        Mockito.when(authorizationService.isOk("all", tokenString)).thenReturn(true);

        UserDTO userDTO = new UserDTO("juan.padilla", 1);

        Mockito.when(authorizationService.getUserByToken(tokenString)).thenReturn(userDTO);

        MeetupDTO meetup = new MeetupDTO(1);

        UserMeetup um = new UserMeetup(1, 1, 1, true);
        Mockito.when(meetupService.addMe(1, 1, true)).thenReturn(um);

        mvc.perform(patch("/meetup/checkin")
                .header("authorization", tokenString)
                .content(asJsonString(meetup))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
