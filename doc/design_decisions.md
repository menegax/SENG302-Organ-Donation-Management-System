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
within the `run()` method for eachCLI command. This way we arere only unit testing internal methods.
 