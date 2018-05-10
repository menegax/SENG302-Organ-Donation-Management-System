# CLI Donor Remove Manual Test

We need to ensure that when removing a donor from the database it is also reflected in the saved disk file.

1. Import dishwasher data using `donor import <arg>`
2. View available donors by using `donor view --all`
3. Attempt to remove a donor using `donor remove -i=<ird>`
3. Save the data using `save`
4. View the raw file on disk to see if the donor was successfully removed from the file