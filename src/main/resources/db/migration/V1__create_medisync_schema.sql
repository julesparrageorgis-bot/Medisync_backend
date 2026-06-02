CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_type VARCHAR(31) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    user_role VARCHAR(255) NOT NULL,
    is_active BOOLEAN,
    last_login DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    two_factor_secret VARCHAR(255),
    two_factor_enabled BOOLEAN,
    two_factor_verified BOOLEAN,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE offices (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(100),
    zip_code VARCHAR(20),
    city VARCHAR(50),
    country VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    is_active BOOLEAN NOT NULL,
    description TEXT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    PRIMARY KEY (id)
);

CREATE TABLE doctors (
    id BIGINT NOT NULL,
    license_number VARCHAR(50),
    specialties JSON,
    languages JSON,
    consultation_rate DECIMAL(10, 2),
    office_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_doctors_license UNIQUE (license_number),
    CONSTRAINT fk_doctors_user FOREIGN KEY (id) REFERENCES users (id),
    CONSTRAINT fk_doctors_office FOREIGN KEY (office_id) REFERENCES offices (id)
);

CREATE TABLE patients (
    id BIGINT NOT NULL,
    social_security_number VARCHAR(50),
    date_of_birth DATE,
    gender VARCHAR(255),
    blood_type VARCHAR(10),
    allergies JSON,
    address VARCHAR(255),
    city VARCHAR(100),
    zip_code VARCHAR(10),
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    office_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_patients_ssn UNIQUE (social_security_number),
    CONSTRAINT fk_patients_user FOREIGN KEY (id) REFERENCES users (id),
    CONSTRAINT fk_patients_office FOREIGN KEY (office_id) REFERENCES offices (id)
);

CREATE TABLE admins (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    admin_level VARCHAR(100) NOT NULL,
    can2fa BOOLEAN NOT NULL,
    two_factor_secret VARCHAR(255),
    two_factor_enabled BOOLEAN NOT NULL,
    permissions TEXT,
    can_access_reports BOOLEAN NOT NULL,
    can_manage_users BOOLEAN NOT NULL,
    can_manage_clinic BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_admins_user UNIQUE (user_id),
    CONSTRAINT fk_admins_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE secretaries (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    office_assigned VARCHAR(100),
    department VARCHAR(50),
    can_manage_appointments BOOLEAN NOT NULL,
    can_manage_patients BOOLEAN NOT NULL,
    can_generate_invoices BOOLEAN NOT NULL,
    work_schedule VARCHAR(50),
    PRIMARY KEY (id),
    CONSTRAINT uk_secretaries_user UNIQUE (user_id),
    CONSTRAINT fk_secretaries_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE consultation_rooms (
    id BIGINT NOT NULL AUTO_INCREMENT,
    office_id BIGINT NOT NULL,
    room_number VARCHAR(50) NOT NULL,
    room_type VARCHAR(50),
    capacity INT NOT NULL,
    equipment VARCHAR(255),
    is_active BOOLEAN NOT NULL,
    is_available BOOLEAN NOT NULL,
    notes TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_consultation_rooms_office FOREIGN KEY (office_id) REFERENCES offices (id)
);

CREATE TABLE appointments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    consultation_room_id BIGINT,
    appointment_date TIMESTAMP NOT NULL,
    appointment_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    reason TEXT,
    notes TEXT,
    consultation_fee DOUBLE NOT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    CONSTRAINT fk_appointments_room FOREIGN KEY (consultation_room_id) REFERENCES consultation_rooms (id)
);

CREATE TABLE time_slots (
    id BIGINT NOT NULL AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    is_available BOOLEAN NOT NULL,
    day_of_week VARCHAR(50),
    is_recurring BOOLEAN NOT NULL,
    created_at TIMESTAMP NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_time_slots_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id)
);

CREATE TABLE availability (
    id BIGINT NOT NULL AUTO_INCREMENT,
    doctor_id BIGINT,
    start_time DATETIME(6),
    end_time DATETIME(6),
    is_booked BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_availability_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id)
);

CREATE TABLE medical_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_id BIGINT,
    diagnosis TEXT NOT NULL,
    symptoms TEXT,
    treatment TEXT,
    notes TEXT,
    record_type VARCHAR(50),
    is_confidential BOOLEAN NOT NULL,
    record_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_medical_records_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_medical_records_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    CONSTRAINT fk_medical_records_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id)
);

CREATE TABLE prescriptions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    medical_record_id BIGINT,
    prescription_number VARCHAR(50) NOT NULL,
    medications TEXT NOT NULL,
    instructions TEXT,
    status VARCHAR(50),
    issue_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    is_digitally_filled BOOLEAN NOT NULL,
    created_at TIMESTAMP NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_prescriptions_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_prescriptions_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    CONSTRAINT fk_prescriptions_record FOREIGN KEY (medical_record_id) REFERENCES medical_records (id)
);

CREATE TABLE medical_documents (
    id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    file_name VARCHAR(100) NOT NULL,
    file_path VARCHAR(255),
    document_type VARCHAR(50),
    file_type VARCHAR(50),
    file_size BIGINT NOT NULL,
    description TEXT,
    uploaded_by_id BIGINT,
    is_public BOOLEAN NOT NULL,
    upload_date TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_medical_documents_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_medical_documents_uploader FOREIGN KEY (uploaded_by_id) REFERENCES users (id)
);

CREATE TABLE invoices (
    id BIGINT NOT NULL AUTO_INCREMENT,
    invoice_number VARCHAR(50) NOT NULL,
    patient_id BIGINT NOT NULL,
    appointment_id BIGINT,
    invoice_date DATE NOT NULL,
    due_date DATE NOT NULL,
    subtotal DOUBLE NOT NULL,
    tax_amount DOUBLE NOT NULL,
    discount_amount DOUBLE NOT NULL,
    total_amount DOUBLE NOT NULL,
    status VARCHAR(50),
    payment_method VARCHAR(50),
    notes TEXT,
    is_paid BOOLEAN NOT NULL,
    paid_at TIMESTAMP NULL,
    created_at TIMESTAMP NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_invoices_number UNIQUE (invoice_number),
    CONSTRAINT fk_invoices_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_invoices_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id)
);

CREATE TABLE invoice_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    invoice_id BIGINT NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit_price DOUBLE NOT NULL,
    tax_rate DOUBLE NOT NULL,
    total_price DOUBLE NOT NULL,
    category VARCHAR(50),
    notes TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_invoice_items_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id)
);

CREATE TABLE payment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    invoice_id BIGINT,
    amount_paid DECIMAL(38, 2),
    payment_method VARCHAR(255),
    payment_date DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id)
);

CREATE TABLE performed_act (
    id BIGINT NOT NULL AUTO_INCREMENT,
    appointment_id BIGINT,
    act_code VARCHAR(255),
    description VARCHAR(255),
    amount DECIMAL(38, 2),
    PRIMARY KEY (id),
    CONSTRAINT fk_performed_act_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id)
);

CREATE TABLE reviews (
    id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_id BIGINT,
    rating INT NOT NULL,
    comment TEXT NOT NULL,
    is_anonymous BOOLEAN NOT NULL,
    is_approved BOOLEAN NOT NULL,
    created_at TIMESTAMP NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_reviews_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_reviews_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    CONSTRAINT fk_reviews_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id)
);

CREATE TABLE audit_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT NOT NULL,
    description TEXT,
    status VARCHAR(50),
    ip_address VARCHAR(45),
    user_agent TEXT,
    is_security_event BOOLEAN NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL,
    notification_channel VARCHAR(50),
    is_sent BOOLEAN NOT NULL,
    sent_at TIMESTAMP NULL,
    read_at TIMESTAMP NULL,
    created_at TIMESTAMP NULL,
    related_entity_type VARCHAR(100),
    related_entity_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE dependent (
    id BIGINT NOT NULL AUTO_INCREMENT,
    parent_patient_id BIGINT,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    birth_date DATE,
    relationship VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_dependent_patient FOREIGN KEY (parent_patient_id) REFERENCES patients (id)
);

CREATE TABLE room (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    type VARCHAR(255),
    available BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE equipment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    serial_number VARCHAR(255),
    assigned_room_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_equipment_room FOREIGN KEY (assigned_room_id) REFERENCES room (id)
);

CREATE TABLE tariff (
    id BIGINT NOT NULL AUTO_INCREMENT,
    doctor_id BIGINT,
    act_type VARCHAR(255),
    sector VARCHAR(255),
    price DECIMAL(38, 2),
    PRIMARY KEY (id),
    CONSTRAINT fk_tariff_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id)
);

CREATE TABLE issue_report (
    id BIGINT NOT NULL AUTO_INCREMENT,
    reporter_id BIGINT,
    title VARCHAR(255),
    description VARCHAR(255),
    status VARCHAR(255),
    created_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_issue_report_user FOREIGN KEY (reporter_id) REFERENCES users (id)
);

CREATE TABLE medical_report_template (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    content TEXT,
    specialty VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE clinic_settings (
    id BIGINT NOT NULL,
    name VARCHAR(255),
    address VARCHAR(255),
    phone VARCHAR(255),
    email VARCHAR(255),
    opening_hours VARCHAR(255),
    specialties VARCHAR(255),
    PRIMARY KEY (id)
);
