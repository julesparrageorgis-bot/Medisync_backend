package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.medisync.medisync_backend.entity.enums.Gender;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients", indexes = {
    @Index(name = "idx_ssn", columnList = "social_security_number", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"appointments", "medicalRecords", "prescriptions", "documents", "invoices", "reviews"})
@DiscriminatorValue("PATIENT")
public class Patient extends User {

    @Column(unique = true, length = 50)
    private String socialSecurityNumber;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 10)
    private String bloodType;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<String> allergies;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 10)
    private String zipCode;

    @Column(length = 100)
    private String emergencyContact;

    @Column(length = 20)
    private String emergencyPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id")
    private Office office;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Prescription> prescriptions = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MedicalDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Invoice> invoices = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
}
