package com.app.XGOS.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class DateParserTest {

    @Test
    void givenParsableDate_whenParse_thanSuccess() {
        LocalDate ld = new DateParser().parseDate("01/01/0001");
        Assertions.assertEquals(ld.toString(), "0001-01-01");
    }

    @Test
    void givenNotParsableDate_whenParse_thanProvideNow() {
        LocalDate beforeDate = LocalDate.now();
        LocalDate expectedResultDate = new DateParser().parseDate(null);
        LocalDate afterDate = LocalDate.now();
        Assertions.assertTrue((expectedResultDate.isAfter(beforeDate) && expectedResultDate.isBefore(afterDate)) || expectedResultDate.isEqual(beforeDate) || expectedResultDate.isEqual(afterDate));
    }

    @Test
    void givenNull_whenParse_thanProvideNow() {
        LocalDate beforeDate = LocalDate.now();
        LocalDate expectedResultDate = new DateParser().parseDate("notParsable");
        LocalDate afterDate = LocalDate.now();
        Assertions.assertTrue((expectedResultDate.isAfter(beforeDate) && expectedResultDate.isBefore(afterDate)) || expectedResultDate.isEqual(beforeDate) || expectedResultDate.isEqual(afterDate));
    }

    @Test
    void givenNull_whenAdjust_throw() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new DateParser().adjustDate(null);
        });
    }

    @Test
    void givenCorrect_whenAdjust_thanSuccess() {
        LocalDate beforeDate = LocalDate.now();
        LocalDate afterDate = new DateParser().adjustDate(beforeDate);
        LocalDate expectedAfterDate = beforeDate.plusDays(3);
        Assertions.assertTrue(afterDate.isEqual(expectedAfterDate));
    }
}