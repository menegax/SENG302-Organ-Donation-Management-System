# Potential Matches Test

### Preconditions
There is a recently deceased donor to match an organ for (over 12 years old)

### 1. Check selection based on organ
1. Edit a patient to request the donated organ (all other criteria matching)
2. Edit a separate patient to requested a different organ (all other criteria matching)
3. Open potential matches for the organ to donate  
-> The first edited patient should be visible  
-> The second edited patient should not be visible

### 2. Check selection based on blood type
1. Edit a patient to request the donated organ (all other criteria matching)
2. Edit a separate patient to requested the donated organ with the wrong blood type (all other criteria matching)
3. Open potential matches for the organ to donate  
-> The first edited patient should be visible  
-> The second edited patient should not be visible

### 3. Check selection based on age difference
1. Edit a patient to request the donated organ (all other criteria matching)
2. Edit a separate patient to requested the donated organ with an age of 16 years older (all other criteria matching)
3. Open potential matches for the organ to donate  
-> The first edited patient should be visible  
-> The second edited patient should not be visible

### 4. Check selection based on age (under 12)
1. Find a deceased donor under 12 years of age
2. Edit a patient to request the donated organ (all other criteria matching - under 12)
3. Edit a separate patient to requested the donated organ with an age of 13 year old (all other criteria matching)
4. Open potential matches for the organ to donate  
-> The first edited patient should be visible  
-> The second edited patient should not be visible

### 4. Check default ordering
1. Create a request for the required organ (all other criteria matching)
2. Create a different request for the required organ (all other criteria matching)
3. Open potential matches for the organ to donate
-> The first request created should appear first in the table

### 5. Check double click open
1. Navigate to the potential matches screen with at least one potential match
2. Double click on the first row in the table
-> The profile of the patient clicked on should open

### 6. Check null blood group
1. Create a donated organ from a patient without a set blood group
2. Attempt to view potential matches for that donation
-> The status bar should update saying a blood group needs to be set


### 7. Filtering
1. Navigate to potential matches screen with at least one match.
2. Select incorrect region from filters - Should not display any results.
3. Select correct region from filters - Should display correct results
4. Select correct and incorrect ages - verify results
5. Select incorrect and correct birth gender from filters - verify results
6. Create a combination of filters and verify results.

## Test History

### 15/08/2018 - Aidan Smith

Test 1. Pass

Test 2. Pass

Test 3. Pass

Test 4. Pass

Test 5. Pass

Test 6. Pass