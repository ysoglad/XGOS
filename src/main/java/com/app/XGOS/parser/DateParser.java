package com.app.XGOS.parser;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class DateParser implements IDateParser {

    @Override
    public LocalDate parseDate(String supposedDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate returnDate = LocalDate.now();
        try {
            returnDate = LocalDate.parse(supposedDate, formatter);
        } catch (NullPointerException | DateTimeParseException ignored) {
        }
        return returnDate;
    }

    @Override
    public LocalDate adjustDate(LocalDate ld) {
        return ld.plusDays(3);
    }
}
