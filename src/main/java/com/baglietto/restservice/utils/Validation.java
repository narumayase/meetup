package com.baglietto.restservice.utils;

import com.baglietto.exception.BadFormatException;
import com.baglietto.exception.PresentDateException;
import com.baglietto.restservice.dto.MeetupDTO;
import com.baglietto.utils.DateUtils;

import java.text.ParseException;
import java.util.Date;

public class Validation {

    /**
     * validaciones al request de meetup.
     *
     * @param meetup
     */
    public static void validateMeetup(MeetupDTO meetup) {
        try {
            Date meetupDate = DateUtils.getDateFromString(meetup.getDate());
            if (!DateUtils.isToday(meetupDate) && (DateUtils.isBeforeDay(meetupDate, new Date()) || !DateUtils.isWithinDaysFuture(meetupDate, 15))) {
                throw new PresentDateException("La fecha elegida debe ser hoy o una fecha futura con un máximo de 15 días.");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new BadFormatException("Formato de la fecha inválido. Formato válido: " + DateUtils.formatDate);
        }
    }
}
