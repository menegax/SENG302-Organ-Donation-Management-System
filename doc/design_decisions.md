# Design Decisions

This document contains a history of agreed-upon design decisions for future reference.

## Sprint 1
23/02 - 09/03

#### Initial UML diagrams
UML diagram drafts (insert images)... 

(insert final UML diagram)

#### App
separated out app from main because...

#### Global Enums
The decision was made to maintain one large global enumerations class in a separate "utility" package because the usage of such enums by various other classes spanning packages is anticipated.

#### String Resources
central string resources because...

#### Resource Manager
ResourceManager because...

#### Donor Unique ID
In order to satisfy story number ?, the user must be able to search for a particular donor and receive one search result. 
This requires a unique search term to be entered such that duplicates are not returned. 

* unique id for donor?

#### Date Library
jodatime / java util date

