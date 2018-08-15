drop table if exists tblProcedures;
drop table if exists tblDiseases;
drop table if exists tblMedications;
drop table if exists tblPatientContact;
drop table if exists tblPatientLogs;
drop table if exists tblAdminLogs;
drop table if exists tblClinicianLogs;
drop table if exists tblTransplantWaitList;
drop table if exists tblClinicians;
drop table if exists tblAdmins;
drop table if exists tblPatients;

CREATE TABLE `tblPatients` (
  `Nhi` char(7) NOT NULL DEFAULT '',
  `FName` varchar(35) NOT NULL,
  `MName` varchar(70) DEFAULT NULL,
  `LName` varchar(35) NOT NULL,
  `Birth` date NOT NULL,
  `Created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Death` timestamp NULL DEFAULT NULL,
  `DeathCity` varchar(75) DEFAULT NULL,
  `DeathRegion` enum('Northland','Auckland','Waikato','Bay of Plenty','Gisborne','Hawkes Bay','Taranaki','Manawatu','Wellington','Tasman','Nelson','Marlborough','West Coast','Canterbury','Otago','Southland') DEFAULT NULL
  `BirthGender` char(1) DEFAULT NULL,
  `PrefGender` char(1) DEFAULT NULL,
  `PrefName` varchar(35) DEFAULT NULL,
  `Height` tinyint(3) unsigned DEFAULT NULL,
  `Weight` tinyint(3) unsigned DEFAULT NULL,
  `BloodType` char(3) DEFAULT NULL,
  `DonatingOrgans` set('liver','heart','kidney','bone','bone marrow','skin','connective tissue','cornea','pancreas','lung','middle ear','intestine') DEFAULT NULL,
  `ReceivingOrgans` set('liver','heart','kidney','bone','bone marrow','skin','connective tissue','cornea','pancreas','lung','middle ear','intestine') DEFAULT NULL,
  PRIMARY KEY (`Nhi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tblAdmins` (
  `Username` varchar(30) NOT NULL DEFAULT '',
  `FName` varchar(35) NOT NULL,
  `MName` varchar(70) DEFAULT NULL,
  `LName` varchar(35) NOT NULL,
  `Salt` char(64) NOT NULL,
  `Password` char(64) NOT NULL,
  `Modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tblClinicians` (
  `StaffID` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `FName` varchar(35) NOT NULL,
  `MName` varchar(70) DEFAULT NULL,
  `LName` varchar(35) NOT NULL,
  `Street1` varchar(100) DEFAULT NULL,
  `Street2` varchar(100) DEFAULT NULL,
  `Suburb` varchar(100) DEFAULT NULL,
  `Region` enum('Northland','Auckland','Waikato','Bay of Plenty','Gisborne','Hawkes Bay','Taranaki','Manawatu','Wellington','Tasman','Nelson','Marlborough','West Coast','Canterbury','Otago','Southland') DEFAULT NULL,
  `Modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`StaffID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tblDiseases` (
  `Patient` char(7) NOT NULL DEFAULT '',
  `Name` varchar(50) NOT NULL DEFAULT '',
  `DateDiagnosed` date NOT NULL DEFAULT '0000-00-00',
  `State` tinyint(2) NOT NULL,
  PRIMARY KEY (`Patient`,`Name`,`DateDiagnosed`),
  CONSTRAINT `tblDiseases_ibfk_1` FOREIGN KEY (`Patient`) REFERENCES `tblPatients` (`Nhi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tblMedications` (
  `Patient` char(7) NOT NULL DEFAULT '',
  `Name` varchar(50) NOT NULL DEFAULT '',
  `State` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`Patient`,`Name`),
  CONSTRAINT `tblMedications_ibfk_1` FOREIGN KEY (`Patient`) REFERENCES `tblPatients` (`Nhi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tblPatientContact` (
  `Patient` char(7) NOT NULL DEFAULT '',
  `StreetNumber` varchar(20) DEFAULT NULL,
  `StreetName` varchar(100) DEFAULT NULL,
  `Suburb` varchar(100) DEFAULT NULL,
  `City` varchar(100) DEFAULT NULL,
  `Region` enum('Northland','Auckland','Waikato','Bay of Plenty','Gisborne','Hawkes Bay','Taranaki','Manawatu','Wellington','Tasman','Nelson','Marlborough','West Coast','Canterbury','Otago','Southland') DEFAULT NULL,
  `Zip` char(4) DEFAULT NULL,
  `HomePhone` varchar(15) DEFAULT NULL,
  `WorkPhone` varchar(15) DEFAULT NULL,
  `MobilePhone` varchar(15) DEFAULT NULL,
  `Email` varchar(254) DEFAULT NULL,
  `ECName` varchar(70) DEFAULT NULL,
  `ECRelationship` varchar(30) DEFAULT NULL,
  `ECHomePhone` varchar(15) DEFAULT NULL,
  `ECWorkPhone` varchar(15) DEFAULT NULL,
  `ECMobilePhone` varchar(15) DEFAULT NULL,
  `ECEmail` varchar(254) DEFAULT NULL,
  PRIMARY KEY (`Patient`),
  CONSTRAINT `tblPatientContact_ibfk_1` FOREIGN KEY (`Patient`) REFERENCES `tblPatients` (`Nhi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tblPatientLogs` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Patient` char(7) NOT NULL DEFAULT '',
  `Time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Level` enum('OFF','SEVERE','WARNING','INFO','CONFIG','FINE','FINER','FINEST','ALL') NOT NULL,
  `Message` varchar(100) NOT NULL,
  `Action` varchar(100) NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `tblPatientLogs_ibfk_1` (`Patient`),
  CONSTRAINT `tblPatientLogs_ibfk_1` FOREIGN KEY (`Patient`) REFERENCES `tblPatients` (`Nhi`)
) ENGINE=InnoDB AUTO_INCREMENT=528 DEFAULT CHARSET=utf8;


CREATE TABLE `tblAdminLogs` (
 `Id` INT NOT NULL auto_increment,
 `Admin` varchar(30) NOT NULL DEFAULT '',
 `Time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 `Level` enum('OFF','SEVERE','WARNING','INFO','CONFIG','FINE','FINER','FINEST','ALL') NOT NULL,
 `Message` varchar(100) NOT NULL,
 `Action` varchar(100) NOT NULL,
 `Target` varchar(100) NOT NULL,
 PRIMARY KEY (`Id`),
 CONSTRAINT `tblAdminLogs_ibfk_1` FOREIGN KEY (`Admin`) REFERENCES `tblAdmins` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tblClinicianLogs` (
 `Id` INT NOT NULL AUTO_INCREMENT,
 `Clinician` mediumint(8) unsigned NOT NULL DEFAULT '0',
 `Time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 `Level` enum('OFF','SEVERE','WARNING','INFO','CONFIG','FINE','FINER','FINEST','ALL') NOT NULL,
 `Message` varchar(100) NOT NULL,
 `Action` varchar(100) NOT NULL,
 `TargetNhi` char(7) NOT NULL,
 PRIMARY KEY (`Id`),
 CONSTRAINT `tblClinicianLogs_ibfk_1` FOREIGN KEY (`Clinician`) REFERENCES `tblClinicians` (`StaffID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tblTransplantWaitList` (
 `Id` Int NOT NULL auto_increment,
 `Patient` char(7) NOT NULL DEFAULT '',
 `RequestDate` date NOT NULL DEFAULT '0000-00-00',
 `Organ` enum('liver','heart','kidney','bone','bone marrow','skin','connective tissue','cornea','pancreas','lung','middle ear','intestine') NOT NULL DEFAULT 'liver',
 `Region` enum('Northland','Auckland','Waikato','Bay of Plenty','Gisborne','Hawkes Bay','Taranaki','Manawatu','Wellington','Tasman','Nelson','Marlborough','West Coast','Canterbury','Otago','Southland') DEFAULT NULL,
 PRIMARY KEY (`Id`),
 CONSTRAINT `tblTransplantWaitList_ibfk_1` FOREIGN KEY (`Patient`) REFERENCES `tblPatients` (`Nhi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tblProcedures` (
  `Patient` char(7) NOT NULL DEFAULT '',
  `Summary` varchar(255) NOT NULL DEFAULT '',
  `Description` text,
  `ProDate` date NOT NULL DEFAULT '0000-00-00',
  `AffectedOrgans` set('liver','heart','kidney','bone','bone marrow','skin','connective tissue','cornea','pancreas','lung','middle ear','intestine') DEFAULT NULL,
  PRIMARY KEY (`Patient`,`Summary`,`ProDate`),
  CONSTRAINT `tblProcedures_ibfk_1` FOREIGN KEY (`Patient`) REFERENCES `tblPatients` (`Nhi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
