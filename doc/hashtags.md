# Agilefant Hashtags

* `#implement`: You wrote code that implemented some functionality.

* `#document`: You worked on documentation, e.g. User guide, code documentation, etc.

* `#test`: You wrote unit or integration tests.

* `#testmanual`: You carried out structured manual testing of the product. Must reference/link to manual test on wiki.

* `#fix`: You fixed a bug.

* `#chore`: You worked on ancillary tasks e.g. configuring your CI environment, fixing a merge conflict, improving your build system, etc.

* `#refactor`: You carried out a refactoring as a result of a code-test-refactor process.

* `#pair[usercode1, usercode2]`: If you pair programmed use this tag. No more than two usercodes.

* `#commits[commit-hash. . . ]`: A comma separated list of hashes for Git commits produced during the logged session.]

#### Example

```$xslt
Implemented filtering for backlog view.

- User can enter text into a textfield in the backlog view. 
- Backlog is filtered using simple string matching. 
- Items in the backlog match the filter if any of their fields contain a word entered in the filter box.

#implement #test #pair[acu27, mmm77] #commits[dgh8239k]

```

# Git Hashtags

* `#pair[usercode1, usercode2]`: See definition above. 

* `#story[ref_number]`: Identifies which story a commit relates to.

  A story’s reference ID can be found in the Agilefant story info pop-up. This is not the story number from the backlog.

#### Example

```
Implement backlog search

- Enables full text search of the backlog using Lucence. 
- Other solutions were looked at like Solr and Elasticsearch. In this case it was easier to use Lucence directly. 
- Added unit and integration tests that give basic test coverage on the happy path. 
- Took more time than expected because the creation and update of various aspects of the backlog had to be extracted to a common interface so it could be easily fed into the indexing pipeline.

#story[42] #pair[mmm71, acu27]
```
