# Design Decisions

This document contains a history of agreed-upon design decisions for future reference.

## Sprint 1
23/02 - 09/03

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
In order to satisfy story number ?, the user must be able to search for a particular donor and receive one search result. 
This requires a unique search term to be entered such that duplicates are not returned. 

* unique id for donor? -- firstname, middle name, last name, dob, created datetime concatenated
* 
