package ma.medisync.medisync_backend.entity.enums;

public enum ConsultationType {
    GENERAL("General Consultation"),
    FOLLOW_UP("Follow-up"),
    EMERGENCY("Emergency"),
    ROUTINE_CHECKUP("Routine Checkup"),
    SPECIALIST_REFERRAL("Specialist Referral");

    private final String displayName;

    ConsultationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
