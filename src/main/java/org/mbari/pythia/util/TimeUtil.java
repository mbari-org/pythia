package org.mbari.pythia.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class TimeUtil {

    private static final DateTimeFormatter COMPACT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX");

    public static String compactIso8601(TemporalAccessor temporal) {
        return COMPACT_FORMATTER.format(temporal);
    }

    public static String now() {
        return COMPACT_FORMATTER.format(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")));
    }
}
