# Undo Redo Manual Tests

### Test each individual control

##### Widgets

For each in:
1. CheckBox(Patient Update Donations)
2. ChoiceBox(Patient Profile Edit)
3. DatePicker(Patient Profile Edit)
4. RadioButton (Patient Profile Edit)
5. TextField (Patient Profile Edit)


1. Navigate to a screen with that control
2. Make a change in that control
3. Press undo (Ctrl + Z)  
-> Control should have changed state back to previous state
4. Press redo (Ctrl + Y)  
-> Control should have changed back to changed state

##### Actions

For every screen where an action - local changes (add, delete, edit) can be performed

If screen has a set button:
1. Make multiple different edits in different controls
2. Press the set button
3. Press undo (Ctrl + Z)  
-> Status bar should say local change undone (will disappear)
-> No controls should have changed
4. Switch to another tab
5. Switch back
-> All edited controls should be at their original values

Repeat steps 1-3
4. Press redo (Ctrl + Y)  
-> Status bar should say local change redone (will disappear)
4. Switch to another tab
5. Switch back
-> All edited controls should be at their edited values

If screen does not have a set button:
For each action possible (add, delete, edit)
1. Perform action
2. Press undo (Ctrl + z)
-> Action should be visibly undone
3. Press redo (Ctrl + Y)
-> Action should be visibly redone

### Test only undo on current stage

##### Widgets
1. Login as a clinician
2. Type a letter in the clinician's edit profile
3. Open a Patient profile from search patients
4. Type a letter in the patient's edit profile
5. Press undo twice when on the clinician's profile  
-> Clinician letter should be undone  
-> Patient letter should not be undone  
6. Press redo twice when on the clinician's profile  
-> Clinician letter should be redone  
-> Patient letter should not be redone  

Repeat 1-4.
5. Press undo twice when on patients profile  
-> Patient letter should be undone  
-> Clinician letter should not be undone
6. Press redo twice when on the patient's profile  
-> Patient letter should be redone  
-> Clinician letter should not be redone

##### Actions
1. Login as a clinician
2. Set a change in clinician update profile
3. Open a Patient profile from search patients
4. Set a change in patient update profile
5. Press undo twice when on the clinician's profile
6. Switch tabs on clinician profile  
-> Clinician changes should be completely undone
7. Switch tabs on patient profile  
-> Patient changes should not be undone  

Repeat 1-4.
5. Press undo twice when on the patient's profile
6. Switch tabs on patient profile  
-> Patient changes should be completely undone
7. Switch tabs on clinician profile  
-> Clinician changes should not be undone 

### Test undo through multiple screens

##### Widgets
1. Login as a patient
2. Make a change in the patient's profile
3. Make a change in the patient's donation's
4. Change to another screen
5. Undo repeatedly until no change is observed  
-> The screen should have navigated to the donations page then the profile page undoing one edit at a time  
6. Redo repeatedly until no change is observed  
-> The screen should have navigated to the profile page then to the donations undoing one edit at a time  

##### Actions

1. Login as a clinician
2. View a patient through search users
2. Make a change in the patient's procedures
3. Make a change in the patient's medication's
4. Change to another screen
5. Undo repeatedly until no change is observed  
-> The screen should have navigated to the medications page then the procedures page undoing one action at a time  
6. Redo repeatedly until no change is observed  
-> The screen should have navigated to the procedures page then to the medications undoing one action at a time

### Test History

#### 14/08/2018 Aidan

Test each individual control: Pass

Test only undo on current stage: Pass

Test undo through multiple screens: Pass