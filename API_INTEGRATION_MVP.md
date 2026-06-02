# MediSync Backend MVP Integration Guide

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
the slot. Patients can only read or modify their own appointments.

## Persistent local database

The default `local` profile uses MySQL and keeps data after restarts. Follow
[`MYSQL_SETUP.md`](MYSQL_SETUP.md) before starting the backend for the first time.

Use the temporary H2 profile only for isolated testing:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Current MVP limits

- Appointment request and response DTO cleanup is still pending; the current payload uses
  nested entity IDs.
- Medical-record ownership hardening and document metadata integration remain pending.
- Production deployment environment variables still need to be set before deployment.
