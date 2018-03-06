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

#### CLI Testing
After a discussion with Marina (senior tutor), we've decided that instead of testing the CLI by running its `main()` and 
providing raw user input, we will instead assume the framework is fully functional, and instead test any methods called 
by the `run()` method within a particular CLI command. This way we're only unit testing internal methods.