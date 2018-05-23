# GUI Clinician Deregistration Reasoning Manual Test

## Preconditions
1. A clinician has logged in to the application.
2. The clinician has navigated to the Search Patients screen.
3. The clinician double clicks on a patient to open the patient's profile
4. The clinician selects the `Manage Requested` button
5. The manage required organs screen is shown
6. The clinician selects all organs (to test these deregistrations)
7. The clinician selects the `save` button 
8. The clinician selects the `View disease history` button
9. Add a disease `Pox` that is not cured
10. Go back to the patient profile
11. The patient profile page should be displayed with all organs filled in the required organs list view

## Deregistration of Heart due to an ERROR
1. The clinician selects the `Manage Requested` button
2. The manage required organs screen should be displayed
3. The clinician deselects the heart organ from required organs
4. The clinician selects the `save` button
5. The organ deregistration popup screen is displayed
6. The ERROR option is the default reason
7. The clinician selects the `ok` button
8. `Heart` should have been removed from the required organs list in the profile screen


## Deregistration of Lung due to cure with a listed disease
1. The clinician selects the `Manage Requested` button
2. The manage required organs screen should be displayed
3. The clinician deselects the lung organ from required organs
4. The clinician selects the `save` button
5. The organ deregistration popup is displayed
6. Set the deregistration reason to deregistration due to cure
7. In the disease dropdown that appears, select the disease `Pox`
8. Click the `ok` button
9. `Lung` should have been removed from the required organs list in the profile
10. Open the diseases page using the `View Disease History` button
11. `Pox` should now be marked as `Cured`

## Deregistration of Bone due to cure without listed diseases
1. The clinician selects the `Manage Requested` button
2. The manage required organs screen should be displayed
3. The clinician deselects the bone organ from required organs
4. The clinician selects the `save` button
5. The organ deregistration popup is displayed
6. Set the deregistration reason to deregistration due to cure
7. Verify no diseases are selected in the diseases dropdown
8. Click the `ok` button
9. `Bone` should have been removed from the required organs list in the profile

## Deregistration of Cornea due to death with valid date
1. The clinician selects the `Manage Requested` button
2. The manage required organs screen should be displayed
3. The clinician deselects the cornea organ from required organs
4. The clinician selects the `save` button
5. The organ deregistration popup is displayed
6. Set the deregistration reason to deregistration due to death
7. In the date of death datepicker that appears, enter a date that is after the patients birthdate but before the current date
8. Click the `ok` button
9. The patient should now have their date of death listed on their profile
10. The patient should have no organs listed in their receiving organs list

## Deregistration of Middle Ear due to death with invalid date
1. The clinician selects the `Manage Requested` button
2. The manage required organs screen should be displayed
3. The clinician deselects the middle ear organ from required organs
4. The clinician selects the `save` button
5. The organ deregistration popup is displayed
6. Set the deregistration reason to deregistration due to death
7. In the date of death datepicker that appears, enter a date after the current date
8. Click the `ok` button
9. The datepicker should be highlighted as invalid and the reason will not be accepted
10. Change the reason to Error
11. Click the `ok` button
12. `Middle Ear` should have been removed from the required organs list

## Deregistration of one organ due to death should log all other requested organs as due to death
1. The clinician selects the `Manage Requested` button
2. The manage required organs screen should be displayed
3. The clinician deselects one required organ out of the rest that are left
4. The deregistration reason popup scene should now be displayed
5. select the reason being the patient dying
6. select the date as the current date
7. Check in the clinician history log that all of the rest of the required organs were removed for the patient dying as the reason

## After
1. Open the main stage that is open on the `Search Donors` screen
2. Click back to return to the clinician home page
3. The clinician selects the `History` button
4. The history screen should now be displayed
5. The most recent log should state that organ was removed from required organs, with relevant data (eg. diseases cured, date of death)
