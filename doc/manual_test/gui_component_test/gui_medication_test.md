# Medication Manual Tests

### Navigate to the medications page

1. Log in as a clinician (default is id 0)
2. Once logged in, click on the `Search Patients` button to navigate to the patient search
3. Double click on any of the patients listed
4. Click on the `Manage Medications` button
5. Verify that the medications page is now displayed

### GUI Medication adding with valid input test

1. Navigate to the medication page using the steps listed under `Navigate to the medications page`
2. Check if `Codeine` is present in either the current or past medications list. If it is, remove it using steps 3 onwards in `GUI Medication delete test`
2. Enter `Codeine` into the new medication textbox that is not in the patients current or past medications
3. Add it using the `Register` button
4. Verify that the medication is now in the current medications list

### GUI Medication adding with invalid input test

1. Navigate to the medication page using the steps listed under `Navigate to the medications page`
2. Once on the medication page, set the new medication textbox to be empty if it is not already
3. Add it using the `Register` button
4. Verify that a message appears stating that the input is invalid
5. Verify that the current and past medication lists have remained unchanged

### GUI Medication delete test

1. Navigate to the medication page using the steps listed under `Navigate to the medications page`
2. Add `Codeine` by following the steps under `GUI Medication adding with valid input test`
3. Click on the added medication within the current medications list to select it
4. Verify that it is selected
5. Click the `Delete` button to remove it
6. Verify that the medication is no longer in the current (or past) medications list

### GUI Medication moving test

1. Navigate to the medication page using the steps listed under `Navigate to the medications page`
2. Add `Codeine` by following the steps under `GUI Medication adding with valid input test`
3. Click on the added medication within the current medications list to select it
4. Verify that it is selected
5. Click the `Remove` button to move it to the past medications list
6. Verify that the medication is now in the past medications list, and no longer in the current medications list
7. Click on that same medication in the past medications list
8. Verify that it is selected
9. Click on the `Add` button to move it back to the current medications list
10. Verify that the medication is now in the current medications list, and no longer in the past medications list


### GUI Medication adding duplicates test

1. Navigate to the medication page using the steps listed under `Navigate to the medications page`
2. Add `Codeine` by following the steps under the `GUI Medication adding with valid input test`
3. Enter `Codeine` into the new medication textbox
4. Click the `Register` button to attempt to add Codeine
5. Verify that a message appears stating that Codeine is already registered
6. Verify that there is only one entry for `Codeine` in the current medications list

### GUI Medication adding medication that is in history test

1. Navigate to the medication page using the steps listed under `Navigate to the medications page`
2. Add `Codeine` by following the steps under the `GUI Medication adding with valid input test`
3. Select Codeine in the current medications list by clicking on it
4. Click the `Remove` button to move it to the past medications list
5. Verify that Codeine is in the past medications list
6. Enter `Codeine` into the new medication textbox
7. Click the `Register` button to add it
8. Verify that Codeine is removed from the past medications list and is now in the current medications list

### GUI Medication deleting multiple medications

1. Navigate to the medication page using the steps listed under `Navigate to the medications page`
2. Add `Codeine` by following the steps under the `GUI Medication adding with valid input test`
3. Add `Morphine` by following the steps under the `GUI Medication adding with valid input test`, using `Morphine` in place of `Codeine`
4. Verify that the current medications list contains `Codeine` and `Morphine`
5. Select Codeine and Morphine by clicking on both while holding `Shift`
6. Click the `Delete` button to remove them both
7. Verify that neither `Codeine` nor `Morphine` appear in the current or past medications list

##Test History

###19/07/2018 - Maree

Navigate to the medications page: Pass

GUI medication adding with valid input: Pass

GUI medication adding with invalid input test: Pass

GUI medication delete test: Pass

GUI medication moving task: Pass

GUI medication adding duplicates test: Pass

GUI medication adding medication that is in history test: Pass

GUI medication deleting multiple medications: Pass

###14/08/2018 - Aidan

Navigate to the medications page: Pass

GUI medication adding with valid input: Pass

GUI medication adding with invalid input test: Pass

GUI medication delete test: Pass

GUI medication moving task: Pass

GUI medication adding duplicates test: Pass

GUI medication adding medication that is in history test: Pass

GUI medication deleting multiple medications: Pass
