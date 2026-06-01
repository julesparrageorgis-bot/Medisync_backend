package ma.medisync.medisync_backend.entity.enums;

public enum NotificationType {
    APPOINTMENT_REMINDER("Appointment Reminder"),
    APPOINTMENT_CHANGE("Appointment Changed"),
    APPOINTMENT_CANCELLED("Appointment Cancelled"),
    PRESCRIPTION_READY("Prescription Ready"),
    INVOICE_SENT("Invoice Sent"),
    PAYMENT_CONFIRMED("Payment Confirmed"),
    RESULTS_AVAILABLE("Results Available");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
