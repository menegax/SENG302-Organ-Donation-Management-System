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
26th March to the 4th of May

#### GUI Donor Medications

Adam Ross' decisions and assumptions for story 18:


To reduce error, for in the case that a medication has been selected in each of the history and the current listViews, it has been decided to reserve deletion of a medication from the history listView only. Otherwise, if not reserve deletion to only one listView, and include both lists, regardless of which medication is the intended medication delete, only the most recently will be deleted. History has been assumed as the more appropriate of the two listViews for deleting from.

If a medication is selected in each of the current and history listViews, and a user selects either a remove or add button unintentionally, then a medication will be swapped from one listView to the other, depending on which button is selected, even if this medication is not the most recently selected between the two listViews. If this is not the intention of the user, then this has been assumed to be determined as user error, and not the fault of the program.

Medication selection is assumed to be possible in each listView simultaneously for the benefit of future stories.

The current option of multiple selection in each listView may not be appropriate for future stories. 