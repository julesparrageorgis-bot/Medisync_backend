# ✅ MediSync Backend - Checklist de Développement

## 📋 Phase 1: Configuration du Projet

### Setup Maven & Base de Données
- [ ] Mettre à jour `pom.xml` avec toutes les dépendances
  - [ ] Spring Boot 3.1.5
  - [ ] Spring Security
  - [ ] JPA/Hibernate
  - [ ] MySQL Driver
  - [ ] JWT (jjwt)
  - [ ] Lombok
  - [ ] Validation
  - [ ] Mail
  - [ ] PDF (iText)
  - [ ] Excel (Apache POI)
  - [ ] Swagger/OpenAPI

- [ ] Créer `application.properties`
  - [ ] Database URL correcte
  - [ ] JWT secret (minimum 256 bits)
  - [ ] Email SMTP configuration
  - [ ] File upload directory

- [ ] Créer `application-dev.properties` et `application-prod.properties`

- [ ] Créer base de données MySQL
  ```sql
  CREATE DATABASE medisync_db;
  ```

- [ ] Configurer structure dossiers
  ```
  src/main/java/ma/medisync/medisync_backend/
  ├── config/
  ├── controller/
  ├── service/
  ├── repository/
  ├── entity/
  ├── dto/
  ├── exception/
  ├── util/
  └── listener/
  ```

---

## 🔐 Phase 2: Authentification & Sécurité

### Implémentation JWT
- [ ] Créer `JwtTokenProvider.java`
  - [ ] Générer token JWT
  - [ ] Valider token
  - [ ] Extraire username du token
  - [ ] Gérer expiration

- [ ] Créer `JwtAuthenticationFilter.java`
  - [ ] Extraire token du header
  - [ ] Valider token
  - [ ] Charger l'utilisateur

### Configuration Spring Security
- [ ] Créer `SecurityConfig.java`
  - [ ] BCryptPasswordEncoder
  - [ ] AuthenticationManager
  - [ ] SecurityFilterChain
  - [ ] CORS configuration
  - [ ] Public endpoints (/auth/*)
  - [ ] Protected endpoints (/* - require auth)

- [ ] Créer `CorsConfig.java`
  - [ ] Allowed origins
  - [ ] Allowed methods
  - [ ] Allowed headers

### Entité User
- [ ] Créer `User.java` entity
  - [ ] Extends UserDetails
  - [ ] Fields: id, email, password, firstName, lastName, phone, userRole, isActive
  - [ ] Enums: UserRole (PATIENT, DOCTOR, SECRETARY, ADMIN)
  - [ ] Implements getAuthorities(), getUsername(), isEnabled(), etc.
  - [ ] @PrePersist pour createdAt
  - [ ] @PreUpdate pour updatedAt

- [ ] Créer `UserRepository.java`
  - [ ] findByEmail(String email)
  - [ ] existsByEmail(String email)

- [ ] Créer `UserService.java`
  - [ ] Implements UserDetailsService
  - [ ] loadUserByUsername()
  - [ ] findByEmail()
  - [ ] save()
  - [ ] existsByEmail()

### Auth Controller
- [ ] Créer `AuthController.java`
  - [ ] POST /auth/login
    - [ ] Authentifier user
    - [ ] Générer JWT
    - [ ] Retourner token + user info
  - [ ] POST /auth/register
    - [ ] Valider email unique
    - [ ] Valider password strength
    - [ ] Hasher password avec BCrypt
    - [ ] Sauvegarder user
  - [ ] POST /auth/refresh-token (optionnel)

### DTOs
- [ ] Créer `LoginRequest.java` (email, password)
- [ ] Créer `LoginResponse.java` (token, userId, email, role)
- [ ] Créer `RegisterRequest.java` (email, password, firstName, lastName)
- [ ] Créer `UserDTO.java` (id, email, firstName, lastName, role)

### Tests Auth
- [ ] Tester register via Postman
- [ ] Tester login via Postman
- [ ] Vérifier token JWT généré
- [ ] Tester accès endpoint protégé sans token (401)
- [ ] Tester accès endpoint protégé avec token (200)

---

## 👥 Phase 3: Entités Utilisateurs

### Patient Entity
- [ ] Créer `Patient.java` (extends User)
  - [ ] Fields: socialSecurityNumber, dateOfBirth, gender, bloodType, allergies (JSON)
  - [ ] Fields: address, city, zipCode, emergencyContact, emergencyPhone
  - [ ] Relationships: 1→N appointments, records, prescriptions, documents
  - [ ] Enum Gender: MALE, FEMALE, OTHER

### Doctor Entity
- [ ] Créer `Doctor.java` (extends User)
  - [ ] Fields: licenseNumber, specialties (JSON), languages (JSON)
  - [ ] Fields: consultationRate (BigDecimal)
  - [ ] Relationships: 1→N appointments, records, prescriptions
  - [ ] Relationship: N→1 office

### Secretary Entity
- [ ] Créer `Secretary.java` (extends User)
  - [ ] Fields: responsibilities (JSON)
  - [ ] Relationships: 1→N managedAppointments, invoices
  - [ ] Relationship: N→1 office

### Admin Entity
- [ ] Créer `Admin.java` (extends User)
  - [ ] Fields: twoFactorEnabled, twoFactorSecret
  - [ ] Relationships: 1→N offices

### 2FA Configuration
- [ ] Ajouter dépendance TOTP: `dev.samstevens.totp:totp:1.7.1`
- [ ] Créer `TwoFactorService.java`
  - [ ] generateSecret()
  - [ ] verifyOTP(secret, otp)
- [ ] Endpoint POST /auth/2fa/setup (pour admins)
- [ ] Endpoint POST /auth/2fa/verify

### Repositories
- [ ] Créer `PatientRepository.java`
- [ ] Créer `DoctorRepository.java`
- [ ] Créer `SecretaryRepository.java`
- [ ] Créer `AdminRepository.java`

### Services
- [ ] Créer `PatientService.java`
- [ ] Créer `DoctorService.java`
- [ ] Créer `SecretaryService.java`
- [ ] Créer `AdminService.java`

---

## 🏥 Phase 4: Entités Clinique & Planning

### Office Entity
- [ ] Créer `Office.java`
  - [ ] Fields: name, address, city, zipCode, phone, email
  - [ ] Fields: openingHours (JSON), specialties (JSON)
  - [ ] Relationships: 1→N doctors, secretaries, consultationRooms, appointments

### ConsultationRoom Entity
- [ ] Créer `ConsultationRoom.java`
  - [ ] Fields: name, equipment (JSON), isAvailable
  - [ ] Relationships: N→1 office, 1→N appointments

### TimeSlot Entity
- [ ] Créer `TimeSlot.java`
  - [ ] Fields: startTime, endTime, durationMinutes, dayOfWeek
  - [ ] Fields: isAvailable
  - [ ] Relationships: N→1 doctor, 0→1 appointment
  - [ ] Enum DayOfWeek: MONDAY, TUESDAY, ..., SUNDAY

### Appointment Entity
- [ ] Créer `Appointment.java`
  - [ ] Fields: appointmentDateTime, durationMinutes, status, reason, notes
  - [ ] Fields: consultationType, reminder24hSent, reminder1hSent
  - [ ] Relationships: N→1 patient, doctor, office, room
  - [ ] Enums: 
    - [ ] AppointmentStatus: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    - [ ] ConsultationType: GENERAL, FOLLOW_UP, EMERGENCY
  - [ ] @PrePersist createdAt, @PreUpdate updatedAt

### Repositories & Services
- [ ] Créer `OfficeRepository.java`
- [ ] Créer `ConsultationRoomRepository.java`
- [ ] Créer `TimeSlotRepository.java`
- [ ] Créer `AppointmentRepository.java`
  - [ ] findByPatientId(Long patientId)
  - [ ] findByDoctorId(Long doctorId)
  - [ ] findByDoctorAndAppointmentDateTime(Doctor, LocalDateTime)
  - [ ] findByStatusAndAppointmentDateTimeAfter()

- [ ] Créer `OfficeService.java`
- [ ] Créer `ConsultationRoomService.java`
- [ ] Créer `TimeSlotService.java`
- [ ] Créer `AppointmentService.java`
  - [ ] createAppointment()
  - [ ] getAvailableSlots()
  - [ ] modifyAppointment()
  - [ ] cancelAppointment()
  - [ ] sendReminders() (scheduled task)

---

## 📋 Phase 5: Dossiers Médicaux

### MedicalRecord Entity
- [ ] Créer `MedicalRecord.java`
  - [ ] Fields: chiefComplaint, history, diagnosis, examinationFindings, treatmentPlan, notes
  - [ ] Relationships: N→1 patient, doctor, appointment
  - [ ] Relationship: 1→N documents, prescriptions
  - [ ] @PrePersist/@PreUpdate timestamps

### Prescription Entity
- [ ] Créer `Prescription.java`
  - [ ] Fields: prescriptionDate, expirationDate, instructions, status, pdfPath
  - [ ] Relationships: N→1 medicalRecord, patient, doctor
  - [ ] Relationship: 1→N medications
  - [ ] Enum Status: ACTIVE, EXPIRED, CANCELLED

### Medication Entity
- [ ] Créer `Medication.java`
  - [ ] Fields: name, dosage, frequency, duration, instructions
  - [ ] Relationships: N→1 prescription

### MedicalDocument Entity
- [ ] Créer `MedicalDocument.java`
  - [ ] Fields: fileName, fileType, fileSize, filePath, uploadDate, documentType
  - [ ] Relationships: N→1 patient, 0→1 medicalRecord
  - [ ] Enum FileType: PDF, JPG, PNG, DICOM
  - [ ] Validation: fileSize <= 20MB

### Repositories & Services
- [ ] Créer `MedicalRecordRepository.java`
- [ ] Créer `PrescriptionRepository.java`
- [ ] Créer `MedicalDocumentRepository.java`

- [ ] Créer `MedicalRecordService.java`
  - [ ] createRecord()
  - [ ] getPatientRecords()
  - [ ] updateRecord()

- [ ] Créer `PrescriptionService.java`
  - [ ] createPrescription()
  - [ ] generatePrescriptionPDF()
  - [ ] expirePrescriptions() (scheduled)

- [ ] Créer `FileStorageService.java`
  - [ ] uploadFile()
  - [ ] downloadFile()
  - [ ] deleteFile()
  - [ ] validateFile()

---

## 💰 Phase 6: Facturation

### Invoice Entity
- [ ] Créer `Invoice.java`
  - [ ] Fields: invoiceDate, totalAmount, paymentStatus, paymentDate, pdfPath
  - [ ] Relationships: 0→1 appointment, N→1 patient, secretary
  - [ ] Relationship: 1→N invoiceItems
  - [ ] Enum PaymentStatus: UNPAID, PAID, OVERDUE

### InvoiceItem Entity
- [ ] Créer `InvoiceItem.java`
  - [ ] Fields: description, quantity, unitPrice, total
  - [ ] Relationships: N→1 invoice

### Repositories & Services
- [ ] Créer `InvoiceRepository.java`
- [ ] Créer `InvoiceItemRepository.java`

- [ ] Créer `InvoiceService.java`
  - [ ] createInvoice()
  - [ ] generateInvoicePDF()
  - [ ] sendInvoiceEmail()
  - [ ] markAsPaid()
  - [ ] getUnpaidInvoices()

- [ ] Créer `PdfGeneratorService.java`
  - [ ] generatePrescriptionPDF()
  - [ ] generateInvoicePDF()
  - [ ] generateReceiptPDF()

---

## 📧 Phase 7: Notifications

### Notification Entity
- [ ] Créer `Notification.java`
  - [ ] Fields: type, message, isRead, sentAt
  - [ ] Relationships: N→1 user
  - [ ] Enum Type: APPOINTMENT_REMINDER, APPOINTMENT_CHANGE, PRESCRIPTION_READY, INVOICE_SENT

### Services
- [ ] Créer `NotificationService.java`
  - [ ] createNotification()
  - [ ] markAsRead()
  - [ ] getUserNotifications()

- [ ] Créer `EmailService.java`
  - [ ] sendAppointmentConfirmation()
  - [ ] sendAppointmentReminder() (24h et 1h)
  - [ ] sendAppointmentChangedNotification()
  - [ ] sendInvoiceEmail()
  - [ ] sendPrescriptionReadyEmail()

### Scheduled Tasks
- [ ] Créer `ScheduledTasks.java`
  - [ ] @Scheduled sendAppointmentReminders24h()
  - [ ] @Scheduled sendAppointmentReminders1h()
  - [ ] @Scheduled expireOldPrescriptions()
  - [ ] @Scheduled markOverdueInvoices()

---

## 📊 Phase 8: Audit & Admin

### AuditLog Entity
- [ ] Créer `AuditLog.java`
  - [ ] Fields: action, targetEntity, targetId, details (JSON), timestamp, ipAddress
  - [ ] Relationships: N→1 user

### Review Entity
- [ ] Créer `Review.java`
  - [ ] Fields: rating (1-5), comment, createdAt
  - [ ] Relationships: N→1 appointment, patient, doctor

### Repositories
- [ ] Créer `AuditLogRepository.java`
- [ ] Créer `ReviewRepository.java`

### Services
- [ ] Créer `AuditService.java`
  - [ ] logAction()
  - [ ] getAuditLogs()
  - [ ] filterByUser/Date/Action()

### Admin Controller
- [ ] Créer `AdminController.java`
  - [ ] GET /admin/users - Lister utilisateurs
  - [ ] POST /admin/users - Créer utilisateur
  - [ ] PUT /admin/users/{id} - Modifier utilisateur
  - [ ] DELETE /admin/users/{id} - Supprimer utilisateur
  - [ ] GET /admin/offices - Lister cliniques
  - [ ] POST /admin/offices - Créer clinique
  - [ ] GET /admin/audit-logs - Logs d'audit
  - [ ] GET /admin/statistics - Statistiques
  - [ ] GET /admin/dashboard - Tableau de bord

### Statistics Service
- [ ] Créer `StatisticsService.java`
  - [ ] getAppointmentStats()
  - [ ] getRevenueStats()
  - [ ] getDoctorPerformance()
  - [ ] getPatientDemographics()
  - [ ] getRoomOccupancy()

---

## 🔗 Phase 9: Controllers REST

### Patient Controller
- [ ] Créer `PatientController.java`
  - [ ] GET /patients/me
  - [ ] PUT /patients/me
  - [ ] GET /patients/{id}
  - [ ] GET /patients/me/appointments
  - [ ] POST /appointments
  - [ ] PUT /appointments/{id}
  - [ ] DELETE /appointments/{id}
  - [ ] GET /doctors (search by specialty)
  - [ ] GET /doctors/{id}
  - [ ] GET /doctors/{id}/availability
  - [ ] GET /patients/me/records
  - [ ] POST /documents (upload)
  - [ ] GET /patients/me/prescriptions
  - [ ] GET /prescriptions/{id}/pdf
  - [ ] POST /reviews
  - [ ] POST /complaints

### Doctor Controller
- [ ] Créer `DoctorController.java`
  - [ ] GET /doctors/me
  - [ ] PUT /doctors/me
  - [ ] GET /doctors/me/appointments
  - [ ] GET /doctors/me/schedule
  - [ ] PUT /doctors/me/availability
  - [ ] GET /patients/{id}
  - [ ] GET /patients/{id}/records
  - [ ] POST /records
  - [ ] PUT /records/{id}
  - [ ] POST /prescriptions
  - [ ] GET /medications (search)
  - [ ] POST /records/{id}/documents

### Secretary Controller
- [ ] Créer `SecretaryController.java`
  - [ ] POST /patients (create patient)
  - [ ] GET /appointments
  - [ ] POST /appointments
  - [ ] PUT /appointments/{id}
  - [ ] DELETE /appointments/{id}
  - [ ] GET /invoices
  - [ ] POST /invoices
  - [ ] GET /invoices/{id}/pdf

### Appointment Controller
- [ ] Créer `AppointmentController.java`
  - [ ] GET /appointments
  - [ ] POST /appointments
  - [ ] PUT /appointments/{id}
  - [ ] DELETE /appointments/{id}
  - [ ] GET /appointments/{id}

### Medical Record Controller
- [ ] Créer `MedicalRecordController.java`
  - [ ] GET /records
  - [ ] GET /records/{id}
  - [ ] POST /records
  - [ ] PUT /records/{id}

### Prescription Controller
- [ ] Créer `PrescriptionController.java`
  - [ ] GET /prescriptions
  - [ ] GET /prescriptions/{id}
  - [ ] POST /prescriptions
  - [ ] GET /prescriptions/{id}/pdf

### Invoice Controller
- [ ] Créer `InvoiceController.java`
  - [ ] GET /invoices
  - [ ] GET /invoices/{id}
  - [ ] POST /invoices
  - [ ] GET /invoices/{id}/pdf

---

## 🧪 Phase 10: Tests & Validation

### Tests Unitaires
- [ ] Tests UserService
- [ ] Tests PatientService
- [ ] Tests AppointmentService
- [ ] Tests InvoiceService
- [ ] Tests FileStorageService
- [ ] Tests PdfGeneratorService

### Tests Intégration
- [ ] Tests AuthController (login/register)
- [ ] Tests PatientController
- [ ] Tests DoctorController
- [ ] Tests AppointmentController
- [ ] Tests InvoiceController

### Tests Sécurité
- [ ] Tester endpoints sans authentification (401)
- [ ] Tester endpoints avec mauvais rôle (403)
- [ ] Tester SQL injection prevention
- [ ] Tester file upload validation (max 20MB)
- [ ] Tester password strength validation

### Tests Avec Postman
- [ ] Register nouveau user
- [ ] Login et obtenir JWT
- [ ] Accéder endpoint protégé avec JWT
- [ ] Accéder endpoint protégé sans JWT (401)
- [ ] Créer appointment
- [ ] Consulter dossier médical
- [ ] Télécharger prescription PDF
- [ ] Créer facture et télécharger PDF

---

## 📚 Phase 11: Documentation

### Documentation Technique
- [ ] Documentation architecture
- [ ] Schéma base de données (ERD)
- [ ] Documentation API (Swagger/OpenAPI)
- [ ] Guide installation & déploiement
- [ ] Guide configuration (dev/prod)

### Documentation Code
- [ ] Javadoc pour toutes les classes
- [ ] Commentaires pour logique complexe
- [ ] README.md avec description projet

### Vidéo Démo
- [ ] Démo workflow Patient (login → rdv → consultation → prescriptions)
- [ ] Démo workflow Doctor (planning → consultations → ordonnances)
- [ ] Démo workflow Secretary (gestion rdv → facturation)
- [ ] Démo workflow Admin (gestion utilisateurs → statistiques)

---

## 🚀 Phase 12: Déploiement

### Build Production
- [ ] Clean build: `mvn clean package`
- [ ] Vérifier pas d'erreurs
- [ ] Générer JAR: `target/medisync-backend-1.0.0.jar`

### Configuration Production
- [ ] Créer `application-prod.properties`
  - [ ] Database URL (prod)
  - [ ] JWT secret (nouveau, sécurisé)
  - [ ] Email configuration
  - [ ] Désactiver debug logging
  - [ ] CORS production origins

### Déploiement
- [ ] Déployer sur serveur (Heroku, AWS, DigitalOcean, etc.)
- [ ] Tester endpoints en production
- [ ] Vérifier SSL/HTTPS
- [ ] Configurer backups database

### Monitoring
- [ ] Activer logs
- [ ] Monitoring health checks
- [ ] Alertes erreurs

---

## ⚠️ Validations Critiques

### Avant Submission
- [ ] Tous les endpoints testés et documentés
- [ ] Authentification JWT fonctionne correctement
- [ ] 2FA implémenté pour admins
- [ ] Emails de notification envoyés
- [ ] PDFs générés correctement
- [ ] Base de données synchronisée
- [ ] Aucune donnée en dur (secrets en variables d'env)
- [ ] Logs d'audit enregistrés
- [ ] RGPD compliant (chiffrement données sensibles)

### Avant Soutenance
- [ ] Code complet et documenté
- [ ] Tous les tests passent
- [ ] Démo live fonctionne sans erreurs
- [ ] Présentation PDF/PPTX prête
- [ ] Vidéo démo prête
- [ ] Tous les membres de l'équipe présents

---

## 📞 Points de Contact Importants

- **Date limite remise**: 10 mai 2026 à 23h59
- **Date soutenance**: 12 mai 2026 à 08h30
- **Présence obligatoire**: Tous les membres du groupe
- **Durée soutenance**: ~20-30 minutes
- **Démonstration**: Live + Vidéo pré-enregistrée

---

**Bon courage! 🚀 Commencez par la Phase 1 dès maintenant!**

