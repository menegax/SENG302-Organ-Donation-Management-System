# GUI Map Test


### Test 1 - Opening Default Clinician

1. Log in as a clinician
2. Expected: the map is shown

### Test 2 - Opening Default Admin

1. Log in as an admin
2. Expected: the map is not shown

### Test 3 - Opening Map Admin

1. Log in as an admin
2. Use the menu bar to select "Open Map"
3. Expected: the map is opened
4. Use the menu bar to select "Open Map" again
5. Expected: the map is not open a second time
6. Close the map
7. Use the menu bar to select "Open Map"
8. Expected: the map is opened

### Test 4 - Opening Map Clinician

1. Log in as an clinician
2. Use the menu bar to select "Open Map"
3. Expected: the map is not opened a second time
4. Close the map
5. Use the menu bar to select "Open Map"
6. Expected: the map is opened

### Test 5 - Tap to View Marker

1. Log in as a clinician
2. Go to the map
3. Tap on a marker
4. Expected: a small info window displays with basic info pertaining ot the marker

### Test 6 - Tap to View, then Open Profile

1. Log in as a clinician
2. Go to the map
3. Tap on a marker
4. Tap on open profile
5. Expected: The patient profile should open

### Test 7 - 'View on map' button clinician search patients

1. Log in as a clinician
2. Go to search patients
3. Filter search to search for 26 year old males in Wellington
3. Select 'View on map' button
4. Select 'OK' on the popup that opens up
5. Expected: The map should re-populate with the patients in the table.

### Test 8 - 'View on map' button clinician transplant waiting list

1. Log in as a clinician
2. Go to transplant waiting list
3. Select 'View on map' button
4. Select 'OK' on the popup that opens up
5. Expected: The map should re-populte with the patients in the table. These include patients with the following Nhi's: AKY4149, AAJ3628

### Test 9 - 'View on map' button clinician available organs

1. Log in as a clinician
2. Go to available organs
3. Select 'View on map' button
4. Select 'OK' on the popup that opens up
5. Expected: The map should re-populate with the patients in the table. These include patients with the following Nhi's: AAB2072
6. Check to make sure that the map only shows at most one marker for each patient that is passed.

### Test 10 - 'View on map' button clinician potential matches

1. Log in as a clinician
2. Go to available organs
3. Select an organ
4. select 'View potential matches'
5. Select 'View on map' button
6. Select 'OK' on the popup that opens up
7. Expected: The map should re-populate with the potential matches in the table and the organ donor.

### Test 11 - 'View on map' button administrator search patients

1. Log in as an admin
2. Go to search patients
3. In the search box enter 'lanny'
3. Select 'View on map' button
4. Select 'OK' on the popup that opens up
5. Select 'View on map' button once again
6. Select 'OK' on the popup that opens up
6. Expected: The map should be populated with the patients in the table. These include patients with the following Nhi's: ANM8474, CTH2080, DMR2101, EQV5740, FUZ3183, KJD8772


### Test 12 - Clicking on Dead patients marker -> profile

1. Log in as Clinician
2. Go to available organs
3. Click view on map
4. Click a marker
5. Click the header containing the patients NHI
6. Expected : Correct Patient Profile to be shown

### Test 13 - Clicking on Dead patients marker - info

1. Log in as Clinician
2. Go to available organs
3. Click view on map
4. Click a marker
5. Expected : NHI, Name, Address, Blood Group, Age to be correct (validate this again patient profile)
6. Expected : Label & dropdown align in same row
7. Expected : Fields to be aligned on top of each other
8. Expected : Assign button align on the bottom left

### Test 14 - Clicking on Dead patients marker - assign organs dropdown

1. Log in as Clinician
2. Go to available organs
3. Click view on map
4. Click a marker
5. Click the assign organs dropdown
6. Expected : Correct organs to be shown (validate against patient profile)


### Test 15 - Displaying correct information in information modal
1. Log in as Clinician
2. Go to available organs
3. Edit 2 patients so that their death addresses are garbage. (if no organs in available organs add some)
4. Click view on map.
5. Wait for info popup and click view patients
6. Expected : at least 2 patients that had their addresses changed to garbage 

### Test 16 - Displaying correct success count in popup message
1. Log in as Clinician
2. Go to available organs
3. Count the number of organs in this table (with valid death addresses).
4. Change one of these addresses to be invalid 
5. Click view on map
6. Expected : The success count of valid organs to be the same as noted in available organs, sum of all patients in available organs
7: Expected : To be able to click "view failed patients"

### Test 17 - Displaying correct popup message
1. Log in as Clinician
2. Go to available organs
3. Ensure that all patients have valid death addresses.
4. Click view on map
5. Expected : The success count of valid organs to be the same as noted in available organs
6: Expected : Should not be any clickable button "view failed patients"

### Test 18 - Marker Colors for Alive/Dead Patients
1. Log in as a clinician
2. (Given that there are patients in the database) Go to Search Patient and click view on map
3. Expected: markers are colored differently depending on if the patient is alive or dead


### Test 19 - Circles are created to deceased patients with organs that are not expired yet.

1. Log in as Clinician
2. Select `Available organs` tab
3. Select `View on Map` button.
4. Select a patient marker.
5. EXPECTED: If the organ is still alive (most likely), a circle should appear.

### Test 20 - Circles are colored in the correct color according to the proportion of time the organ has remaining before expiry.

1. Log in as Clinician
2. Select `Available organs` tab
3. Select `View on Map` button.
4. Select a patient marker.
5. EXPECTED: Organ circle is colored green if it has a lot of time remaining, orange if it has moderate time remaining and red if it has very little time remaining.

### Test 21 - Circles are shrinking in size (as long as the distance has not maxed out).

1. Log in as Clinician.
2. Select `Available organs` tab.
3. Select `View on Map` button.
4. Select a patient marker.
5. Select an organ button (avoid bone, go for 'liver or pancreas).
6. Ensure that the organ is still alive.
7. EXPECTED: If you zoom in very close to the perimeter of the circle, you can see the circle shrinking every second

### Test 22 - Circles are maxed out at the length of NZ

1. Log in as Clinician.
2. Select `Available organs` tab.
3. Select `View on Map` button.
4. Select a patient marker with a bone available either at the top or bottom of NZ.
5. Select the `Bone` button.
6. EXPECTED: The green circle will cover the whole of NZ and no more at the opposite edge.

### Test 23 - Assign organ
1. View available organs on map
2. Select a donor that has potential matches shown in the potential matches screen
3. Select the appropriate organ from the dropdown
4. Select view potential matches
5. EXPECTED: A dialog box should pop-up informing the user how many matches were found
6. EXPECTED: The same potential matches should be visible on the map and in the potential matches table
7. EXPECTED: The map should also show a marker for the donor
8. Click on a potential receiver
9. Click on assign 'organ'
10. Click on assign on the modal
11. EXPECTED: The assignment should be visible in both patient's profiles
12. EXPECTED: The map should reload with the most recently viewed set of patients (available organs)

### Test 24 - Cancel assignment of organ 
1. View available organs on map
2. Select a donor that has potential matches shown in the potential matches screen
3. Select the appropriate organ from the dropdown
4. Select view potential matches
5. Click on the cancel assignment button
6. EXPECTED: The map should reload with the most recently viewed set of patients (available organs)

### Test 25 - Attempt to assign organ with no matches
1. View available organs on map
2. Select a donor that has no potential matches shown in the potential matches screen
3. Select the appropriate organ from the dropdown
4. Select view potential matches 
5. EXPECTED: A pop-up dialog should be displayed that no potential matches were found
6. EXPECTED: The map should show just the donor

## Test History

### 24/09/18 - Joshua

Test 19: PASS

Test 20: PASS

Test 21: PASS

Test 22: PASS

### 09/09/18 - Joshua

Test 7: PASS

Test 8: PASS

Test 9: PASS

Test 10: PASS

Test 11: PASS

### 15/8/18 - Andrew

Test 1: Pass

Test 2: Pass

Test 3: Failed expectation #3

Test 4: Pass

Test 5: Pass

Test 6: Pass

### 15/8/18 - Kyle

Test 3: Pass

### 22/09/18 - Hayden

Test 12: Pass

Test 13: Pass

Test 14: Pass

### 23/09/18 - Hayden

Test 1: Pass
Test 2: Pass
Test 3: Pass
Test 4: Pass - after closing and opening the map default patients arent added
Test 5: Pass
Test 6: Pass
Test 7: Pass
Test 8: Fail - no markers shown
Test 9: Pass
Test 10: Pass
Test 11: Pass
Test 12: Pass
Test 13: Pass
Test 14: Pass
Test 15: Pass - may need to add different table for when there are no errors
Test 16: Pass
Test 17: Pass

### 23/09/18 - Andrew

Test 18: Pass

### 24/09/18 - Kyle & Hayden

Test 1: Pass
Test 2: Pass
Test 3: Pass
Test 4: Pass
Test 5: Pass
Test 6: Pass
Test 7: Pass
Test 8: Pass
Test 9: Pass
Test 10: Pass
Test 11: Pass
Test 12: Pass
Test 13: Pass
Test 14: Pass
Test 15: Pass
Test 16: Pass
Test 17: Pass 
Test 18: Pass

### 25/09/18 - Kyle & Hayden

Test 1: Pass  
Test 2: Pass  
Test 3: Pass  
Test 4: Pass  
Test 5: Pass  
Test 6: Pass  
Test 7: Pass  
Test 8: Pass  
Test 9: Pass  
Test 10: Pass  
Test 11: Pass  
Test 12: Pass  
Test 13: Pass  
Test 14: Pass    
Test 15: Pass  
Test 16: Pass  
Test 17: Pass  
Test 18: Pass  
Test 19: Pass  
Test 20: Pass  
Test 21: Pass  
Test 22: Pass  

### 27/09/18 - Aidan Smith

Test 23: Pass  
Test 24: Pass  
Test 25: Pass  

**ADD MOST RECENT RESULTS TO GITLAB TEST SUITE SUMMARY**
