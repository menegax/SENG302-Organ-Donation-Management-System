# Medication Ingredients Manual Tests

### Navigate to the medications page

1. Log in as a clinician (default is id 0)
2. Once logged in, click on the `Search Patients` button to navigate to the patient search
3. Double click on any of the patients listed
4. Click on the `Medications` button
5. Verify that the medications page is now displayed

### GUI ingredients show on select test

1. Add `Codeine` to the medication list using the test `GUI Medication adding with valid input test` listed under `gui_medication_test`
2. Click on Codeine to select it
3. Verify Codeine is selected
4. Verify that the ingredients for Codeine should appear in the ingredients and interactions list under the entry `Ingredients for 'Codeine': `

### GUI ingredients history test

1. Perform the steps to load ingredients for `Codeine` by running `GUI ingredients show on select test`
2. Add `Morphine` to the medication list using the test `GUI Medication adding with valid input test` listed under `gui_medication_test`, using `Morphine` in place of `Codeine`
3. Click on Morphine to select it
4. Verify Morphine is selected
5. Verify that the ingredients for Morphine and Codeine appear in the ingredients and interactions list

### GUI clear ingredients list test

1. Perform the steps to load ingredients for `Codeine` by running `GUI ingredients show on select test`
2. Once the ingredients for Codeine are listed, click the `Clear` button to clear the list
3. Verify that the ingredients list now contains no entries

##Test History

###19/07/2018 - Maree

Navigate to the medications page: Pass

GUI ingredients show on select test: Pass

GUI ingredients history test: Pass

GUI clear ingredients test: Pass

###14/08/2018 - Aidan

Navigate to the medications page: Pass

GUI ingredients show on select test: Pass

GUI ingredients history test: Pass

GUI clear ingredients test: Pass



