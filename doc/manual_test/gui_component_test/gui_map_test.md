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

## Test History

### 15/8/18 - Andrew

Test 1: Pass

Test 2: Pass

Test 3: Failed expectation #3

Test 4: Pass

Test 5: Pass

Test 6: Pass

### 15/8/18 - Kyle

Test 3: Pass