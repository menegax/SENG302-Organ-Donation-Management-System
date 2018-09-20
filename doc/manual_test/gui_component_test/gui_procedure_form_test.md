# GUI Procedure Application Form Manual Test

### Procedure buttons invisible for patients
1. Open GUI Application
2. Login as Patient
3. Click Profile button
4. The patient profile screen will be shown
5. Click `View Procedures` button
6. The `Procedures` screen will be shown
7. Verify that the `Add Procedure`, `Edit`, and `Delete` buttons are not visible

### Procedure buttons visible for clinicians
1. Open GUI Application
2. Login as Clinician
3. Click `Search Patients` button
4. Double click on a patient
5. The patient profile screen will be shown
6. Click `Manage Procedures` button
7. The `Procedures` screen will be shown
8. Verify that the `Add Procedure`, `Edit`, and `Delete` buttons are visible

### Edit and Delete buttons disabled by default
1. Open GUI Application
2. Login as Clinician
3. Click `Search Patients` button
4. Double click on a patient
5. The patient profile screen will be shown
6. Click `Manage Procedures` button
7. The `Procedures` screen will be shown
8. Verify that the `Edit` and `Delete` buttons are disabled by default
9. Verify that the `Edit` and `Delete` buttons enable when a procedure is selected

## Precondition (for tests below) - navigate to the Add Procedure screen

1. Open GUI application
2. Login as a clinician
3. Select the 'Search Patients' button
4. The Search Patients screen will be shown
5. Select any patient listed in the patients table
6. The patient 'Profile' screen will be shown
7. Select the 'Manage Procedures' button
8. The 'Procedures' screen will be shown
9. Select the 'Add Procedure' button
10. The 'Add Procedure' screen will be shown


### A null procedure application will be invalid

1. The 'Add Procedure' screen will be shown
2. Summary and date are null (null description and null affected donations have no effect on validation)
3. Select the 'Done' button
4. An error message is displayed stating "Field input(s) are invalid!"
5. The 'Add Procedure' screen will be shown
6. The summary textField will be highlighted red
7. Select the 'Cancel' button
8. The 'Procedures' screen will be shown

### A procedure application with a null summary will be invalid

1. The 'Add Procedure' screen will be shown
2. Summary is null (null description and null affected donations have no effect on validation)
3. Date is the current date
4. Select the 'Done' button
5. An error message is displayed stating "Field input(s) are invalid!"
6. The 'Add Procedure' screen will be shown
7. The summary textField will be highlighted red
8. Select the 'Cancel' button
9. The 'Procedures' screen will be shown
10. There is no null procedure displaying in either pending or previous procedures tables

### A procedure application with a null date will be invalid

1. The 'Add Procedure' screen will be shown
2. Date is null (null description and null affected donations have no effect on validation)
3. Summary is "Valid summary"
4. Select the 'Done' button
5. An error message is displayed stating "Field input(s) are invalid!"
6. The 'Add Procedure' screen will be shown
7. Select the 'Cancel' button
8. The 'Procedures' screen will be shown
9. There is no procedure with summary "Valid Summary" and null date displayed in either pending or previous procedures tables

### A procedure application with an empty summary will be invalid

1. The 'Add Procedure' screen will be shown
2. Summary is " "
3. Date is the current date
4. Description and affected donations are null (and have no effect on validation)
5. Select the 'Done' button
6. An error message is displayed stating "Field input(s) are invalid!"
7. The 'Add Procedure' screen will be shown
8. The summary textField will be highlighted red
9. Select the 'Cancel' button
10. The 'Procedures' screen will be shown
11. There is no procedure with summary " " with current date displayed in either pending or previous procedures tables

### A procedure application with a summary containing special character(s) will be invalid

1. The 'Add Procedure' screen will be shown
2. Summary is "$ummary"
3. Date is the current date
4. Description and affected donations are null (and have no effect on validation)
5. Select the 'Done' button
6. An error message is displayed stating "Field input(s) are invalid!"
7. The 'Add Procedure' screen will be shown
8. The summary textField will be highlighted red
9. Select the 'Cancel' button
10. The 'Procedures' screen will be shown
11. There is no procedure with summary "$ummary" and current date displayed in either pending or previous procedures tables

### A procedure application with a description containing special character(s) will be invalid

1. The 'Add Procedure' screen will be shown
2. Summary is "Valid summary"
3. Date is the current date
4. Description is "Inv@lid description"
5. Affected donations are null (and have no effect on validation)
6. Select the 'Done' button
7. An error message is displayed stating "Field input(s) are invalid!"
8. The 'Add Procedure' screen will be shown
9. Select the 'Cancel' button
10. The 'Procedures' screen will be shown
11. There is no procedure with summary "Valid Summary" and description "Inv@lid description" and current date displayed in either pending or previous procedures tables

### A procedure application with a date that is before the patients date of birth is invalid

1. The 'Add Procedure' screen will be shown
2. Summary is "Valid summary"
3. Date is one day before the selected patients date of birth
4. Description and affected donations are null (and have no effect on validation)
5. Select the 'Done' button
6. An error message is displayed stating "Field input(s) are invalid!"
7. The 'Add Procedure' screen will be shown
8. Select the 'Cancel' button
9. The 'Procedures' screen will be shown
10. There is no procedure with summary "Valid summary" and any date displayed in either pending or previous procedures tables

### A procedure application with a date that is after the current date is valid

1. The 'Add Procedure' screen will be shown
2. Summary is "Valid summary"
3. Date is one day after the current date
4. Description and affected donations are null (and have no effect on validation)
5. Select the 'Done' button
6. The 'Procedures' screen will be shown
7. There is a procedure with summary "Valid summary" displayed in the pending procedures tables
   with the date being one day after the current date, null description, and null affected organs

### A procedure application with a date that is before the current date, not before patient DOB is valid

1. The 'Add Procedure' screen will be shown
2. Summary is "Valid summary"
3. Date is one day before the current date and patient DOB is at least one day before the current date
4. Description and affected donations are null (and have no effect on validation)
5. Select the 'Done' button
6. The 'Procedures' screen will be shown
7. There is a procedure with summary "Valid summary" displayed in the previous procedures tables
   with the date being one day before the current date, null description, and null affected organs
   
### A procedure application with a summary only containing alphabet, numbers, -, and spaces is valid

1. The 'Add Procedure' screen will be shown
2. Summary is "-A summary without 1 special character except for hyphen is valid-"
3. Date is the current date
4. Description and affected donations are null (and have no effect on validation)
5. Select the 'Done' button
6. The 'Procedures' screen will be shown
7. There is a procedure with summary "-A summary without 1 special character except for hyphen is valid-" 
  displayed in the pending procedures tables with the date being the current date, null description, and
  null affected organs
  
### A procedure application with a description only containing alphabet, numbers, -, and spaces is valid

1. The 'Add Procedure' screen will be shown
2. Summary is "A valid summary"
3. Date is the current date
4. Description is "-A description without 1 special character except for hyphen is valid-"
5. Affected donations is null (and has no effect on validation)
6. Select the 'Done' button
7. The 'Procedures' screen will be shown
8. There is a procedure with summary "A valid summary" displayed in the pending procedures tables with the
   date being the current date, description "-A description without 1 special character except for hyphen
   is valid-", and null affected organs
   
### A procedure application with any selected organ as an affected donation is valid
 
 1. The 'Add Procedure' screen will be shown
 2. Summary is "A valid summary"
 3. Date is the current date
 4. Any organ from the list of affected donations is selected as an affected donation
 5. Description is null (and has no effect on validation)
 6. Select the 'Done' button
 7. The 'Procedures' screen will be shown
 8. There is a procedure with summary "A valid summary" displayed in the pending procedures tables with
    the date being the current date, description is null, and affected organs is the selected organ
    
## Test History

### 19/07/2018 - Maree

Procedure buttons invisible for patients: Pass

Procedure buttons visible for clinicians: Pass

Edit and delete buttons disabled by default: Pass

Precondition: Pass

A null procedure application will be invalid: Pass

A null summary application will be invalid: Pass

A procedure application with a summary containing special character(s) will be invalid: Pass

A procedure application with a description containing special character(s) will be invalid: Pass

A procedure application with a date that is before the patients date of birth is invalid: Pass

A procedure application with a date that is after the current date is valid: Pass

A procedure application with a date that is before the current date, not before patient DOB is valid: Pass

A procedure application with a summary only containing alphabet, numbers, -, and spaces is valid: Pass

A procedure application with a description only containing alphabet, numbers, -, and spaces is valid: Pass

A procedure application with any selected organ as an affected donation is valid: Pass

### 14/08/2018 - Aidan

Procedure buttons invisible for patients: Pass

Procedure buttons visible for clinicians: Pass

Edit and delete buttons disabled by default: Pass

Precondition: Pass

A null procedure application will be invalid: Pass

A null summary application will be invalid: Pass

A procedure application with a summary containing special character(s) will be invalid: Pass

A procedure application with a description containing special character(s) will be invalid: Pass

A procedure application with a date that is before the patients date of birth is invalid: Pass

A procedure application with a date that is after the current date is valid: Pass

A procedure application with a date that is before the current date, not before patient DOB is valid: Pass

A procedure application with a summary only containing alphabet, numbers, -, and spaces is valid: Pass

A procedure application with a description only containing alphabet, numbers, -, and spaces is valid: Pass

A procedure application with any selected organ as an affected donation is valid: Pass

**ADD MOST RECENT RESULTS TO GITLAB TEST SUITE SUMMARY**
