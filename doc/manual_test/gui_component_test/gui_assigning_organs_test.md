# GUI Assigning Organs Tests

1. Assigning an organ to a receiver
    - Log in as an admin or clinician
    - Navigate to the available organs table
    - Open the potential matches for an available organ and assign it to a potential match
    - Verify that the potential match window closes and the available organ is removed from the available organs table

2. Deregistering an assigned organ (receiver POV)
    - Log in as an admin or clinician
    - Navigate to the profile of a patient who has a donated organ assigned to them
    - Go to the receiving organs tab
    - Attempt to deregister the assigned organ
    - Verify that the warning alert shows up
    
3. Deregistering an assigned organ (donator POV)
    - Log in as an admin or clinician
    - Navigate to the profile of a patient who is donating an organ that has been assigned
    - Go to the donating organs tab
    - Attempt to deregister the assigned organ
    - Verify that the warning alert shows up

4. Check that correct receiver NHI displays
    - Log in as an admin or clinician
    - Navigate to the available organs table
    - Open the potential matches for an available organ and assign it to a potential recipient
    - Navigate to the profile of the donor for the available organ
    - Verifies that the organ now has the receivers NHI next to the organ in the donating organs list
    
5. Check that correct donor NHI displays
    - Log in as an admin or clinician
    - Navigate to the available organs table
    - Open the potential matches for an available organ and assign it to a potential recipient
    - Navigate to the profile of the recipient for the available organ
    - Verifies that the organ now has the donors NHI next to the organ in the receiving organs list
    
6. Check that deregistering an assigned organ affects both profiles
    - Log in as an admin or clinician
    - Navigate to the profile of a patient with either an assigned donation or assigned receiving organ
    - Open either their donation or receiving organ update screens (depending on if the patient is a donor or receiver)
    - Clear the donation/receiving status on the organ that is assigned (the alert should show)
    - Verify that the organ no longer appears in that patients donating or receiving list
    - Verify that there is no longer an assignment for that organ on the other patients profile (i.e. There should be no NHI displayed next to it)  
    
7. Check undoing the deregistration an assigned organ affects both profiles
    - Log in as an admin or clinician
    - Navigate to the profile of a patient with either an assigned donation or assigned receiving organ
    - Open either their donation or receiving organ update screens (depending on if the patient is a donor or receiver)
    - Clear the donation/receiving status on the organ that is assigned (the alert should show)
    - Undo the deregistration
    - Verify that the organ is still assigned by viewing the current patients profile
    - Verify that the organ is still assigned on the other patients profile   
    
8. Opening the assigned patient profile from the receiving or donating list of another patient
    - Log in as an admin or clinician
    - Navigate to the profile of a patient with either an assigned donation or assigned receiving organ
    - Double click on an entry in their donating list or receiving list that has an assignment
    - Verify that the profile of the assigned patient opens    
    
### 21/09/2018 - Kyle Lamb

1. Pass

2. Pass

3. Pass

4. Pass

5. Pass

### 21/09/2018 - Kyle Lamb

6. Pass

7. Pass

## 22/09/2018 - Kyle Lamb

8. Pass