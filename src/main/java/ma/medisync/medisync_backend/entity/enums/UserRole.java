package ma.medisync.medisync_backend.entity.enums;

public enum UserRole {
    PATIENT("Patient"),
    DOCTOR("Doctor"),
    SECRETARY("Secretary"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
