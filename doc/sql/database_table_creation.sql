DROP TABLE IF EXISTS tblClinicians;
DROP TABLE IF EXISTS tblPatientContact;
DROP TABLE IF EXISTS tblPatientLogs;
DROP TABLE IF EXISTS tblDiseases;
DROP TABLE IF EXISTS tblMedications;
DROP TABLE IF EXISTS tblTransplantWaitList;
DROP TABLE IF EXISTS tblPatientProcedures;
DROP TABLE IF EXISTS tblPatients;

CREATE TABLE tblPatients(
Nhi CHAR(7),
FName VARCHAR(35) NOT NULL,
MName VARCHAR(70),
LName VARCHAR(35) NOT NULL,
Birth DATE NOT NULL,
Created TIMESTAMP NOT NULL,
Modified TIMESTAMP NOT NULL,
Death DATE,
BirthGender CHAR(1), 
PrefGender CHAR(1),
PrefName VARCHAR(35),
Height TINYINT UNSIGNED,
Weight TINYINT UNSIGNED,
BloodType CHAR(3),
DonatingOrgans SET ('liver', 'heart', 'kidney', 'bone', 'bone marrow', 'skin', 'connective tissue', 'cornea', 'pancreas', 'lung', 'middle ear', 'intestine'),
ReceivingOrgans SET ('liver', 'heart', 'kidney', 'bone', 'bone marrow', 'skin', 'connective tissue', 'cornea', 'pancreas', 'lung', 'middle ear', 'intestine'),
PRIMARY KEY(Nhi)
);

CREATE TABLE tblPatientContact(
Patient CHAR(7),
Street1 VARCHAR(100),
Street2 VARCHAR(100),
Suburb VARCHAR(100),
Region ENUM('Northland', 'Auckland', 'Waikato', 'Bay of Plenty', 'Gisborne', 'Hawkes Bay', 'Taranaki', 'Manawatu', 'Wellington', 'Tasman', 'Nelson', 'Marlborough', 'West Coast', 'Canterbury', 'Otago', 'Southland'),
Zip CHAR(4),
HomePhone VARCHAR(9),
WorkPhone VARCHAR(9),
MobilePhone VARCHAR(11),
Email VARCHAR(254),
ECName VARCHAR (70),
ECRelationship VARCHAR(30),
ECHomePhone VARCHAR(9),
ECWorkPhone VARCHAR(9),
ECMobilePhone VARCHAR(11),
ECEmail VARCHAR(254),
PRIMARY KEY(Patient),
FOREIGN KEY(Patient) REFERENCES tblPatients(Nhi)
);

CREATE TABLE tblPatientLogs(
Patient CHAR(7),
Time TIMESTAMP,
Level ENUM('OFF','SEVERE','WARNING','INFO','CONFIG','FINE','FINER','FINEST','ALL') NOT NULL,
Message VARCHAR(100) NOT NULL,
Action VARCHAR(100) NOT NULL,
PRIMARY KEY(Patient, Time),
FOREIGN KEY(Patient) REFERENCES tblPatients(Nhi)
);

CREATE TABLE tblDiseases(
Patient CHAR(7),
Name VARCHAR(50),
DateDiagnosed DATE,
State TINYINT(2) NOT NULL,
PRIMARY KEY(Patient, Name, DateDiagnosed),
FOREIGN KEY(Patient) REFERENCES tblPatients(Nhi)
);

CREATE TABLE tblMedications(
Patient CHAR(7),
Name VARCHAR(50),
PRIMARY KEY(Patient, Name),
FOREIGN KEY(Patient) REFERENCES tblPatients(Nhi)
);

CREATE TABLE tblProcedures(
Patient CHAR(7),
Summary VARCHAR(255),
Description TEXT,
ProDate DATE,
AffectedOrgans Set('liver', 'heart', 'kidney', 'bone', 'bone marrow', 'skin', 'connective tissue', 'cornea', 'pancreas', 'lung', 'middle ear', 'intestine'),
PRIMARY KEY(Patient, Summary, ProDate),
FOREIGN KEY(Patient) REFERENCES tblPatients(Nhi)
);

CREATE TABLE tblTransplantWaitList (
Patient CHAR(7),
RequestDate DATE,
Organ ENUM('liver', 'heart', 'kidney', 'bone', 'bone marrow', 'skin', 'connective tissue', 'cornea', 'pancreas', 'lung', 'middle ear', 'intestine'),
Region ENUM('Northland', 'Auckland', 'Waikato', 'Bay of Plenty', 'Gisborne', 'Hawkes Bay', 'Taranaki', 'Manawatu', 'Wellington', 'Tasman', 'Nelson', 'Marlborough', 'West Coast', 'Canterbury', 'Otago', 'Southland'),
PRIMARY KEY(Patient, RequestDate, Organ),
FOREIGN KEY(Patient) REFERENCES tblPatients(Nhi)
);

CREATE TABLE tblClinicians(
StaffID MEDIUMINT UNSIGNED,
FName VARCHAR(35) NOT NULL,
MName VARCHAR(70),
LName VARCHAR(35) NOT NULL,
Street1 VARCHAR(100),
Street2 VARCHAR(100),
Suburb VARCHAR(100),
Region ENUM('Northland', 'Auckland', 'Waikato', 'Bay of Plenty', 'Gisborne', 'Hawkes Bay', 'Taranaki', 'Manawatu', 'Wellington', 'Tasman', 'Nelson', 'Marlborough', 'West Coast', 'Canterbury', 'Otago', 'Southland'),
Modified TIMESTAMP NOT NULL,
PRIMARY KEY(StaffID)
);
