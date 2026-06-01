package ma.medisync.medisync_backend.entity.enums;

public enum DayOfWeekEnum {
    MONDAY("Monday", 1),
    TUESDAY("Tuesday", 2),
    WEDNESDAY("Wednesday", 3),
    THURSDAY("Thursday", 4),
    FRIDAY("Friday", 5),
    SATURDAY("Saturday", 6),
    SUNDAY("Sunday", 7);

    private final String displayName;
    private final int dayValue;

    DayOfWeekEnum(String displayName, int dayValue) {
        this.displayName = displayName;
        this.dayValue = dayValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDayValue() {
        return dayValue;
    }
}
