# Story - Definition of Pending
Before a story can be defined as "Pending" (for code review )the following list must be followed. 
If a story is ready for review, it should be set to "Pending" on Agilefant.
  
## Checklist

##### Implementation
- All story tasks set to **Done** or **Pending**
- **Acceptance criteria** reviewed and met
- **Persistent features** (see below) implemented where necessary
- `development` has been merged into the branch
- Regression testing done (latest GitLab build passed)

##### Documentation
- All **todo**'s are removed
- **JavaDoc** added to appropriate methods and attributes
- **Design decisions** documented
- A demo has been created and posted for the story, if applicable

##### Testing
- **Unit tests** implemented where necessary
- **Manual tests** documented and performed on both the desktop and touch application

##### Chores
- Story set to **Pending**
- Merge request has the **"WIP: "** prefix removed

## Persistent Features
* Undo/redo
* User action history
