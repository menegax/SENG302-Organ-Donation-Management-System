#GUI Clinician Update Diagnoses Manual Test

##Preconditions

1. A clinician has logged in to the application.
2. The clinician has navigated to the Search Patients screen.
3. The clinician double clicks on a patient to open the patient's profile
4. The clinician selects the disease history button
5. The diagnoses screen is shown
6. The clinician double clicks on a diagnosis
7. The diagnosis update window is shown

###Cancel update with no changes

1. The clinician selects the cancel button without changing any fields
2. The popup window closes and the diagnoses page is shown
3. No changes have been made to the diagnosis

###Cancel update with changes made

1. The clinician edits one or more of the diagnosis attributes
2. The clinician selects the cancel button
3. The popup window closes and the diagnoses page is shown
4. No changes have been made to the diagnosis

###Make valid diagnosis updates

1. The clinician edits the diagnosis attributes so all fields are valid input.
2. The clinician selects the done button
3. The popup window closes and the diagnoses page is shown
4. Changes made to the diagnosis are displayed in the window

###Change past diagnosis to not cured

1. The clinician changes a past diagnosis to be chronic or have no state
2. The clinician selects the done button
3. The popup window closes and the diagnoses page is shown
4. The updated diagnosis is now in the current diseases list


###Change current non-chronic diagnosis to cured

1. The clinician changes a current diagnosis that is not chronic to cured
2. The clinician selects the done button
3. The popup window closes and the diagnoses page is shown
4. The updated diagnosis is now in the past diseases list

###Change current chronic diagnosis to cured

1. The clinician changes a current chronic diagnosis to be cured
2. The clinician selects the done button
3. An alert is shown stating a chronic disease can not be directly cured, and the tags choicebox is highlighted in red

###Invalid diagnosis date entered

1. The clinician enters an invalid date for the diagnosis date
2. The clinician selects the done button
3. An alert is shown stating dates must be valid. The date picker is highlighted in red.

###Invalid diagnosis name entered

1. The clinician enters an invalid name for the diagnosis date
2. The clinician selects the done button
3. An alert is shown stating the diagnosis name must be valid. The diagnosis name field is highlighted in red.

###Update a diagnosis to be equal to another diagnosis

1. The clinician double clicks an existing diagnosis from the viewing screen
2. The clinician enters a disease name and diagnosis date that are both the same as another of the patient's diagnoses
3. The clinician selects the done button
4. An alert is shown stating diseases must be unique

##Test History

###18/07/2018 - Maree

Preconditions: Pass

Cancel update with no changes: Pass

Cancel update with changes made: Pass

Make valid diagnosis updates: Pass

Change past diagnosis to not cured: Fail - the window does not automatically refresh

Change current non-chronic diagnosis to cured: Fail - the window does not automatically refresh

Change current chronic diagnosis to cured: Fail - field not highlighted

Invalid diagnosis date entered: Fail - field not highlighted

Invalid diagnosis name entered: Fail - field not highlighted

Update a diagnosis to be equal to another diagnosis: Pass


