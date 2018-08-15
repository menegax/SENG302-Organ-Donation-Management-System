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
 
### 15/08/2018 - Hayden Taylor

Tests pass.
Did notice that you have to switch tabs after adding more donations.
