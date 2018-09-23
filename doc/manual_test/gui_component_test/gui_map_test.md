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
5. Expected: The map should re-populte with the patients in the table. These include patients with the following Nhi's: BRK3714, CGC2343, JLP9988, PXB9300, GLB4745, JBS0871, JAC2680, ETD6078, ADB1975

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
7. Expected: The map should re-populte with the patients in the table. These include patients with the following Nhi's: AKY4149

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
6. Expected : Label & dd align in same row
7. Expected : Fields to be aligned on top of each other
8. Expected : Assign button align on the bottom right

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
6. Expected : The success count of valid organs to be the same as noted in available organs + sum of all patients in available organs
7: Expected : To be able to click "view failed patients"

### Test 17 - Displaying correct popup message
1. Log in as Clinician
2. Go to available organs
3. Ensure that all patients have valid death addresses.
4. Click view on map
5. Expected : The success count of valid organs to be the same as noted in available organs
6: Expected : Should not be any clickable button "view failed patients"


## Test History

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

**ADD MOST RECENT RESULTS TO GITLAB TEST SUITE SUMMARY**
