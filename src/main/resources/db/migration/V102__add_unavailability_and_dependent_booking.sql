CREATE TABLE doctor_unavailability (
    id BIGINT NOT NULL AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    start_time DATETIME(6),
    end_time DATETIME(6),
    reason VARCHAR(255),
    leave_day BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_doctor_unavailability_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id)
);

ALTER TABLE appointments
    ADD COLUMN dependent_id BIGINT,
    ADD CONSTRAINT fk_appointments_dependent FOREIGN KEY (dependent_id) REFERENCES dependent (id);
