package org.imec.ivlab.core.jcommander;

import com.beust.jcommander.IStringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class LocalDateConverter implements IStringConverter<LocalDate> {

    public LocalDate convert(String value) {

        if (value == null) {
            return null;
        }

        return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    }

}
