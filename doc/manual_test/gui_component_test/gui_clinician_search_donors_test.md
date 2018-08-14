# Client filter search patient tests

### Navigate to the search patients page

1. Log in as a clinician (default is id 0)
2. Once logged in, click on the `Search Patients` button to navigate to the patient search

### GUI Clinician Search Donors Test

1. Open GUI application
2. Register many, many patients using different names, capitalization, length, and null or not middle names
3. Log in as a clinician user and go to the search patients scene
4. Search for patients by searching first, middles, and last names. Search results will search each type of name and return results matching the seach
within two characters i.e. "Fox" == "Doe" because it is two characters away.

### Filter patients by age

1. Navigate to the search patients page using the steps listed under `Navigate to the search patients page`
2. Drag the minimum age bar to 28.
3. Drag the maximum age bar to also 28.
4. The results should present only patients that are 28 years old.

### Filter patients by birth gender

1. Navigate to the search patients page using the steps listed under `Navigate to the search patients page`
2. Alter the birth gender combobox so that only male patients are presented.
3. The resulting patients should only be male at birth.

### Filter patients by region

1. Navigate to the search patients page using the steps listed under `Navigate to the search patients page`
2. Alter the region combobox so that only patients from Auckland are presented.
3. The resulting patients should only be from Auckland.

### Filter patients by donating organ

1. Navigate to the search patients page using the steps listed under `Navigate to the search patients page`
2. Alter the donating organ combobox so that only patients donating a liver are presented in the table.
3. The resulting patients should be donating a liver.

### Filter patients by receiving organ

1. Navigate to the search patients page using the steps listed under `Navigate to the search patients page`
2. Alter the receiving organ combobox so that only patients requiring a liver are presented in the table.
3. The resulting patients should require a liver.

### Filter patients by age, birth gender, donating organ and donating checkbox

1. Navigate to the search patients page using the steps listed under `Navigate to the search patients page`
2. Drag the minimum age bar to 28.
3. Drag the maximum age bar to also 28.
4. Alter the birth gender combobox so that only male patients are presented.
5. Alter the donating organ combobox so that only patients donating bone are presented in the table.
6. Ensure that the `Donor` checkbox is checked.
7. The resulting patient(s) left in the table should be a 28 year old male donating bone.

### Filter patients by age, birth gender, receiving organ and receiver checkbox

1. Navigate to the search patients page using the steps listed under `Navigate to the search patients page`
2. Drag the minimum age bar to 28.
3. Drag the maximum age bar to also 28.
4. Alter the birth gender combobox so that only female patients are presented.
5. Alter the receiving organ combobox so that only patients requiring lung are presented in the table.
6. Ensure that the `Receiver` checkbox is checked.
7. The resulting patient(s) left in the table should be a 28 year old female receiver, requiring a lung.

## Test History

### 14/08/2018 - Aidan

Navigate to search patients page: Pass

GUI clinician search donors test: Fail Non-Functional requirements not met - too slow with no indication of current search

Filter patients by age: Pass

Filter patients by birth gender: Pass

Filter patients by region: Pass

Filter patients by donating organ: Pass

Filter patients by receiving organ: Pass

Filter patients by age, birth gender, donating organ and donating checkbox: Pass

Filter patients by age, birth gender, receiving organ and receiving checkbox: Pass
