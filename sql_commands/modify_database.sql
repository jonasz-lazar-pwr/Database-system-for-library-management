-- Drop chosen tables
DROP TABLE IF EXISTS Reports;
DROP TABLE IF EXISTS Reservations;
DROP TABLE IF EXISTS Loans;
DROP TABLE IF EXISTS Books;
DROP TABLE IF EXISTS Authors;
DROP TABLE IF EXISTS Users;

-- Delete database "libraryDB"
DROP DATABASE libraryDB;

-- Auto-increment reset
ALTER TABLE Users AUTO_INCREMENT = 1;
ALTER TABLE Authors AUTO_INCREMENT = 1;
ALTER TABLE Books AUTO_INCREMENT = 1;
ALTER TABLE Reservations AUTO_INCREMENT = 1;
ALTER TABLE Reports AUTO_INCREMENT = 1;

-- Create database "libraryDB"
CREATE DATABASE libraryDB;

-- Operations on "libraryDB" database
USE libraryDB;

-- Create table "Users"
CREATE TABLE Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    Address VARCHAR(100),
    PhoneNumber VARCHAR(15),
    CardNumber VARCHAR(20) UNIQUE,
    Role ENUM('czytelnik', 'bibliotekarz')
);

-- Create table "Authors"
CREATE TABLE Authors (
    AuthorID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    BirthDate DATE,
    Nationality VARCHAR(50)
);

-- Create table "Books"
CREATE TABLE Books (
    BookID INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(100),
    AuthorID INT,
    Publisher VARCHAR(50),
    PublicationYear INT,
    ISBN VARCHAR(13) UNIQUE,
    Availability ENUM('dostępna', 'wypożyczona', 'zarezerwowana'),
    FOREIGN KEY (AuthorID) REFERENCES Authors(AuthorID)
);

-- Create table "Loans"
CREATE TABLE Loans (
    LoanID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT,
    BookID INT,
    LoanDate DATE,
    DueDate DATE,
    ReturnDate DATE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID)
);

-- Create table "Reservations"
CREATE TABLE Reservations (
    ReservationID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT,
    BookID INT,
    ReservationDate DATE,
    Status ENUM('aktywna', 'zrealizowana', 'anulowana'),
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
