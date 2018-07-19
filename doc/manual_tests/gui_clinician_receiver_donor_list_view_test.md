# Receiving and Donating organs views from clinician testing

### Navigate to clinician view of a patient
1. Log in as a clinician (default is 0)
2. Once Logged in, click on the `Search Patients` button to navigate to the patient search
3. Double click on any of the patients listed
4. Verify that the patient profile is now displayed and you can see both receiving organs and donating organs listviews

### Ensure that organs are listed on the receiving listview
1. Navigate to the patient profile by using the steps described in `Navigate to clinician view of a patient` manual test
2. Add organs to the patients required organ list by clicking on `Manage Requested`
3. Verify that the required organs scene is now displayed
4. Click on a few organs to add organs to the required organs list of the patient (make sure a few organs checkboxes are selected)
5. Click on the save button
6. Verify that the receiving list is filled with the organs you selected

### Ensure that organs are listed on the donating listview
1. Use the same steps as provided in `Ensure that organs are listed on the donating listview`, however use the `Manage Donatons` button instead of `Manage Requested` button as described in step 2. Also ensure that you select different organs as in the receiving list
2. Verify that the donations list is filled with the organs you selected

### Ensure that organs highlighted if patient is donating and receiving the same organ
1. Use the same steps as provided in `Enusre that organs are listed on the donating listview` to add an organ that is already selected to be required/received by the patient (e.g. the patient is donating and receiving the same organ, Liver).
2. Verify that the organs that is the same in both lists is highlighted

### Ensure that donating list and receiving lists are segregated
1. log into a patient profile without any required organs
2. select the profile button
3. the required list view should not be visible in the patient home page but the donating list view should be visible
4. logout and log back in as a patient without any donating organs
5. select the profile button
6. the donating list view should not be visible in the patient home page but the required list view should be visible

##Test History

###18/07/2018 - Maree
Navigate to clinician view of patient: Pass

Ensure that organs are listed on the receiving listview: Pass

Ensure that organs are listed on the donating listview: Pass

Ensure that organs highlighted if patient is donating and receiving the same organ: Pass

Ensure that donating list and receiving lists are segregated: Fail - the listviews are still visible, but empty
