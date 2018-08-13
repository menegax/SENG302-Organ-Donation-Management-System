# Import Test

# Perform clean import on clean db
1. run sql script to clean db (location is in resources/sql)
2. open application and log in as admin
3. import large data set
4. check that patient data is visible in the search tabs (admin and clinician)

# Perform import on existing data in db
1. Ensure that data has existing data (i.e. add a patient)
2. Import same csv again
3. Log out and close application
4. Log in as clinician and ensure the newly created patient is still in the application
along with the new ones.

##Test History
### 13/08/2018 - Hayden Taylor and Maree Palmer
Perform clean import on clean db - pass
Perform import on existing data in db - pass