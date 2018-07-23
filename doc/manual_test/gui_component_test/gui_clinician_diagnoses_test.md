#GUI Clinician Update Diagnoses Manual Test

##Preconditions

1. A clinician has logged in to the application.
2. The clinician has navigated to the Search Patients screen.
3. The clinician double clicks on a patient to open the patient's profile
4. The clinician selects the disease history button
5. The diagnoses screen is shown

###Add a diagnosis

1. The clinician types a valid diagnosis name into the add text field
2. The clinician selects the add button
3. The diagnosis is added to the current diagnoses table

###Add a diagnosis already in current

1. The clinician types a diagnosis name into the text field that is already in the current disease list
2. The clinician selects the add button
3. An alert is shown stating the disease is already registered and no disease is added

###Add a diagnosis already in past

1. The clinician types a diagnosis name into the text field that is already in the past disease list
2. The clinician selects the add button
3. An alert is shown stating the disease is already registered
4. The disease of the same name is moved to the current disease list

###Add an invalid disease name

1. The clinician types a diagnosis name into the text field that is not valid
2. The clinician selects the add button
3. An alert is shown stating the disease name is invalid and no disease is added

###Delete a diagnosis

1. The clinician selects a disease in either the past or current disease list
2. The clinician selects the delete button
3. The selected disease is deleted and is no longer present in the list


##Test History

###18/07/2018 - Maree

Preconditions: Pass

Add a diagnosis: Pass

Add a diagnosis already in current: Pass

Add an invalid disease name: Pass

Delete the diagnosis: Pass