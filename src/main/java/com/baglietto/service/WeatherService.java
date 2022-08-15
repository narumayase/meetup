package com.baglietto.service;

import com.baglietto.client.WeatherClient;
import com.baglietto.restservice.dto.WeatherDTO;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class WeatherService {

    private final WeatherClient weatherClient;

    public WeatherService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    /**
     * Obtener la temperatura específica del día seleccionado.
     *
     * @param dateString formato de fecha yyyy-MM-dd
     * @return WeatherDTO
     * @throws ParseException
     */
    public WeatherDTO getWeather(String dateString) throws Exception {
        return weatherClient.getSpecificDayWeather(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
    }
}
