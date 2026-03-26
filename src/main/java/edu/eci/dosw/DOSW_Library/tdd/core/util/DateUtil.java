package edu.eci.dosw.DOSW_Library.tdd.core.util;

import java.time.LocalDate;

public class DateUtil {
    private DateUtil() {
    }

    public static LocalDate today() {
        return LocalDate.now();
    }
}
