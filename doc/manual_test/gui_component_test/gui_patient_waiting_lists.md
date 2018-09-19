# GUI Organ Waiting List Manual Test

##Precondition 1: navigate to the Organ Request screen for a patient
1. Open GUI application
2. Login as a clinician
3. Open Patient Search screen
4. Double click on a patient in the table
5. The patient's profile is opened
6. The Manage Requests button is selected
7. The request management screen is open

###An organ request is added when a patient has a requested organ added
1. The clinician selects one or more organs to add to a patient's required organs
2. The clinician selects the save button
3. In the main window, the clinician selects the back button
4. In the clinician home window, the clinician selects the Organ Waiting List button
5. The organ waiting list screen is shown, with the patient's requested organs shown in the table

###An organ request is removed when a patient has a requested organ removed
1. The clinician selects one or more organs to remove from a patient's required organs
2. The clinician selects the save button
3. In the main window, the clinician selects the back button
4. In the clinician home window, the clinician selects the Organ Waiting List button
5. The organ waiting list screen is shown, with the patient's removed requests absent from the table

## Precondition 2: navigate to the Waiting List screen

1. Open GUI application
2. Login as a clinician
3. Select the Organ Waiting List button
4. The Organ Waiting List screen will be shown

### The waiting list is sorted based on name

1. The Organ Waiting List screen is shown
2. The header titled "Name" is selected
3. The list is sorted alphabetically by name

### The waiting list is sorted based on organ

1. The Organ Waiting List screen is shown
2. The header titled "Organ" is selected
3. The list is sorted alphabetically by organ

### The waiting list is sorted based on region

1. The Organ Waiting List screen is shown
2. The header titled "Region" is selected
3. The list is sorted alphabetically by region

### A patient profile is opened by double clicking

1. The Organ Waiting List screen is shown
2. The user double clicks on a row containing a patient
3. The patient profile selected is opened in a new window

### Multiple patient profile are opened

1. The Organ Waiting List screen is shown
2. The user double clicks on a row containing a patient
3. The patient profile selected is opened in a new window
4. The user double clicks on another row containing a patient
5. A second patient window is opened with the selected patient

### Invalid double click action

1. The Organ Waiting List screen is open
2. The user double clicks on a row that does not contain any patient information
3. No window is opened. The Organ Waiting List screen remains open.

## Test History

### 19/07/2018 - Maree

Precondition 1: Pass

An organ request is added when a patient has a requested organ added: Pass

An organ request is removed when a patient has a requested organ removed: Pass

Precondition 2: Pass

The waiting list is sorted based on name: Pass

The waiting list is sorted based on organ: Pass

The waiting list is sorted based on region: Pass

A patient profile is opened by double clicking: Pass

Multiple patient profiles are opened: Pass

### 18/04/2018 - Aidan

Precondition 1: Pass

An organ request is added when a patient has a requested organ added: Pass

An organ request is removed when a patient has a requested organ removed: Pass

Precondition 2: Pass

The waiting list is sorted based on name: Pass

The waiting list is sorted based on organ: Pass

The waiting list is sorted based on region: Pass

A patient profile is opened by double clicking: Pass

Multiple patient profiles are opened: Pass

**ADD MOST RECENT RESULTS TO GITLAB TEST SUITE SUMMARY**
