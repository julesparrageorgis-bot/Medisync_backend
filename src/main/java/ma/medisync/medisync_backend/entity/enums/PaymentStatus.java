package ma.medisync.medisync_backend.entity.enums;

public enum PaymentStatus {
    UNPAID("Unpaid"),
    PAID("Paid"),
    PARTIALLY_PAID("Partially Paid"),
    OVERDUE("Overdue"),
    CANCELLED("Cancelled");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
