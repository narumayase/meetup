package com.baglietto.client;

import com.baglietto.restservice.dto.WeatherDTO;
import com.baglietto.utils.DateUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

//https://rapidapi.com/weatherbit/api/weather

@Service
public class WeatherClient {

    public WeatherDTO getSpecificDayWeather(Date date) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String uri = "https://weatherbit-v1-mashape.p.rapidapi.com/forecast/daily?lat=-34.6131500&lon=-58.3772300&units=M&lang=en";

        ResponseEntity<Root> response = restTemplate.exchange(uri, HttpMethod.GET, addHeaders(), Root.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Root body = response.getBody();
            if (body != null) {
                for (Data d : body.getData()) {
                    if (DateUtils.isSameDay(new SimpleDateFormat("yyyy-MM-dd").parse(d.getValid_date()), date)) {
                        return new WeatherDTO(d.getTemp());
                    }
                }
            }
        } else {
            throw new Exception("Ocurrió un error con el cliente");
        }
        return null;
    }

    private HttpEntity<String> addHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", "e24c4d04a6msh53ffe62f3e61455p1df251jsn89fe791e2436");
        headers.set("x-rapidapi-host", "weatherbit-v1-mashape.p.rapidapi.com");
        return new HttpEntity<>(headers);
    }
}


