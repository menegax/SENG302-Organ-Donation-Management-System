# This document is only for code reviewers to follow
If you are a developer seeking a code review, please follow the `story_definition_of_pending.md`


## Code Reviewer Questionnaire

* Have the developers on the story gone through DoD? Ask them. If not, stop the review. Else continue questionnaire.
* Has the story passed the story DoD?
* It's in the DoP, but it's important: has the story passed the acceptance criteria?
* Are classes named accordingly? (i.e. GUIPatientUpdateProfile not PatientProfileUpdate)
* Are classes placed in the correct package? (i.e. undo/redo in utility)
* How has the new features been tested? Are the tests documented? Is the test class name mirroring a GUI controller class name?
* How does the code look? Can you read and understand what it does easily?
* Can SENG301 principles be followed more closely anywhere?


## Checklist

* If DoD successful, approve merge request and remove old feature branch
* Ensure the new build in `development` passes
* Set story on Agilefant to **Done**