package com.baglietto;

import com.baglietto.restservice.WeatherController;
import com.baglietto.restservice.dto.WeatherDTO;
import com.baglietto.service.AuthorizationService;
import com.baglietto.service.WeatherService;
import com.baglietto.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private AuthorizationService authorizationService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void testGetWeather_successfully() throws Exception {
        Mockito.when(authorizationService.isOk("admin", "cde6f7c1-f7f5-49d2-9f88-23c08f9360a8")).thenReturn(true);

        String dateString = DateUtils.getDateString(Calendar.getInstance().getTime());
        WeatherDTO weather = new WeatherDTO(27.0);

        Mockito.when(weatherService.getWeather(dateString)).thenReturn(weather);

        mvc.perform(get("/weather")
                .header("authorization", "cde6f7c1-f7f5-49d2-9f88-23c08f9360a8")
                .param("date", dateString)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("\"temperature\":27.0")));
    }
}
