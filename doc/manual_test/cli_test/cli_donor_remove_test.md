# CLI Donor Remove Manual Test

We need to ensure that when removing a patient from the database it is also reflected in the saved disk file.

1. Import dishwasher data using `patient import <arg>`
2. View available patients by using `patient view --all`
3. Attempt to remove a patient using `patient remove -i=<ird>`
3. Save the data using `save`
4. View the raw file on disk to see if the patient was successfully removed from the file

**ADD MOST RECENT RESULTS TO GITLAB TEST SUITE SUMMARY**
