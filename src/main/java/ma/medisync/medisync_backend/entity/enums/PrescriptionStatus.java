package ma.medisync.medisync_backend.entity.enums;

public enum PrescriptionStatus {
    ACTIVE("Active"),
    EXPIRED("Expired"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private final String displayName;

    PrescriptionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
