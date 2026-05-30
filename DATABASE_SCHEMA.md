# 🗄️ MediSync Database Schema

## Entity Relationship Diagram (ERD)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         USERS & AUTHENTICATION                          │
└─────────────────────────────────────────────────────────────────────────┘

    ┌──────────────────────────┐
    │         USERS            │
    ├──────────────────────────┤
    │ PK  id (BIGINT)          │
    │     email (VARCHAR)      │◄─────┐
    │     password (VARCHAR)   │      │
    │     first_name (VARCHAR) │      │
    │     last_name (VARCHAR)  │      │
    │     phone (VARCHAR)      │      │
    │     user_role (ENUM)     │      │
    │     is_active (BOOLEAN)  │      │
    │     last_login (DATETIME)│      │
    │     created_at (DATETIME)│      │
    │     updated_at (DATETIME)│      │
    └──────────────────────────┘      │
           │         │                 │
           │         ├─────────────────┼─────────────────┬──────────────┐
           │         │                 │                 │              │
           │         │                 │                 │              │
    ┌──────────┬─────────┴──┐  ┌──────────┬──┐  ┌──────────┬──┐  ┌──────────┬──┐
    │ PATIENTS │            │  │ DOCTORS  │  │  │SECRETARIES   │  │ ADMINS   │  │
    ├──────────┼────────────┤  ├──────────┼──┤  ├──────────────┤  ├──────────┤──┤
    │ FK user_id           │  │ FK user_id   │  │ FK user_id   │  │ FK user_id│
    │    ssn (VARCHAR)     │  │    license   │  │              │  │ 2fa_enabled
    │    dob (DATE)        │  │    specialties   │              │  │ 2fa_secret
    │    gender (ENUM)     │  │    languages │  │              │  │          │
    │    blood_type        │  │    rate (DEC)│  │              │  │          │
    │    allergies (JSON)  │  │ FK office_id │  │ FK office_id │  │          │
    │    address (VARCHAR) │  │              │  │              │  │          │
    │    dependents (LIST) │  │              │  │              │  │          │
    └──────────┴────────────┘  └──────────┴──┘  └──────────────┘  └──────────┴──┘
           │                          │                │                │
           │                          │                │                │
           └──────────────┬───────────┴────────────────┴────────────────┘
                          │
          ┌───────────────┴───────────────────────┐
          │                                       │
          │        ┌────────────────────────┐     │
          │        │ AUDIT_LOGS             │     │
          │        ├────────────────────────┤     │
          │        │ PK  id (BIGINT)        │     │
          │        │ FK  user_id            │◄────┤
          │        │     action (VARCHAR)   │     │
          │        │     target_entity      │     │
          │        │     target_id          │     │
          │        │     details (JSON)     │     │
          │        │     timestamp          │     │
          │        │     ip_address         │     │
          │        └────────────────────────┘     │
          │                                       │
          │        ┌────────────────────────┐     │
          │        │ NOTIFICATIONS          │     │
          │        ├────────────────────────┤     │
          │        │ PK  id (BIGINT)        │     │
          │        │ FK  user_id            │◄────┤
          │        │     type (ENUM)        │     │
          │        │     message (TEXT)     │     │
          │        │     is_read (BOOLEAN)  │     │
          │        │     sent_at (DATETIME) │     │
          │        └────────────────────────┘     │
          │                                       │
          └───────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                       CLINIC MANAGEMENT                                 │
└─────────────────────────────────────────────────────────────────────────┘

        ┌──────────────────────────┐
        │      OFFICES             │
        ├──────────────────────────┤
        │ PK  id (BIGINT)          │
        │     name (VARCHAR)       │
        │     address (VARCHAR)    │
        │     city (VARCHAR)       │
        │     zip_code (VARCHAR)   │
        │     phone (VARCHAR)      │
        │     email (VARCHAR)      │
        │     opening_hours (JSON) │
        │     specialties (JSON)   │
        └──────────────────────────┘
           │                  │
           │                  └────────┬──────────────┐
           │                           │              │
    ┌──────────────────────┐  ┌────────────────────┐  │
    │ CONSULTATION_ROOMS   │  │   TIME_SLOTS       │  │
    ├──────────────────────┤  ├────────────────────┤  │
    │ PK  id (BIGINT)      │  │ PK  id (BIGINT)    │  │
    │ FK  office_id        │  │ FK  doctor_id      │  │
    │     name (VARCHAR)   │  │     start_time     │  │
    │     equipment (JSON) │  │     end_time       │  │
    │     is_available     │  │     duration_min   │  │
    └──────────────────────┘  │     day_of_week    │  │
           ▲                   │     is_available   │  │
           │                   │ FK appointment_id  │  │
           │                   └────────────────────┘  │
           │                          ▲               │
           │                          │               │
           │                          │               │
           │        ┌─────────────────┴───────────────┘
           │        │
           │   ┌────────────────────────────────┐
           │   │    APPOINTMENTS                │
           │   ├────────────────────────────────┤
           │   │ PK  id (BIGINT)                │
           │   │ FK  patient_id                 │
           │   │ FK  doctor_id                  │
           │   │ FK  office_id                  │
           │   │ FK  room_id                    │◄─────┐
           │   │     appointment_datetime       │      │
           │   │     duration_minutes           │      │
           │   │     consultation_type (ENUM)  │      │
           │   │     status (ENUM)              │      │
           │   │     reason (VARCHAR)           │      │
           │   │     notes (TEXT)               │      │
           │   │     reminder_24h_sent (BOOL)  │      │
           │   │     reminder_1h_sent (BOOL)   │      │
           │   │     created_at (DATETIME)      │      │
           │   │     updated_at (DATETIME)      │      │
           │   └────────────────────────────────┘      │
           │        │              │                   │
           │        │              └───────────────────┘
           │        │
           │        │ ┌──────────────────────┐
           │        │ │    REVIEWS           │
           │        │ ├──────────────────────┤
           │        │ │ PK  id (BIGINT)      │
           │        │ │ FK  appointment_id   │◄───────────┐
           │        │ │ FK  patient_id       │            │
           │        │ │ FK  doctor_id        │            │
           │        │ │     rating (INT)     │            │
           │        │ │     comment (TEXT)   │            │
           │        │ │     created_at       │            │
           │        │ └──────────────────────┘            │
           │        │                                     │
           └────────┼─────────────────────────────────────┘
                    │
           ┌────────┴──────────────────────┐
           │                               │
    ┌──────────────────────┐   ┌──────────────────────┐
    │  MEDICAL_RECORDS     │   │  INVOICES            │
    ├──────────────────────┤   ├──────────────────────┤
    │ PK  id (BIGINT)      │   │ PK  id (BIGINT)      │
    │ FK  patient_id       │   │ FK  appointment_id   │
    │ FK  doctor_id        │   │ FK  patient_id       │
    │ FK  appointment_id   │   │ FK  secretary_id     │
    │     consultation_date│   │     invoice_date     │
    │     chief_complaint  │   │     total_amount (DEC)
    │     history (TEXT)   │   │     payment_status   │
    │     diagnosis (TEXT) │   │     payment_date     │
    │     examination      │   │     pdf_path (VARCHAR)
    │     treatment_plan   │   │     created_at       │
    │     notes (TEXT)     │   │     updated_at       │
    │     created_at       │   │                      │
    │     updated_at       │   └──────────────────────┘
    └──────────────────────┘          │
           │                          │
           │                   ┌──────┴──────────────┐
           │                   │                    │
           │        ┌──────────────────────┐   ┌────────────────────┐
           │        │ INVOICE_ITEMS        │   │ SERVICES/ACTS      │
           │        ├──────────────────────┤   ├────────────────────┤
           │        │ PK  id (BIGINT)      │   │ PK  id (BIGINT)    │
           │        │ FK  invoice_id       │   │     code (VARCHAR) │
           │        │     description      │   │     name (VARCHAR) │
           │        │     quantity (INT)   │   │     price (DEC)    │
           │        │     unit_price (DEC) │   │     sector (ENUM)  │
           │        │     total (DEC)      │   │     specialty       │
           │        └──────────────────────┘   └────────────────────┘
           │                                          ▲
           └──────────────────────────────────────────┘

    ┌──────────────────────────────────┐
    │   PRESCRIPTIONS                  │
    ├──────────────────────────────────┤
    │ PK  id (BIGINT)                  │
    │ FK  medical_record_id            │
    │ FK  patient_id                   │
    │ FK  doctor_id                    │
    │     prescription_date (DATE)     │
    │     expiration_date (DATE)       │
    │     instructions (TEXT)          │
    │     status (ENUM)                │
    │     pdf_path (VARCHAR)           │
    │     created_at (DATETIME)        │
    └──────────────────────────────────┘
              │
              │ (1 → N)
              │
    ┌──────────────────────────────────┐
    │   MEDICATIONS                    │
    ├──────────────────────────────────┤
    │ PK  id (BIGINT)                  │
    │ FK  prescription_id              │
    │     name (VARCHAR)               │
    │     dosage (VARCHAR)             │
    │     frequency (VARCHAR)          │
    │     duration (INT - jours)       │
    │     instructions (TEXT)          │
    └──────────────────────────────────┘

    ┌──────────────────────────────────┐
    │   MEDICAL_DOCUMENTS              │
    ├──────────────────────────────────┤
    │ PK  id (BIGINT)                  │
    │ FK  patient_id                   │
    │ FK  medical_record_id (nullable) │
    │     file_name (VARCHAR)          │
    │     file_type (ENUM)             │
    │     file_size (BIGINT)           │
    │     file_path (VARCHAR)          │
    │     upload_date (DATETIME)       │
    │     document_type (VARCHAR)      │
    └──────────────────────────────────┘
```

---

## 📊 Table Summary

| Entity | Description | Relationships |
|--------|-------------|---------------|
| **USERS** | Base table for all users | 1→N PATIENTS, DOCTORS, SECRETARIES, ADMINS |
| **PATIENTS** | Patient details | 1→N APPOINTMENTS, RECORDS, DOCUMENTS |
| **DOCTORS** | Doctor details | 1→N APPOINTMENTS, RECORDS, PRESCRIPTIONS |
| **OFFICES** | Clinic information | 1→N DOCTORS, ROOMS, APPOINTMENTS |
| **APPOINTMENTS** | Medical appointments | N→1 PATIENT/DOCTOR/OFFICE/ROOM |
| **MEDICAL_RECORDS** | Patient medical history | 1→N DOCUMENTS, PRESCRIPTIONS |
| **PRESCRIPTIONS** | Electronic prescriptions | 1→N MEDICATIONS |
| **INVOICES** | Billing | 1→N INVOICE_ITEMS, 1→1 APPOINTMENT |
| **AUDIT_LOGS** | Security logs | N→1 USER |
| **NOTIFICATIONS** | User notifications | N→1 USER |

---

## 🔑 Key Constraints

```sql
-- USERS Table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    user_role ENUM('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_user_role (user_role)
);

-- PATIENTS Table
CREATE TABLE patients (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    ssn VARCHAR(50) UNIQUE,
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    blood_type VARCHAR(5),
    allergies JSON,
    address VARCHAR(255),
    city VARCHAR(100),
    zip_code VARCHAR(10),
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_ssn (ssn)
);

-- DOCTORS Table
CREATE TABLE doctors (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    specialties JSON,
    languages JSON,
    consultation_rate DECIMAL(10,2),
    office_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (office_id) REFERENCES offices(id),
    INDEX idx_license (license_number)
);

-- APPOINTMENTS Table
CREATE TABLE appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    office_id BIGINT NOT NULL,
    room_id BIGINT,
    appointment_datetime DATETIME NOT NULL,
    duration_minutes INT DEFAULT 30,
    consultation_type ENUM('GENERAL', 'FOLLOW_UP', 'EMERGENCY') DEFAULT 'GENERAL',
    status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    reason VARCHAR(255),
    notes TEXT,
    reminder_24h_sent BOOLEAN DEFAULT FALSE,
    reminder_1h_sent BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (office_id) REFERENCES offices(id),
    FOREIGN KEY (room_id) REFERENCES consultation_rooms(id),
    INDEX idx_patient_date (patient_id, appointment_datetime),
    INDEX idx_doctor_date (doctor_id, appointment_datetime),
    INDEX idx_status (status)
);

-- PRESCRIPTIONS Table
CREATE TABLE prescriptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    medical_record_id BIGINT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    prescription_date DATE NOT NULL,
    expiration_date DATE,
    instructions TEXT,
    status ENUM('ACTIVE', 'EXPIRED', 'CANCELLED') DEFAULT 'ACTIVE',
    pdf_path VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id)
);

-- INVOICES Table
CREATE TABLE invoices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appointment_id BIGINT,
    patient_id BIGINT NOT NULL,
    secretary_id BIGINT,
    invoice_date DATE NOT NULL,
    total_amount DECIMAL(10,2),
    payment_status ENUM('UNPAID', 'PAID', 'OVERDUE') DEFAULT 'UNPAID',
    payment_date DATETIME,
    pdf_path VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (secretary_id) REFERENCES secretaries(id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    INDEX idx_date (invoice_date),
    INDEX idx_payment_status (payment_status)
);

-- AUDIT_LOGS Table
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    action VARCHAR(100),
    target_entity VARCHAR(50),
    target_id BIGINT,
    details JSON,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_timestamp (timestamp),
    INDEX idx_user (user_id)
);
```

