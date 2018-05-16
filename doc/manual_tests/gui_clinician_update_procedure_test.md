# GUI Clinician Update Procedure Test

#### Update Procedure Test

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