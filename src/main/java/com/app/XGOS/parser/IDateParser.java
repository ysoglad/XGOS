package com.app.XGOS.parser;

import java.time.LocalDate;

public interface IDateParser {

    LocalDate parseDate(String supposedDate);

    LocalDate adjustDate(LocalDate localDate);
}
