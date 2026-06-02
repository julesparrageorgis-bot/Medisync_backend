# MediSync Backend Web Integration Guide

## Local startup

```bash
./mvnw spring-boot:run
```

Base URL: `http://localhost:8080/api`

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Authentication

### Register a patient

`POST /api/auth/register`

```json
{
  "email": "patient@example.com",
  "password": "Password1!",
  "firstName": "Jane",
  "lastName": "Doe"
}
```

Registration creates both the login account and its patient profile. Passwords require at
least eight characters with uppercase, lowercase, digit, and special characters.

### Login

`POST /api/auth/login`

```json
{
  "email": "patient@example.com",
  "password": "Password1!"
}
```

Use the returned token on protected requests:

```text
Authorization: Bearer <token>
```

The login response includes `twoFactorRequired`. Admin accounts must complete
`POST /api/auth/2fa/setup` and `POST /api/auth/2fa/verify` before using protected APIs.

### Refresh JWT

`POST /api/auth/refresh-token`

## Frontend MVP endpoints

| Method | URL | Roles | Purpose |
| --- | --- | --- | --- |
| `GET` | `/api/doctors` | Patient, secretary, admin | List doctors |
| `GET` | `/api/doctors/specialty/{specialty}` | Patient, secretary, admin | Filter doctors |
| `GET` | `/api/offices` | Authenticated users | List clinic offices |
| `GET` | `/api/time-slots/doctor/{doctorId}/available` | Authenticated users | List bookable slots |
| `POST` | `/api/time-slots` | Doctor, admin | Create a slot |
| `POST` | `/api/appointments` | Patient, secretary, admin | Book an available slot |
| `POST` | `/api/appointments/emergency` | Secretary, admin | Book an emergency slot |
| `POST` | `/api/appointments/dependents/{dependentId}` | Patient, secretary, admin | Book for a dependent |
| `GET` | `/api/appointments` | Authenticated users | List allowed appointments |
| `GET` | `/api/appointments/patient/{patientId}` | Authenticated users | List patient appointments |
| `PUT` | `/api/appointments/id/{id}/cancel` | Patient, secretary, admin | Cancel booking |
| `PUT` | `/api/appointments/id/{id}/confirm` | Doctor, secretary, admin | Confirm booking |

Create slots with:

```json
{
  "doctorId": 2,
  "startTime": "2026-06-10T10:00:00",
  "endTime": "2026-06-10T10:30:00"
}
```

Book a slot using the slot start time:

```json
{
  "patient": { "id": 1 },
  "doctor": { "id": 2 },
  "appointmentDate": "2026-06-10T10:00:00",
  "reason": "General consultation"
}
```

Booking requires a future available slot and reserves it atomically. Cancelling releases
the slot. Rescheduling reserves the new slot and releases the old one. Slots accept only
15, 30, or 60 minute durations and cannot overlap.

## Additional web API groups

| Base URL | Purpose |
| --- | --- |
| `/api/prescriptions` | Prescription CRUD and `/id/{id}/pdf` download |
| `/api/medications` | Medication CRUD and `/search?query=` |
| `/api/documents` | Patient-linked secure upload, list, ID-based download, and delete |
| `/api/invoices` | Invoices, items, payments, performed acts, PDF export, and email |
| `/api/operations/rooms` | Consultation rooms |
| `/api/operations/equipment` | Equipment |
| `/api/operations/tariffs` | Tariffs |
| `/api/operations/report-templates` | Medical report templates |
| `/api/operations/doctor-unavailability` | Doctor leave days and unavailable periods |
| `/api/operations/dependents` | Patient dependents |
| `/api/operations/issues` | Issue reports |
| `/api/reviews` | Reviews and admin approval |
| `/api/notifications` | Current-user notifications |
| `/api/audit-logs` | Admin audit-log reads |
| `/api/admin/dashboard` | Room occupancy, no-shows, revenue, unpaid invoices, doctor totals |
| `/api/admin/reports` | Financial JSON, PDF, and XLSX exports |

Medical document upload is multipart:

```text
POST /api/documents/upload
patientId=<id>
documentType=LAB_REPORT
description=<optional text>
file=<PDF, JPG, PNG, DCM, or DICOM file up to 20 MB>
```

## Persistent local database

The default `local` profile uses MySQL and keeps data after restarts. Follow
[`MYSQL_SETUP.md`](MYSQL_SETUP.md) before starting the backend for the first time.

Use the temporary H2 profile only for isolated testing:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Current limits

- Existing legacy CRUD controllers still accept some nested entity ID payloads. Sensitive
  password, TOTP, recursive, social-security, allergy, blood-type, and emergency fields are
  protected from JSON responses.
- Financial reports are generated on demand; persisted monthly report snapshots are not yet stored.
- Production deployment environment variables still need to be set before deployment.
