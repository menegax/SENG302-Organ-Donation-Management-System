#GUI Available Organ Test

## Preconditions
- Re-import all records (clean records)

### Test 1
1. Select any patient from the db and set the time of death to be now (and fill out other death details)
2. Add in donations to the patient. (lung, heart, liver)
3. Save to database
4. Navigate to the available organs tab and check that there are 3 organs in the list
5. Check that liver has no bounds on it. (red highlights and countdown from 24 hours)
6. Check that heart and lung have the same bounds with 6 hours as the countdown (and counting down)
    
### Test 2
1. Double click on the liver from the table and add more donations to the patient (kidney, pancreas)
2. Verify the results from test 1
3. Verify that the results are sorted
 and kidney and pancreas are displayed correctly. (kidney should have same bounds as liver, heart)
 
 
### Test 3
1. Add more than 10 organs to the table - this is done by marking a patient as deceased at the current date and time
then adding all organs to the donations.
2. Navigate to available organs view and check that there are 10 organs in the first page.
3. Check that there are > 1 pages and that the other pages are filled as expected.
4. Check that the organs are sorted correctly (lowest time to highest) across ALL pages.
5. Use the inbuilt table sort to check that all the organs are sorted correctly (ensure that this is true across all pages).

### Test 4
1. Ensure that there are greater than 10 organs in the available organs view.
2. Add an organ that has a minute remaining before expiry (heart has 6 hours before expiration so put date of death as 5hr and 59 min before current times).
3. Navigate to available organs view and wait for count down to expire.
4. Check that the page has been populated with organs (not 9) and the organ has been removed.
5. Check that the page count has decreased if there was only 1 organ on the last page before expiry.


### 15/08/2018 - Hayden Taylor

Tests pass.
Did notice that you have to switch tabs after adding more donations.

### 11/09/2018
Tests pass.

**ADD MOST RECENT RESULTS TO GITLAB TEST SUITE SUMMARY**
