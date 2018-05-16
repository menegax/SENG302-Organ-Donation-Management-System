# GUI Clinician Update Procedure Test

#### Update Procedure Summary Test

1. Login as the default clinician (Staff ID `0`)
2. Click on the `Search Patients` button
3. Double click on the patient `Joe Middle Xavier Bloggs`
4. Click on the `Procedures` button
5. If no procedure exists in either the previous or pending procedure tables, follow the steps in `Add Procedure`
6. Select the existing/new procedure
7. Click the `Edit` button to load the procedure in the procedure form
8. Change the summary to `newSummary`
9. Click the `Done` button to save the changes
10. Verify that the summary of that procedure in the table view is now `newSummary`

#### Update Procedure Organs Test

1. Login as the default clinician (Staff ID `0`)
2. Click on the `Search Patients` button
3. Double click on the patient `Joe Middle Xavier Bloggs`
4. Check if the patient is donation `Liver`, if not, navigate to the donations page and select `Liver` before navigating back to the profile
5. Click on the `Procedures` button
6. If no procedure exists in either the previous or pending procedure tables, follow the steps in `Add Procedure`
7. Select the existing/new procedure
8. Click the `Edit` button to load the procedure in the procedure form
9. Select `Liver` in the affected organs dropdown
10. Click the `Done` button to save the changes
11. Verify that the affected organs of that procedure in the table view contains `Liver`

#### Cancel Update Test

1. Login as the default clinician (Staff ID `0`)
2. Click on the `Search Patients` button
3. Double click on the patient `Joe Middle Xavier Bloggs`
4. Click on the `Procedures` button
5. If no procedure exists in either the previous or pending procedure tables, follow the steps in `Add Procedure`
6. Select the existing/new procedure
7. Click the `Edit` button to load the procedure in the procedure form
8. Change the summary to `unsavedSummary`
9. Click the `Cancel` button to revert changes and leave
10. Verify that the summary of that procedure in the table view has remained unchanged

#### Add Procedure

1. Click on `Add Procedure`
2. In the summary textbox, enter `Brain Surgery`
3. In the description textbox, enter `Conducted brain surgery`
4. Set the date picker to two days in the past
5. Click `Done` to add the procedure