# 🏥 MediSync Backend - Spécifications Détaillées

## 📋 Vue d'ensemble
Le backend MediSync doit être une **API REST sécurisée** construite avec **Spring Boot** qui gère:
- Authentification & Autorisation (JWT + Roles)
- Gestion des utilisateurs (Patients, Médecins, Secrétaires, Administrateurs)
- Rendez-vous & Planning
- Dossiers médicaux
- Prescriptions & Comptes rendus
- Facturation & Paiements
- Notifications
- Audit & Logs

---

## 🗄️ Modèles de Données (Entités JPA)

### 1. **User (Utilisateur) - Parent pour tous les rôles**
```
- id (Long)
- email (String, unique)
- password (String, hashé)
- firstName (String)
- lastName (String)
- phone (String)
- userRole (ENUM: PATIENT, DOCTOR, SECRETARY, ADMIN)
- isActive (Boolean)
- lastLogin (LocalDateTime)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)
```

### 2. **Patient** (extends User)
```
- socialSecurityNumber (String, unique) - Numéro de sécurité sociale
- dateOfBirth (LocalDate)
- gender (ENUM: MALE, FEMALE, OTHER)
- address (String)
- city (String)
- zipCode (String)
- bloodType (String)
- allergies (List<String>) - JSON array
- emergencyContact (String)
- emergencyPhone (String)
- dependents (List<Patient>) - Pour les mineurs/dépendants
- medicalRecords (List<MedicalRecord>)
- appointments (List<Appointment>)
- prescriptions (List<Prescription>)
- documents (List<MedicalDocument>)
```

### 3. **Doctor** (extends User)
```
- specialties (List<String>) - Cardiologie, Pédiatrie, etc.
- licenseNumber (String)
- languages (List<String>) - Langues parlées
- consultationRate (BigDecimal) - Tarif de consultation
- availabilitySchedule (List<TimeSlot>)
- office (Office)
- consultationRooms (List<ConsultationRoom>)
- appointments (List<Appointment>)
- medicalRecords (List<MedicalRecord>)
- prescriptions (List<Prescription>)
```

### 4. **Secretary** (extends User)
```
- office (Office)
- responsibilities (List<String>) - Accueil, facturation, etc.
- managedAppointments (List<Appointment>)
- invoices (List<Invoice>)
```

### 5. **Admin** (extends User)
```
- offices (List<Office>) - Cliniques gérées
- twoFactorEnabled (Boolean)
- twoFactorSecret (String) - Pour TOTP
```

### 6. **Office (Clinique/Cabinet)**
```
- id (Long)
- name (String)
- address (String)
- city (String)
- zipCode (String)
- phone (String)
- email (String)
- openingHours (JSON) - Horaires ouverture
- specialties (List<String>)
- doctors (List<Doctor>)
- secretaries (List<Secretary>)
- rooms (List<ConsultationRoom>)
- appointments (List<Appointment>)
```

### 7. **Appointment (Rendez-vous)**
```
- id (Long)
- patient (Patient)
- doctor (Doctor)
- office (Office)
- room (ConsultationRoom)
- appointmentDateTime (LocalDateTime)
- durationMinutes (Integer) - 15, 30, 60
- consultationType (ENUM: GENERAL, FOLLOW_UP, EMERGENCY)
- status (ENUM: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED)
- reason (String) - Motif de consultation
- notes (String) - Remarques
- reminder24hSent (Boolean)
- reminder1hSent (Boolean)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)
```

### 8. **ConsultationRoom (Salle de consultation)**
```
- id (Long)
- name (String) - "Salle A", "Salle B", etc.
- office (Office)
- equipment (List<String>) - Équipements disponibles
- isAvailable (Boolean)
- appointments (List<Appointment>)
```

### 9. **MedicalRecord (Dossier Médical)**
```
- id (Long)
- patient (Patient)
- doctor (Doctor)
- appointment (Appointment)
- consultationDate (LocalDateTime)
- chief_complaint (String) - Motif principal
- history (String) - Historique de consultation
- diagnosis (String) - Diagnostic
- examination_findings (String) - Examen physique
- treatment_plan (String) - Plan de traitement
- notes (String)
- attachments (List<MedicalDocument>)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)
```

### 10. **Prescription (Ordonnance)**
```
- id (Long)
- medicalRecord (MedicalRecord)
- patient (Patient)
- doctor (Doctor)
- medications (List<Medication>)
- prescriptionDate (LocalDate)
- expirationDate (LocalDate)
- instructions (String) - Instructions d'utilisation
- status (ENUM: ACTIVE, EXPIRED, CANCELLED)
- pdfPath (String) - Chemin PDF généré
- createdAt (LocalDateTime)
```

### 11. **Medication (Médicament)**
```
- id (Long)
- name (String)
- dosage (String) - "500mg", "10ml"
- frequency (String) - "3x par jour"
- duration (Integer) - Durée en jours
- instructions (String) - Mode d'emploi
- prescription (Prescription)
```

### 12. **MedicalDocument (Document Médical)**
```
- id (Long)
- patient (Patient)
- medicalRecord (MedicalRecord)
- fileName (String)
- fileType (ENUM: PDF, JPG, PNG, DICOM)
- fileSize (Long) - Taille en bytes (max 20MB)
- filePath (String) - Chemin de stockage
- uploadDate (LocalDateTime)
- documentType (String) - "Résultat labo", "Radio", etc.
```

### 13. **Invoice (Facture)**
```
- id (Long)
- appointment (Appointment)
- secretary (Secretary)
- patient (Patient)
- invoiceDate (LocalDate)
- totalAmount (BigDecimal)
- items (List<InvoiceItem>)
- paymentStatus (ENUM: UNPAID, PAID, OVERDUE)
- paymentDate (LocalDateTime)
- pdfPath (String)
- createdAt (LocalDateTime)
```

### 14. **InvoiceItem (Ligne de facture)**
```
- id (Long)
- invoice (Invoice)
- description (String)
- quantity (Integer)
- unitPrice (BigDecimal)
- total (BigDecimal)
```

### 15. **TimeSlot (Créneau horaire)**
```
- id (Long)
- doctor (Doctor)
- startTime (LocalTime)
- endTime (LocalTime)
- durationMinutes (Integer)
- dayOfWeek (ENUM: MONDAY, ..., SUNDAY)
- isAvailable (Boolean)
- appointment (Appointment) - null si disponible
```

### 16. **AuditLog (Journal d'audit)**
```
- id (Long)
- user (User)
- action (String) - "VIEW_RECORD", "MODIFY_DATA", etc.
- targetEntity (String) - Type d'entité modifiée
- targetId (Long)
- details (JSON)
- timestamp (LocalDateTime)
- ipAddress (String)
```

### 17. **Review (Avis/Évaluation)**
```
- id (Long)
- appointment (Appointment)
- patient (Patient)
- doctor (Doctor)
- rating (Integer) - 1 à 5 étoiles
- comment (String)
- createdAt (LocalDateTime)
```

### 18. **Notification**
```
- id (Long)
- user (User)
- type (ENUM: APPOINTMENT_REMINDER, APPOINTMENT_CHANGE, PRESCRIPTION_READY)
- message (String)
- isRead (Boolean)
- sentAt (LocalDateTime)
```

---

## 🔌 API REST Endpoints

### **Authentication (Authentification)**
```
POST   /api/auth/register              - Inscription
POST   /api/auth/login                 - Connexion
POST   /api/auth/refresh-token         - Renouveler token JWT
POST   /api/auth/logout                - Déconnexion
POST   /api/auth/oauth/google          - OAuth 2.0 Google
GET    /api/auth/2fa/setup             - Configurer 2FA
POST   /api/auth/2fa/verify            - Vérifier 2FA
```

### **Patient Endpoints**
```
GET    /api/patients/{id}              - Profil patient
PUT    /api/patients/{id}              - Modifier profil
GET    /api/patients/{id}/appointments - Liste rendez-vous
POST   /api/appointments               - Créer rendez-vous
PUT    /api/appointments/{id}          - Modifier rendez-vous
DELETE /api/appointments/{id}          - Annuler rendez-vous

GET    /api/doctors                    - Lister tous les médecins
GET    /api/doctors/{id}               - Détails médecin
GET    /api/doctors/{id}/availability  - Disponibilités médecin

GET    /api/patients/{id}/records      - Dossier médical
GET    /api/records/{id}               - Détails record
POST   /api/records/{id}/documents     - Télécharger document

GET    /api/patients/{id}/prescriptions - Ordonnances
GET    /api/prescriptions/{id}/pdf     - Télécharger PDF

POST   /api/notifications              - Obtenir notifications
PUT    /api/notifications/{id}/read    - Marquer comme lue

POST   /api/reviews                    - Laisser un avis
POST   /api/complaints                 - Signaler problème
```

### **Doctor Endpoints**
```
GET    /api/doctors/me                 - Mon profil
PUT    /api/doctors/me                 - Modifier profil
GET    /api/doctors/me/appointments    - Mes rendez-vous
GET    /api/doctors/me/schedule        - Mon planning
PUT    /api/doctors/me/availability    - Paramétrer disponibilité

GET    /api/patients/{id}              - Détails patient
GET    /api/patients/{id}/records      - Dossier patient
POST   /api/records                    - Créer compte rendu
PUT    /api/records/{id}               - Modifier compte rendu

POST   /api/prescriptions              - Créer ordonnance
GET    /api/medications                - Rechercher médicaments
POST   /api/records/{id}/documents     - Ajouter document
```

### **Secretary Endpoints**
```
POST   /api/patients                   - Créer compte patient
GET    /api/appointments               - Liste rendez-vous
POST   /api/appointments               - Créer rendez-vous
PUT    /api/appointments/{id}          - Modifier rendez-vous
DELETE /api/appointments/{id}          - Annuler rendez-vous

GET    /api/invoices                   - Liste factures
POST   /api/invoices                   - Créer facture
GET    /api/invoices/{id}/pdf          - Télécharger PDF

GET    /api/services                   - Liste des actes
POST   /api/services/{id}/invoice      - Ajouter à facture
```

### **Admin Endpoints**
```
GET    /api/admin/users                - Liste utilisateurs
POST   /api/admin/users                - Créer utilisateur
PUT    /api/admin/users/{id}           - Modifier utilisateur
DELETE /api/admin/users/{id}           - Supprimer utilisateur

GET    /api/admin/offices              - Cliniques
POST   /api/admin/offices              - Créer clinique
PUT    /api/admin/offices/{id}         - Modifier clinique

GET    /api/admin/roles                - Rôles & permissions
PUT    /api/admin/roles/{id}           - Modifier rôles

GET    /api/admin/audit-logs           - Journal d'audit
GET    /api/admin/statistics           - Statistiques
GET    /api/admin/dashboard            - Tableau de bord
GET    /api/admin/financial-reports    - Rapports financiers
```

---

## 🔐 Sécurité

### **Authentification**
- JWT (JSON Web Tokens) pour les sessions
- Hachage des mots de passe avec BCrypt
- Validation: 8 caractères min, 1 majuscule, 1 chiffre, 1 caractère spécial

### **Autorisation**
- **Role-Based Access Control (RBAC)**:
  - `PATIENT` - Accès à son propre dossier
  - `DOCTOR` - Accès aux dossiers patients, planning personnel
  - `SECRETARY` - Gestion rendez-vous, facturation
  - `ADMIN` - Accès complet + 2FA obligatoire

### **2FA (Two-Factor Authentication)**
- TOTP (Time-based One-Time Password)
- Intégration Google Authenticator/Authy
- Obligatoire pour administrateurs

### **Audit & Compliance**
- Journal d'audit complet pour actions sensibles
- RGPD compliance (droit à l'oubli, export données)
- Chiffrement des données sensibles
- Logs d'accès avec IP

---

## 🗃️ Base de Données

**Type**: MySQL ou PostgreSQL (relationnelle)

**Tables principales**:
- `users`
- `patients`
- `doctors`
- `secretaries`
- `admins`
- `offices`
- `appointments`
- `consultation_rooms`
- `medical_records`
- `prescriptions`
- `medications`
- `medical_documents`
- `invoices`
- `invoice_items`
- `time_slots`
- `audit_logs`
- `reviews`
- `notifications`

---

## 📦 Structure du Projet Spring Boot

```
src/main/java/ma/medisync/medisync_backend/
├── config/                          # Configuration (Security, CORS, etc.)
│   ├── SecurityConfig.java
│   ├── JwtAuthenticationFilter.java
│   ├── CorsConfig.java
│   └── FileStorageConfig.java
├── controller/                      # API Controllers
│   ├── AuthController.java
│   ├── PatientController.java
│   ├── DoctorController.java
│   ├── SecretaryController.java
│   ├── AdminController.java
│   ├── AppointmentController.java
│   ├── MedicalRecordController.java
│   ├── PrescriptionController.java
│   └── InvoiceController.java
├── service/                         # Business Logic
│   ├── AuthService.java
│   ├── UserService.java
│   ├── PatientService.java
│   ├── DoctorService.java
│   ├── AppointmentService.java
│   ├── MedicalRecordService.java
│   ├── PrescriptionService.java
│   ├── InvoiceService.java
│   ├── NotificationService.java
│   ├── EmailService.java
│   ├── FileStorageService.java
│   └── AuditService.java
├── repository/                      # Data Access Layer
│   ├── UserRepository.java
│   ├── PatientRepository.java
│   ├── DoctorRepository.java
│   ├── AppointmentRepository.java
│   ├── MedicalRecordRepository.java
│   ├── PrescriptionRepository.java
│   ├── InvoiceRepository.java
│   ├── AuditLogRepository.java
│   └── ...
├── entity/                          # JPA Entities
│   ├── User.java
│   ├── Patient.java
│   ├── Doctor.java
│   ├── Secretary.java
│   ├── Admin.java
│   ├── Appointment.java
│   ├── MedicalRecord.java
│   ├── Prescription.java
│   ├── Invoice.java
│   ├── AuditLog.java
│   └── ...
├── dto/                             # Data Transfer Objects
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── UserDTO.java
│   ├── PatientDTO.java
│   ├── AppointmentDTO.java
│   └── ...
├── exception/                       # Custom Exceptions
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   ├── InvalidCredentialsException.java
│   └── ...
├── util/                            # Utilities
│   ├── JwtTokenProvider.java
│   ├── ValidationUtil.java
│   ├── FileUploadUtil.java
│   └── PdfGenerator.java
├── listener/                        # Event Listeners
│   ├── AppointmentEventListener.java
│   └── NotificationListener.java
└── MedisyncBackendApplication.java  # Main Application Class

resources/
├── application.properties            # Configuration
├── application-dev.properties
├── application-prod.properties
└── db/migration/                     # Flyway migrations
```

---

## 🚀 Technologies à Utiliser

| Domaine | Technologie |
|---------|------------|
| **Framework** | Spring Boot 3.x |
| **ORM** | JPA/Hibernate |
| **Database** | MySQL 8.0+ / PostgreSQL 14+ |
| **Authentication** | JWT + Spring Security |
| **2FA** | TOTP (javaxt-totp) |
| **Email** | Spring Mail + Thymeleaf |
| **PDF Generation** | iText ou Apache PDFBox |
| **File Storage** | Local filesystem ou Cloud (AWS S3) |
| **Validation** | Bean Validation (Hibernate Validator) |
| **Logging** | SLF4J + Logback |
| **Testing** | JUnit 5 + Mockito |
| **Build Tool** | Maven |

---

## 📋 Checklist de Développement

### Phase 1: Foundation
- [ ] Configurer Spring Boot + Database
- [ ] Implémenter entités JPA
- [ ] Créer repositories
- [ ] Configurer JPA/Hibernate

### Phase 2: Authentication & Security
- [ ] Implémenter JWT
- [ ] Configurer Spring Security
- [ ] Implémenter login/register
- [ ] Implémenter 2FA pour Admin
- [ ] Système de rôles & permissions

### Phase 3: Patient Features
- [ ] API Patient (CRUD)
- [ ] Recherche médecins
- [ ] Gestion rendez-vous
- [ ] Dossier médical
- [ ] Téléchargement documents

### Phase 4: Doctor Features
- [ ] API Doctor (CRUD)
- [ ] Planning & disponibilités
- [ ] Gestion consultations
- [ ] Comptes rendus
- [ ] Prescriptions électroniques

### Phase 5: Administrator Features
- [ ] Gestion utilisateurs
- [ ] Gestion établissement
- [ ] Audit logs
- [ ] Tableau de bord & statistiques

### Phase 6: Additional Features
- [ ] Système facturation
- [ ] Notifications (email + push)
- [ ] Avis/Evaluations
- [ ] Export PDF/Excel
- [ ] Tests & Sécurité

---

## 📝 Notes Importantes

1. **Créneaux Horaires**: Fixes (15, 30, 60 min)
2. **RGPD Compliant**: Chiffrer données sensibles
3. **Audit Trail**: Logger tous les accès médicaux
4. **Notifications**: Email à 24h et 1h avant RDV
5. **Tarification**: Variable par praticien/acte
6. **File Upload**: Max 20MB, formats: PDF, JPG, PNG, DICOM

