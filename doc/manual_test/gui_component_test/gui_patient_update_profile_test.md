# GUI Patient Update Profile Manual Tests

### Test 1
1. Log in as a patient
2. Go to the update profile tab
3. Edit the fields to valid values:
    * First name
    * Middle names
    * Last name
    * Preferred name
    * Death
    * Birth
    * Street1
    * Street2
    * Suburb
    * Region
    * Zip
    * Weight
    * Height
4. Click "set". If there is a validation error for valid values, the test fails.
5. Go to the profile tab
6. Compare what was entered in the update tab to what is displayed. If there is a mismatch, the test fails. Otherwise it succeeds.

### Test 2
1. Log in as a patient
2. Go to the update profile tab
3. Edit the fields:
* NHI
4. Click "set". If there is a validation error for valid values, the test fails.
5. Go to the profile tab
6. Compare what was entered in the update tab to what is displayed. If there is a mismatch, the test fails. Otherwise it succeeds.

### Test 3
1. Log in as patient
2. Go to the update profile tab
3. Edit any single death detail field (i.e. death city, or death region)
4. Attempt to set the attributes
5. Expected: warning saying it should require the other death details to be set as well

### Test 4
1. Log in as patient
2. Go to the update profile tab
3. Edit each to include invalid characters (i.e. @#$):
    * deathDate
    * deathLocation
    * deathCity
    * deathRegion
4. Click set
5. Expected: all death fields should turn to "invalid" red style
6. Expected: there should be error messages in the status bar detailing the errors
7. Edit each field to be valid
8. Click set
9. Expected: the patient successfully updates

## Test History

### 19/07/2018 - Maree

Test 1: Pass

Test 2: Pass

### 13/08/2018 - Andrew

Test 3: Pass

Test 4: Pass