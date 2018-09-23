# GUI Administrator Tests

1. Register new patient using GUI logged in as Admin
    - Log in as an admin
    - Click "Register User" button
    - Select "Patient" radio button
    - Enter a NHI, first name, last name, middle name(s) and date of birth
    - Click "Done" button
    - Click "Search Users" button
    - Enter the NHI of the new patient and ensure it is present in the search results.

2. Register new clinician using GUI logged in as Admin
    - Log in as an admin
    - Click "Register User" button
    - Select "Clinician" radio button
    - Enter a first name, last name, middle name(s) and region
    - Click "Done" button
    - Click "Search Users" button
    - Enter the "Staff ID" of the new clinician and ensure it is present in the search results.

3. Register new admin using GUI logged in as Admin
    - Log in as an admin
    - Click "Register User" button
    - Select "Admin" radio button
    - Enter a username, first name, last name, middle name(s) and password
    - Click "Done" button
    - Click "Search Users" button
    - Enter the username of the new admin and ensure it is present in the search results.

4. Delete patient using GUI logged in as Admin
    - Log in as an admin
    - Click "Search Users" button
    - Double click on a patient
    - Click "Delete Profile" button
    - Patient should be removed from search results

5. Delete clinician using GUI logged in as Admin
    - Log in as an admin
    - Click "Search Users" button
    - Double click on a clinician
    - Click "Delete Profile" button
    - Clinician should be removed from search results

6. Delete admin using GUI logged in as Admin
    - Log in as an admin
    - Click "Search User" button
    - Double click on a admin
    - Click "Delete Profile" button
    - Admin should be removed from search results

7. Update patient using GUI logged in as Admin
    - Log in as an admin
    - Click "Search User" button
    - Double click on a patient
    - Click "Manage Profile" button
    - Change any fields
    - Click "Save" button
    - Ensure changes are reflected in profile
    - Close window
    - Ensure changes are reflected in the Search Users window
    - Double click on the patient again in the search
    - Ensure the changes are still present

8. Update clinician using GUI logged in as Admin
    - Log in as an admin
    - Click "Search User" button
    - Double click on a clinician
    - Click "Edit Profile" button
    - Change any fields
    - Click "Save" button
    - Ensure changes are reflected in profile
    - Close window
    - Ensure changes are reflected in the Search Users window
    - Double click on the clinician again in the search
    - Ensure the changes are still present

9. Update admin using GUI logged in as Admin
    - Log in as an admin
    - Click "Search User" button
    - Double click on a admin
    - Click "Edit Profile" button
    - Change any fields
    - Click "Save" button
    - Ensure changes are reflected in profile
    - Close window
    - Ensure changes are reflected in the Search Users window
    - Double click on the admin again in the search
    - Ensure the changes are still present

10. Update admin attributes in GUI
    - Log in as an admin
    - Click "Profile" button
    - Click "Edit Profile" button
    - Change any fields
    - Click "Save" button
    - Ensure changes are reflected on the profile screen
    - Log out
    - Log back in
    - Ensure changes are persisted

## Test History

### 21/07/2018 - Andrew

Test 1: Pass

Test 2: Fail using lastname: M@#$%^

```
Caused by: java.lang.IllegalArgumentException: lastname
	at service.Database.addClinician(Database.java:171)
	at controller.GUIUserRegister.register(GUIUserRegister.java:379)
	... 63 more
WARNING: Couldn't add clinician due to invalid field: last name
```

Test 3: Pass

Test 4: Fail

The patient remained in the search results until changing tabs and changing back

Test 5: Fail

The clinician remained in the search results until changing tabs and changing back

Test 6: Fail

```
Caused by: java.lang.NullPointerException
	at controller.GUIAdministratorProfile.deleteProfile(GUIAdministratorProfile.java:76)
	... 59 more
```

Test 7: Fail

Updating first name worked, but was not reflected in the profile page or the search results.

Test 8: Pass

Test 9: Pass

Test 10: Pass

### 23/07/2018 - Andrew

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

### 31/07/2018 - Andrew

Test 1: Pass

Test 2: Pass

Test 3: Pass

Test 4: Pass

Test 5: Pass

Test 6: Pass

Test 7: Pass

Test 8: Pass

Test 9: Pass

Test 10: Pass / Fail. Once logged out can't log back in as an admin.

### 31/07/2018 - Aidan

Test 3: Pass

Test 6: Pass

Test 9: Pass

Test 10: Pass

### 14/08/2018 - Aidan

Test 1: Pass* User registered (present in table), but search did not yield result

Test 2: Pass

Test 3: Pass

Test 4: Pass

Test 5: Fail - Clinician not deleted null pointer thrown on second clinician profile open 

```
Exception in thread "JavaFX Application Thread" java.lang.NullPointerException
	at org.apache.lucene.index.BufferedUpdates.addTerm(BufferedUpdates.java:232)
	at utility.Searcher.removeIndex(Searcher.java:199)
```

Test 6: Fail - Delete button not visible

Test 7: Partial Pass - Can only edit a newly registered patient, not one loaded from the database 

Test 8: Partial Pass - Updated name not immediately shown on clinician profile

Test 9: Pass (Only tested with newly registered user)

Test 10: Pass

### 23/07/2018 - Maree

Test 1: Pass

Test 2: Pass

Test 3: Pass

Test 4: Fail - stage is closed, and closes application

Test 5: Fail - stage is closed, and closes application

Test 6: Fail - stage is closed, and closes application

Test 7: Pass

Test 8: Pass

Test 9: Pass

Test 10: Pass

**ADD MOST RECENT RESULTS TO GITLAB TEST SUITE SUMMARY**
