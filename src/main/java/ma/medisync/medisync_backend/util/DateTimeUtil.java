package ma.medisync.medisync_backend.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateTimeUtil {
    
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static LocalDateTime now() {
        return LocalDateTime.now(UTC);
    }
    
    public static LocalDateTime toUTC(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC).toLocalDateTime();
    }
    
    public static String formatForDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DISPLAY_FORMATTER);
    }
    
    public static String formatISO(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(ISO_FORMATTER);
    }
    
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(now());
    }
    
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(now());
    }
    
    public static long getMinutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.MINUTES.between(start, end);
    }
    
    public static long getHoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.HOURS.between(start, end);
    }
    
    public static long getDaysBetween(java.time.LocalDate start, java.time.LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(start, end);
    }
    
    public static LocalDateTime addDays(LocalDateTime dateTime, int days) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusDays(days);
    }
    
    public static LocalDateTime addHours(LocalDateTime dateTime, int hours) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusHours(hours);
    }
    
    public static LocalDateTime addMinutes(LocalDateTime dateTime, int minutes) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusMinutes(minutes);
    }
}
