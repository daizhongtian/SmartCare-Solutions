# User Stories

## Admin Role User Stories

### Epic
As an admin, I want to use admin-only permissions to manage system data so that I can administer the platform security.

### Acceptance Criteria
- Admin can enter the system using the correct username and password.
- Non-admin user access to the admin page will return a 403 code.
- Admin can add doctor.
- Admin can delete doctor.
- Admin can run a MySQL stored procedure that returns monthly appointment counts.

### User Stories

#### US-ADMIN-01
As an admin, I can enter the system using the correct username and password so that I can securely access the admin portal.  
**Priority:** High  
**Story Points:** 3

#### US-ADMIN-02
As an admin, I can access admin-only pages so that I can manage protected system data.  
**Priority:** High  
**Story Points:** 3

#### US-ADMIN-03
As an admin, I can add doctor so that I can maintain the doctor list in the system.  
**Priority:** High  
**Story Points:** 5

#### US-ADMIN-04
As an admin, I can delete doctor so that I can remove inactive or incorrect doctor records.  
**Priority:** High  
**Story Points:** 5

#### US-ADMIN-05
As an admin, I can run a MySQL stored procedure that returns monthly appointment counts so that I can review monthly appointment statistics.  
**Priority:** Medium  
**Story Points:** 5


## Patient Role User Stories

### Epic
As a patient, I want to register and log in to get an appointment and manage my appointment, and get consultation with doctor, so that I can receive care online.

### User Stories

#### US-PATIENT-01
As a patient, I can check doctor list without log in so that I can browse available doctors before creating an account.  
**Priority:** High  
**Story Points:** 2

#### US-PATIENT-02
As a patient, I can use email and password to register so that I can create an account and get appointment access.  
**Priority:** High  
**Story Points:** 3

#### US-PATIENT-03
As a patient, I can log in to the website so that I can get appointment services.  
**Priority:** High  
**Story Points:** 3

#### US-PATIENT-04
As a patient, I can log in to the website to manage my appointment so that I can update or review my booking details.  
**Priority:** High  
**Story Points:** 5

#### US-PATIENT-05
As a patient, I can log out of the website so that my account remains secure.  
**Priority:** Medium  
**Story Points:** 1

#### US-PATIENT-06
As a patient, I can log in and get consultation with doctor so that I can receive care online.  
**Priority:** High  
**Story Points:** 5

#### US-PATIENT-07
As a patient, I can check nearest appointment so that I can choose the earliest available time for care.  
**Priority:** Medium  
**Story Points:** 3


## Doctor Role User Stories

### Epic
As a doctor, I want to log in and manage my profile, availability, and appointments so that I can stay organized and provide care on time.

### User Stories

#### US-DOCTOR-01
As a doctor, I can log in to the portal so that I can manage my data and appointments.  
**Priority:** High  
**Story Points:** 3

#### US-DOCTOR-02
As a doctor, I can log out of the portal so that my account and data stay secure.  
**Priority:** Medium  
**Story Points:** 1

#### US-DOCTOR-03
As a doctor, I can view my appointment calendar so that I can stay organized.  
**Priority:** High  
**Story Points:** 5

#### US-DOCTOR-04
As a doctor, I can mark my available/unavailable time slots so that patients only book when I am free.  
**Priority:** High  
**Story Points:** 5

#### US-DOCTOR-05
As a doctor, I can update my personal and professional information so that patients see the latest details.  
**Priority:** Medium  
**Story Points:** 3

#### US-DOCTOR-06
As a doctor, I can view upcoming appointments so that I can prepare in advance.  
**Priority:** High  
**Story Points:** 3
