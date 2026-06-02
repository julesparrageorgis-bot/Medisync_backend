-- Development-only accounts. This location is enabled only by the local profile.
INSERT IGNORE INTO users (
    id, user_type, email, password, first_name, last_name, user_role, is_active,
    created_at, updated_at, two_factor_enabled, two_factor_verified
) VALUES
    (900001, 'User', 'admin@medisync.local', '$2y$10$nDUyyCush/JruVbeW0Z1F.uX8S8XerL5vfV9YjjTxa3Vs86aqOwOa', 'Admin', 'Local', 'ADMIN', TRUE, NOW(6), NOW(6), FALSE, FALSE),
    (900002, 'DOCTOR', 'doctor@medisync.local', '$2y$10$sPSC5u2ZKNDO4HVm7MdvjORfR5Bab0me6NI2pBE.Z1WCOasmSArye', 'Doctor', 'Local', 'DOCTOR', TRUE, NOW(6), NOW(6), FALSE, FALSE),
    (900003, 'User', 'secretary@medisync.local', '$2y$10$9VxyR3vVL4crAUutbapXSuc5b/Smi9xy5COAIjeIZFF4ivEy1Qkym', 'Secretary', 'Local', 'SECRETARY', TRUE, NOW(6), NOW(6), FALSE, FALSE);

INSERT IGNORE INTO offices (
    id, name, address, city, country, phone, email, is_active, description, created_at
) VALUES (
    900001, 'MediSync Local Clinic', '1 Development Street', 'Casablanca', 'Morocco',
    '+212500000000', 'clinic@medisync.local', TRUE, 'Local development clinic', NOW()
);

INSERT IGNORE INTO doctors (
    id, license_number, specialties, languages, consultation_rate, office_id
) VALUES (
    900002, 'LOCAL-DOCTOR-001', '["GENERAL_MEDICINE"]', '["FRENCH","ARABIC"]', 250.00, 900001
);

INSERT IGNORE INTO admins (
    user_id, admin_level, can2fa, two_factor_enabled, permissions,
    can_access_reports, can_manage_users, can_manage_clinic
) VALUES (
    900001, 'SUPER_ADMIN', TRUE, FALSE, 'LOCAL_DEVELOPMENT',
    TRUE, TRUE, TRUE
);

INSERT IGNORE INTO secretaries (
    user_id, office_assigned, department, can_manage_appointments,
    can_manage_patients, can_generate_invoices, work_schedule
) VALUES (
    900003, 'MediSync Local Clinic', 'Reception', TRUE, TRUE, TRUE, 'Monday-Friday 9AM-5PM'
);
