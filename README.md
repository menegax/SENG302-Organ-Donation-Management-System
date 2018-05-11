# SENG302 Project Template
last update 03/08/2018

# Project Structure
 - `src/` Your application source
 - `doc/` User and design documentation
 - `doc/examples/` Demo example files for use with your application
 - `doc/uml/` UML diagrams
 - `doc/manual_tests/` Contains written instructions to perform manual tests
 
# Requirements
 
 * JDK 1.8
 
## Starting the Application
 
### CLI
 1. Deploy the .jar file(s) using Maven
 2. Open a terminal application and navigate to the directory with the .jar file
 3. Run the command `java -jar <jarfilename> cli` to begin the application.
 
 If you are running the CLI app:
 * Type "quit" at any time into the command line to quit the application
 * Use `-h` for help for any command or sub-command

#### Available CLI Commands
 
 * Donor
    * Add
    * Donations
    * Remove
    * Update
    * View
 * Import
 * Save
 
### GUI
 1. Deploy the .jar file(s) using Maven
 2. Run the terminal command `java -jar <pathtojar>` to begin the application
 
### Dependencies
 
 * JUnit
 * JLine
 * Picocli
 * Gson
 * StringUtils
 * Lucene
 * HTTPClient
 * FluentAPI
 * TestFX
 