-- DROP TABLE IF EXISTS Reports;
-- DROP TABLE IF EXISTS Reservations;
-- DROP TABLE IF EXISTS Loans;
-- DROP TABLE IF EXISTS Books;
-- DROP TABLE IF EXISTS Users;

-- DROP DATABASE libraryDB;
-- CREATE DATABASE libraryDB;

USE libraryDB;

-- Create table "Users"
CREATE TABLE Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    Address VARCHAR(100),
    PhoneNumber VARCHAR(15) UNIQUE,
    CardNumber VARCHAR(20) UNIQUE,
    Email VARCHAR(50) UNIQUE,
    Login VARCHAR(20) UNIQUE,
    UserPassword VARCHAR(255),
    UserRole ENUM('czytelnik', 'bibliotekarz')
        CHECK (UserRole IN ('czytelnik', 'bibliotekarz'))
);

-- Create table "Books"
CREATE TABLE Books (
    BookID INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(100),
    AuthorLastName VARCHAR(100),
    AuthorFirstName VARCHAR(100),
    Publisher VARCHAR(50),
    PublicationYear INT,
    ISBN VARCHAR(13) UNIQUE,
    BookAvailability ENUM('dostępna', 'wypożyczona', 'zarezerwowana')
        CHECK (BookAvailability IN ('dostępna', 'wypożyczona', 'zarezerwowana'))
);

-- Create table "Loans"
CREATE TABLE Loans (
    LoanID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    BookID INT NOT NULL,
    LoanDate DATE NOT NULL,
    DueDate DATE NOT NULL,
    ReturnDate DATE NOT NULL,
    Status ENUM('aktywne', 'zrealizowane')
        CHECK (Status IN ('aktywne', 'zrealizowane')),
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID)
);

-- Create table "Reservations"
CREATE TABLE Reservations (
    ReservationID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    BookID INT NOT NULL,
    ReservationDate DATE NOT NULL,
    Status ENUM('aktywna', 'zrealizowana'),
        CHECK (Status IN ('aktywna', 'zrealizowana')),
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID)
);

-- Create table "Reports"
CREATE TABLE Reports (
    ReportID INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(100),
    Description TEXT,
    GenerationDate DATETIME,
    ReportContent TEXT,
    GeneratedByUserID INT,
    FOREIGN KEY (GeneratedByUserID) REFERENCES Users(UserID)
);