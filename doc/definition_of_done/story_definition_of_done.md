# This document is only for code reviewers to follow
If you are a developer seeking a code review, please follow the `story_definition_of_pending.md`


## Code Reviewer Questionnaire

* Have the developers on the story gone through DoP? Ask them. If not, stop the review. Else continue questionnaire.
* Has the story passed the story DoP?
* It's in the DoP, but it's important: has the story passed the acceptance criteria?
* Are classes named accordingly? (i.e. GUIPatientUpdateProfile not PatientProfileUpdate)
* Are classes placed in the correct package? (i.e. undo/redo in utility)
* How have the new features been tested? Are the tests documented? Is the test class name mirroring a GUI controller class name?
* What are the test coverage percentages?
* How does the code look? Can you read and understand what it does easily?
* Are there "magic numbers" anywhere? Can they be removed or refactored to be more obvious?
* Can SENG301 principles be followed more closely anywhere?
* Do the new features work as expected on both the desktop and touch applications?


## Checklist

* If DoP and DoD passes, approve the merge request and remove old feature branch
* Ensure the new build in `development` passes
* Set story on Agilefant to **Done**