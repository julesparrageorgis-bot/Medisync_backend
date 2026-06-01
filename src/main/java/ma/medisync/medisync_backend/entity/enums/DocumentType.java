package ma.medisync.medisync_backend.entity.enums;

public enum DocumentType {
    REPORT("Medical Report"),
    X_RAY("X-Ray"),
    LAB_RESULT("Lab Result"),
    SCAN("Scan"),
    PRESCRIPTION("Prescription"),
    REFERRAL("Referral"),
    OTHER("Other");

    private final String displayName;

    DocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
