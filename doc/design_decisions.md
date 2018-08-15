# Design Decisions

This document contains a history of agreed-upon design decisions for future reference.

## Sprint 1
23rd of February to the 9th of March

#### Conflict Resolution
If a major conflict arises or a design deciion was made that neither person anticipated, we need to make a call that day to resolve. 

#### Initial UML diagrams
UML diagram drafts (insert images)... 

(insert final UML diagram)

#### App
To create a single entry point which can quickly be changed from launching the CLI (soon to be deprecated) to the launching GUI later. This decoupling facilitates agile development fo CLI and GUI.

#### Global Enums
The decision was made to maintain one large global enumerations class in a separate "utility" package because the usage of such enums by various other classes spanning packages is anticipated.

#### Date Library
JodaTime will be used for dates and times (i.e. DateTime and LocalDate), not the native Java Date class from utils.

#### Patient Unique ID
In order to satisfy story number 43, the user must be able to search for a particular patient and receive one search result. 
This requires a unique search term to be entered such that duplicates are not returned. 

* we have decided to use a patient's IRD number to distinguish one patient from another, whom have colliding names.
* we will check for uniqueness within our application by checking if a patient with the IRD already exists. If it already exists an exception is thrown to tell the user this.
* if there an IRD is entered but there is a collision (i.e another patient has the incorrect IRD) then the incorrect IRD will need to updated before adding the new patient

#### CLI Subcommand Limit
We have decided that the maximum level of subcommands is three i.e. `patient update donations --option`

#### Gson Library
We've decided to use Gson library for parsing json files. This will be used for saving data to .json and importing data from .json.

Moffat has approved the use of this library.

#### Picocli
We have decided to use Picocli library to aid in the creation of our CLI. Picocli is a modern, annotation-based external library which auto-populates help commands, usage messages, and other common features expected in a command line interface.
Using this library and its abstraction features supports code maintainability and readability which are crucial during such rapid team changes throughout SENG302.

Moffat has approved the use of this library.

#### Testing
Because we are using external libraries such as Picocli and Gson, we can assume the external libraries are sufficiently tested and therefore we do not need to test a lot of codebase. This is because

We are manually testing saving and importing functionality from the Database class (for now). This is because writing and reading is used from the external Gson library, and any testing on writing would require using the reading functionality of the same library, and vice versa.

After a discussion with Marina (senior tutor), we've decided that instead of testing the CLI by running its `main()` and 
providing raw user input, we will instead assume the framework is fully functional, and test any internal method calls
within the `run()` method for eachCLI command. This way we are only unit testing internal methods.
 
## Sprint 2
9th to the 23rd of March

#### JLine
We decided to go with the JLine library for story 9 (command line history). We decided to use this over the other option of JNativeHook as 
JLine provided a simple implementation of the history and solely required us to set up the terminal using JLine's terminal builder and Line Reader, whereas
JNativeHook captured keypress events on a global level (Even if the terminal didn't have focus). This also would have then left us with the tasks of having to store the commands in a list as well as 
creating functionality in which the keypress events would 'navigate' through that list and fill out the terminal command line. 

#### IRD -> NHI Transition
We decided to change form using IRD as the identifier of a donor to the NHI number. The IRD number is thus no longer used. The reason behind this is that it makes
more sense in the context of a health app, and users may feel more comfortable provided a NHI number instead of an IRD number due to NHI's association with health rather than tax and finance.

#### Logging and System Print Messages
We've decided to use a logger to log all actions and/or events during the application session. 
The logger has associated handlers and one of those outputs appropriate messages to console. 
Therefore from this point on there should never be a System.out.println call in our entire codebase.
Refer to the appropriate logger to log INFO, WARNING, etc and its respective filter for end-user output to console.

#### GUI Back Button
Each GUI screen other than the home screen should have a `back button` styled as a back button in the CSS class placed in the top left corner with text `〱back`

#### GUI Method Names
goToScreen() can be used as a method name only if the only code inside the method simply activates a new screen.

See the GUIDonorRegister class for examples of goToLogin() and register()

#### User Action History
All user actions require an NHI to be logged against the action and the corresponding result. Therefore attempting but failing to log in would not be logged as there is no NHI to use. Registering a new donor would not be logged either.

## Sprint 3
26th March to the 3rd of May

#### GUI Donor Medications

To reduce error, for in the case that a medication has been selected in each of the history and the current listViews, it has been decided to reserve deletion of a medication from the history listView, only. Otherwise, if not reserve deletion to only one listView, and include both listViews, being that there is currently no found appropriate method to determine which medication is the most recently selected between listViews, and that deciding deletion between listViews can only currently be done by determining if one listView does not have a medication selected before deleting a selected medication from another listView, then a selected medication other than the most previously selected medication could be deleted instead if the most recently selected medication is in the listView that has a selected medication deleted from it only after the other listView is determined to not have any selected medication for deleting, when in the case that each listView has a medication selected.

If a medication is selected in each of the current and history listViews, and a user selects either a remove or add button unintentionally, then a medication will be swapped from one listView to the other, depending on which button is selected, even if this medication is not the most recently selected between the two listViews. If this is not the intention of the user, then this has been assumed to be determined as user error, and not the fault of the program.

Medication selection is assumed to be possible in each listView simultaneously for the benefit of future stories.

The current option of multiple selection in each listView may not be appropriate for future stories. 


####Donor Contact Details
Contact Details for a Donor are updated in a separate update method. This is because as contact details are implemented solely in the GUI application, and so will only need handling there.

For viewing a Donor's contact details, a new window is shown for both editing and viewing contact details. This is to reduce clutter in the profile view screen.

#### Interface classes
All interface classes must be prefixed with "I". i.e. IEnumberable. This is to be able to distinguish classes from interfaces easily when the repository gets larger.

#### Undo
We've decided to have the action listeners linked to the state history for each control.  
This allows undo to be generalised and applied much easier.  
It also allows for separate actions to listen to for store(state change) and undo(Ctrl Z).  
This therefore lead to the decision to only undo one letter at a time in all scenarios as this is how it was being stored.  
We store and undo all control widgets at once as this circumvents the need to identify what control changed last, while also keeping the same user experience.   
We decided to implement an IUndoRedo interface which all controls used will have an applicable StateHistory which represents it.  
This interface shows exactly what is expected of the StateHistorys and is also used for iteration purposes in the StatesHistoryScreen object.

#### Redo
We decided to implement redo as almost the opposite of undo to maintain simplicity for both the user and the development team.  
This meant that an action could not be undone after another action was performed.  
This allows the user to more easily keep track of what changes they have made and what states they can switch between.  
In addition we decided to bind redo to Ctrl + Y. This is because Ctrl + Y is the industry standard undo for non-technical applications (such as word).  
This is how we aim to target our application, as we do not see most of our users having software development or similar backgrounds.

#### Name Search For Patients
We decided to use an external library, Lucene, to do our name searching of patients. This library allows us to not just do a simple exact name search but do a fuzzy search of the names.
This term "fuzzy search" means that some of the character can be different from what is in the name and it will still be a result. In order to tell which results are "better" a "score" is included with the individual results, which symbolizes how close to the entered search the names are. The closer the name to the search, the higher the score.
We thought this would be very useful when searching for patients, as the clinician may not know the exact spelling of a particular patient but knows roughly how it is spelt. Or in a much simpler case, the clincian may simply have a miss key press. 
However we do not want every patient to be matched to any search, so we set the max number of the character difference between the search and the patient's name to two.


## Sprint 4

#### Diagnosis Validation
A diagnosis must have a name between 3 and 50 characters long, as this covers the length of the full names of most diseases and conditions. A diagnosis can not be made in the future or before the patient was born.

#### Diagnosis Adding
A diagnosis can not be added to the current diagnoses list if the patient has a diagnosis of the same name and the same diagnosis date.

#### Setting past and current diagnoses lists
Diagnoses lists for a patient are set entirely after all changes have been made before saving. This is to keep tracking changes made to a minimum to reduce operation complexity, and thus making the saving procedure quicker to execute.

#### User Actions Logging
User actions are now an attribute within the patient object. This way the patient is in charge of its own logs. However, to log a record to the user 
history, the userActionHistory class needs to be used. This way the UserActionHistory class is responsible for all user action logs regardless of 
the patient or where in the app the log was created. However, the logger class needs some sort of access to the patient's logs, so we created a getter
. Unfortunately, the getter must return a modifiable list of records. This opens up the possibility of other classes getting a list of modifiable 
records and modifying them inappropriately. This is the trade off between for having a separate logger class that implements the Java API logger class.

#### System Logging
Since a Java logger had already been implemented it was very easy to implement an internal logging solution. This systemLogger is very similar to the 
userActions log. It will be used for only developer debugging purposes. There should never be a System.out.println() call ever again; even temporarily
-- it should be a systemLog.log() and when done debugging the statement should be left there for future use. Please add logs as you go and never delete
 logs (unless they're incorrectly written, of course).  
 
#### Clinician Logging
To keep consistency, the logging of medication actions and other clinician actions is now solely using the UserHistory logger. This allowed
us to log actions that occur within the medications page (and other pages while a clinician is logged in) in a way that all actions, whether saved or unsaved,
are visible within the new history table for clinicians. If a clinician alters medications for a patient, a ClinicianActionRecord is created that stores
the standing log information, plus the NHI number of the 'target' patient that the changes are being made to. Logs (+ medication changes) will persist between 
sessions after the user saves changes using the standard save button on the home screen.

#### Undo/Redo
We decided to reload the fxml on every undo and redo as it did not create significant processing time.  
In addition it removed the necessity for the application to know whether it was on the screen it needed to undo or not.
We created abstract classes UndoableStage and UndoableController to enable future implementations with ease as most methods and attributes would be inherited.

#### Deregistering an Organ due to Cure
When you deregister an organ due to a disease or collection of diseases being cured and mark a collection of selected diseases to cured in the dropdown, we considered how
the program should handle chronic diseases. We decided that it makes most sense to be able to set chronic diseases to cured in this scenario as it is not
very user friendly to have to go back to the disease screen to 'complete' the cure of the disease

#### Storing (hashed) passwords and salt in the Administrator object
Due to the database story being currently worked on, we have decided to store the hashed password and salt for the administrator accounts
stored within the administrator object rather than only storing it in the database. This is so we can test and progress the administrator story without
waiting for the database story to get fully implemented. When the database story is complete, we easily switch it so the password and salt are no longer
stored within the objects if required/decided.

#### Keyboard Shortcuts
MenuBar has the ability to set and bind keyboard shortcuts to a stage. Therefore we will use MenuBars to set the keyboard shortcuts. 
This means there should not be other action listeners for global or stage-level shortcuts. If it has a keyboard shortcut, it belongs as an item in the menubar.
  
 ## Sprint 5
 
 #### GUI
 We have decided that tabs will not call methods to navigate between themselves on the home screen.  
 This will remove the necessity to find the tabs of the home screen that a tab is on.
 
 #### GUI Testing
 TestFX has been essentially component testing the application instead of performing small-scale unit tests. We debated making each GUI controller 
 runnable for the purpose of unit testing single controllers, but realized the unit tests would have minimal meaning without the backend supporting 
 it. Therefore we will be using TestFX controller tests more sparingly and for larger-scale component testing for the entire application.
 
 #### GUI Testing (again)
 TestFX has been a huge drain on effort and time from the developers. We've decided to entirely remove all TestFX tests in favor of the more 
 human-intensive manual testing method. In truth, we seem to believe manual testing will take less time and effort than TestFX at this point. This 
 will and has led to 0% test coverage on the `controller` package. We expect that. With comprehensive manual testing in addition to smoke testing we 
 will effectively have 100% test coverage through manual testing.
 
 #### Undo/Redo
 On the register users screen as an administrator, fields will be cleared if and only if the radio buttons are clicked by the user.  
 If different radio buttons are selected through undo/redo, the inputs will instead persist.  
 Undoing/Redoing "actions" will consume the whole undo/redo event. The previous key press, etc. will not be undone.  
 This is because in procedures/diagnosis the user will want to undo actions without reverting to a previous screen (the case otherwise)
 
 ####Touch Screen Controls
 We decided to use the built in touch commands for zooming and rotating functions, as they are built in events and handlers
 that are simple to grasp and intuitive control schemes.
 
 ####Multiple User touch interface
 We decided we would use the external library TUIOFX to implement a multi-user touch interface. This will require the application to be reworked so only one stage is present in the application, with multiple panes as scenes.
 
 ####Base Touchscreen ACs
 In the original acceptance criteria for base touchscreen functionality, there was a condition that the on-screen keyboard would be rotatable. In this sprint, we found that creating a rotatable keyboard is a story by itself, and so with permission from the PO, we kept the default keyboard that is not rotatable.
 
 In addition, the AC about multiple window touch functionality was not met in this sprint due to the amount of time it would take to implement simultaneous interactions. This would involve changing the setup of our application to be a single stage with multiple panes acting as "windows" to allow for the TUIOFX library to function for multi-user touch interfaces.

 #### Status bar updates
 We decided to use the built in logger we are using to set the status bar text when a log is added. This means that we can rely
 on our existing log additions instead of having to set the status bar text in each controller class. The status bar is updated by 
 using setStatus within the observable StatusObservable, which notifies each of its observers.

#### Parsing CSV data
When we parse CSV data to import into the database, we decided to only insert a patient if all of the fields are valid ascii, else the
record is discarded. This is because it makes it easier to manage the data without having to perform validation checks or modifications
when reading/writing to the database.

### Sprint 6

#### Screen control
When re-engineering screen control we decided to encapsulate the stage and scene within screen control.
This means whenever we need to set on close or similar method only one method can be called at a time as IWindowObserver doesn't differentiate based on the window that was closed

#### Potential matches
When ordering matches, we decided to use region for ordering to limit geocode use.
To do this, we implemented a priority based on adjacent regions such that the distance between regions is based on how many regions one has to travel through between them

#### Map Marker Seperation
We decided to place a very small random shift in marker positions so that when addresses that do not have valid roads but have a valid city are geocoded, they do not lead to duplicate latlongs. 
This means that we no longer have markers that are grouped on top of each other in the centre of that city (and thus being hard to find all markers/open markers behind them)

#### Expiring Organs
We have done more research to check the expiry times for organs such as Intestines, Bone Marrow, Connective tissue  and 
Middle ear. We have assumed that middle ear will have an upper bound of 24 hrs (same as connective tissue) We used the following resources:
http://www.nedonation.org/donation-guide/organ/acceptable-ischemic-times
https://www.researchgate.net/post/How_should_bone_marrow_be_stored_and_how_long_can_it_be_exposed_to_a_room_temperature_environment
https://www.ncbi.nlm.nih.gov/pmc/articles/PMC5116036/

We have also decided to use values of 50% time elapsed to change to yellow/orange and 80% time elapsed to change red on default. (i.e no lower - upper bound)