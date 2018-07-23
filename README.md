# SENG302 Project Template
last update 24/07/2018

# Project Structure
 - `src/` Your application source
 - `doc/` User and design documentation
 - `doc/examples/` Demo example files for use with your application
 - `doc/uml/` UML diagrams
 - `doc/manual_tests/` Contains written instructions to perform manual tests
 
# Starting the Application
 
## CLI
 1. Deploy the .jar file(s) using Maven
 2. Open a terminal application and navigate to the directory with the .jar file
 3. Run the command `java -jar <jarfilename> cli` to begin the application.
 
 If you are running the CLI app:
 * Type "quit" at any time into the command line to quit the application
 * Use `-h` for help for any command or sub-command

 
## GUI
 1. Deploy the .jar file(s) using Maven
 2. Run the terminal command `java -jar <pathtojar>` to begin the application
 
## Touchscreen
 1. Deploy the .jar file(s) using Maven
 2. Run the terminal command `java -jar <pathtojar> touch` to begin the application
 
# Requirements
* JDK 1.8
 
# Dependencies
 
 * JUnit
 * JLine
 * Picocli
 * Gson
 * StringUtils
 * Lucene
 * HTTPClient
 * FluentAPI
 * TestFX
 