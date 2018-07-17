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
