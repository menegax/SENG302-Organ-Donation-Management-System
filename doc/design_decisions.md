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

#### Donor Unique ID
In order to satisfy story number 43, the user must be able to search for a particular donor and receive one search result. 
This requires a unique search term to be entered such that duplicates are not returned. 

* we have decided to use a donor's IRD number to distinguish one donor from another, whom have colliding names.
* we will check for uniqueness within our application by checking if a donor with the IRD already exists. If it already exists an exception is thrown to tell the user this.
* if there an IRD is entered but there is a collision (i.e another donor has the incorrect IRD) then the incorrect IRD will need to updated before adding the new donor

#### CLI Subcommand Limit
We have decided that the maximum level of subcommands is three i.e. `donor update donations --option`

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
26th of March to the 3rd of May

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


##Sprint 4

####Diagnosis Validation
A diagnosis must have a name between 3 and 50 characters long, as this covers the length of the full names of most diseases and conditions. A diagnosis can not be made in the future or before the patient was born.

####Diagnosis Adding
A diagnosis can not be added to the current diagnoses list if the patient has a diagnosis of the same name and the same diagnosis date.

####Setting past and current diagnoses lists
Diagnoses lists for a patient are set entirely after all changes have been made before saving. This is to keep tracking changes made to a minimum to reduce operation complexity, and thus making the saving procedure quicker to execute.