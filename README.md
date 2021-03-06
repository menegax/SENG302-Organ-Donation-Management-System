# SENG302 Project Template
last update 27/09/2018

# Project Structure
 - `src/` Your application source
 - `resources/` Contains CSS, html, and other data required by the source code
 - `doc/` User and design documentation
 - `doc/import_data/` Demo example files for use with your application
 - `doc/manual_test/` Contains written instructions to perform manual tests
 
# Starting the Application

### Requirements
* JDK 1.8.0_161
* Internet connection
* Windows 10 or above
* Sufficiently powerful CPU and GPU for 4K resolution if using touch app
* If importing data, our own custom data sets are required (not the course provided data sets) as described above

 
## CLI
 1. Deploy the .jar file(s) using Maven
 2. Run the command `java -jar <pathtojar> cli` to begin the application.
 
 If you are running the CLI app:
 * Type "quit" at any time into the command line to quit the application
 * Use `-h` for help for any command or sub-command

## GUI
 1. Deploy the .jar file(s) using Maven
 2. Run the terminal command `java -jar <pathtojar>` to begin the application
 
## Touch
 1. Deploy the .jar file(s) using Maven
 2. Run the terminal command `java -jar <pathtojar> touch` to begin the touch application
 
# Default Credentials

* Patient with username `abc1238`
* Clinician with ID `0`
* Admin with username `admin` and password `password` 
 
# Dependencies
 
 * JUnit
 * JLine
 * Picocli
 * Gson
 * StringUtils
 * Lucene
 * HTTPClient
 * FluentAPI
 * Sonar
 * ControlsFX
 * SimpleCSV
 * Google Maps API
