# CCC-ACT: CCC Advance Clearance Tracker

CCC-ACT is a Java Swing desktop application for demonstrating an advance clearance tracking workflow for City College of Calapan. It uses MVC-style organization, in-memory session data, fictional demo accounts, and role-based screens for students, department staff, and administrators.

## Requirements

- OpenJDK 25
- No database, web server, or external dependencies
- Java Swing desktop environment

> Data is stored in memory only. Closing and reopening the application recreates the original demo data and clears session changes.

## Compile, Test, and Run

On this machine, Java 25 is available at `C:\Users\Admin\.jdks\openjdk-25.0.1\bin`.

```powershell
New-Item -ItemType Directory -Force out | Out-Null
C:\Users\Admin\.jdks\openjdk-25.0.1\bin\javac.exe -d out (Get-ChildItem -Recurse src\main\java,src\test\java -Filter *.java).FullName
C:\Users\Admin\.jdks\openjdk-25.0.1\bin\java.exe -cp out tests.BusinessLogicTest
C:\Users\Admin\.jdks\openjdk-25.0.1\bin\java.exe -cp out app.App
```

Shortcut:

```powershell
.\run.ps1
```

## Demo Credentials

All preloaded demo accounts start with temporary password `Temp@123` and must change it on first login.

| Role | Username |
| --- | --- |
| Administrator | `admin` |
| Student | `student.luna` |
| Student | `student.mika` |
| Student | `student.noel` |
| Staff | `staff.treasurer` |
| Staff | `staff.president` |
| Staff | `staff.curricular` |
| Staff | `staff.noncurricular` |
| Staff | `staff.treasurer2` |
| Staff | `staff.curricular2` |

## Major Features

- In-system student registration by the administrator with duplicate username validation
- PBKDF2 salted password hashing
- Temporary first-login password change
- Administrator-assisted password recovery through temporary password issuance
- Student dashboard for clearance progress, flagged remarks, contact profile edits, name-change requests, and authorized audit history
- Staff dashboard for assigned-department queues, filtering, approval, flagging, re-review, and authorized audit history
- Administrator dashboard for accounts, staff assignment, student records, department configuration, semester settings, bulk clearance generation, all records, name-change decisions, and audit history
- Required versus optional department handling
- Progress calculation based only on required departments
- Session-only audit log

## Demonstration Flow

1. Sign in as `admin` with `Temp@123`, then set a new password.
2. Open **Students** to register or edit student records.
3. Open **Generate Clearance**, select students, and generate records.
4. Sign out and sign in as a staff account, then approve or flag assigned department entries.
5. Sign in as the affected student to view progress, read flagged remarks, and mark an issue addressed.
6. Return to staff to re-review the pending item.
7. Submit a student displayed-name request from **Settings**, then approve or reject it as admin.
8. Open audit screens to show role-based audit visibility.

## Project Structure

- `src/main/java/app` - application entry point
- `src/main/java/model` - domain models and enums
- `src/main/java/service` - business logic, validation, authorization, auditing
- `src/main/java/data` - in-memory session data and demo initialization
- `src/main/java/ui` - Java Swing frames, dialogs, and dashboard panels
- `src/main/java/util` - password hashing, validation, formatting, UI helpers
- `src/test/java/tests` - dependency-free business logic test runner
- `docs` - project documentation for defense

## Limitations

- No persistent data storage
- No email/SMS notifications
- No official college-system integration
- Proposed department list is configurable demo data and should be institutionally verified
- Historical cycles exist only during the current application run
