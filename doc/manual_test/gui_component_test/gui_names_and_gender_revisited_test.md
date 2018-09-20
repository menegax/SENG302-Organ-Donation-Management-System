# GUI Names and Genders Revisited Manual Test

## Precondition - navigate to the Search Patients screen

1. Open GUI application
2. Login as a clinician
3. Select the 'Search Patients' button
4. The Search Patients screen will be shown

### The Patient list is sorted based on birth gender

1. The Search Patients screen is shown
2. The header titled "Birth Gender" is selected
3. The list is sorted alphabetically by birth gender
4  The header titled "Birth Gender" is selected
5. The list is sorted alphabetically in the opposite order

### Review the birth gender, preferred gender and preferred name on a patients profile

1. The Search Patients screen is shown
2. A patient in the list is selected by mouse double-click
3. The Profile screen is shown
4. 'Gender Identity:' field shows either 'Man', 'Woman', 'Non-binary', or 'Not set'
3. 'Gender Assigned at Birth' field shows either 'Male', 'Female' or 'Not set'
5. 'Name' field shows preferred name (or first name if not set), middle name(s), and last name

### Edit the preferred name for a patient

1. The Search Patients screen is shown
2. A patient in the list is selected by mouse double-click
3. The Profile screen is shown
4. Select the 'Manage Profile' button
5. Select the 'First Name' textField
6. Enter to the 'First Name' textField: 'legal name'
7. Enter to the 'Preferred Name' textField: 'nickname'
8. Select the 'Save' button
9. 'Name:' field shows 'Nickname' (not 'legal name') followed by middle name(s) and last name

### Edit the preferred gender to Man for a patient

1. The Search Patients screen is shown
2. A patient in the list is selected by mouse double-click
3. The Profile screen is shown
4. Select the 'Manage Profile' button
5. Select the 'Female' Radio button, adjacent to 'Gender assigned at birth'
6. Select the 'Man' Radio button, adjacent to 'Gender identity'
8. Select the 'Save' button
9. 'Gender Identity:' field shows 'Man', and not 'Female', 'Woman', 'Male', 'Non-identity' or 'Not set'

### Edit the preferred gender to Woman for a patient

1. The Search Patients screen is shown
2. A patient in the list is selected by mouse double-click
3. The Profile screen is shown
4. Select the 'Manage Profile' button
5. Select the 'Male' Radio button, adjacent to 'Gender assigned at birth'
6. Select the 'Woman' Radio button, adjacent to 'Gender identity'
8. Select the 'Save' button
9. 'Gender Identity:' field shows 'Woman', and not 'Male', 'Man', 'Female, 'Non-identity' or 'Not set'

### Edit the preferred gender to Non-binary for a patient

1. The Search Patients screen is shown
2. A patient in the list is selected by mouse double-click
3. The Profile screen is shown
4. Select the 'Manage Profile' button
5. Select either the 'Male' or 'Female' Radio buttons, adjacent to 'Gender assigned at birth'
6. Select the 'Non-binary' Radio button, adjacent to 'Gender identity'
8. Select the 'Save' button
9. 'Gender Identity:' field shows 'Non-binary', and not 'Male', 'Female', 'Man', 'Woman', or 'Not set'

## Test History

### 19/07/2018 - Maree

Precondition: Pass

The patient list is sorted based on birth gender: Pass

The patient list shows only searched female birth gender: Pass

The patient list shows only searched male birth gender: Pass

Review the birth gender, preferred gender and preferred name on a patients profile: Pass

Edit the preferred name for a patient: Pass

Edit the preferred gender to Man for a patient: Pass

Edit the preferred gender to Woman for a patient: Pass

Edit the preferred gender to Non-binary for a patient: Pass

### 14/08/2018 - Aidan

Precondition: Pass

The patient list is sorted based on birth gender: Pass

Review the birth gender, preferred gender and preferred name on a patients profile: Pass

Edit the preferred name for a patient: Pass

Edit the preferred gender to Man for a patient: Pass

Edit the preferred gender to Woman for a patient: Pass

Edit the preferred gender to Non-binary for a patient: Pass

**ADD MOST RECENT RESULTS TO GITLAB TEST SUITE SUMMARY**
