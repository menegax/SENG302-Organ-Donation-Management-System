# Story - Definition of Done
Before a story can be defined as "Done" the following list must be followed. 
If a story is ready for review, it should be set to "Pending" on Agilefant.
  
Checklist

- `development` has been merged into the branch
- Regression testing done (latest GitLab build passed)
- Story set to "Ready"
- All story tasks set to "Done"
- Ask team members for questions on implementation
- Acceptance criteria reviewed and met
- Code peer reviewed for readability, refactoring, etc.
- All todo's are removed
- JavaDoc added to appropriate methods and attributes
- Unit tests implemented where necessary
- Manual tests documented and performed
- Design decisions documented
- Maven deploy and verify working .jar(s)
- Previous stories that are required to support future implementation are adapted (e.g. Undo/redo should work with this new story (if applicable))
- GUIs include CSS where applicable

If it passes:

- Merge to `development`
- If build succeeds, set story to "Done"
