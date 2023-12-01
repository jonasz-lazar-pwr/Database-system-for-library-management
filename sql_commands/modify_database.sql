DROP TABLE IF EXISTS Reports;
DROP TABLE IF EXISTS Reservations;
DROP TABLE IF EXISTS Loans;
DROP TABLE IF EXISTS Books;
DROP TABLE IF EXISTS Users;

-- DROP DATABASE libraryDB;
-- CREATE DATABASE libraryDB;

USE libraryDB;

-- Create table "Users"
CREATE TABLE Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Address VARCHAR(100) NOT NULL,
    PhoneNumber VARCHAR(15) UNIQUE NOT NULL,
    CardNumber VARCHAR(20) UNIQUE NOT NULL,
    Email VARCHAR(50) UNIQUE NOT NULL,
    Login VARCHAR(20) UNIQUE NOT NULL,
    UserPassword VARCHAR(255) NOT NULL,
    UserRole ENUM('czytelnik', 'bibliotekarz')
        CHECK (UserRole IN ('czytelnik', 'bibliotekarz'))
);

-- Create table "Books"
CREATE TABLE Books (
    BookID INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    Title VARCHAR(100) NOT NULL,
    AuthorLastName VARCHAR(100) NOT NULL,
    AuthorFirstName VARCHAR(100) NOT NULL,
    Publisher VARCHAR(50) NOT NULL,
    PublicationYear INT NOT NULL,
    ISBN VARCHAR(13) UNIQUE NOT NULL,
    BookAvailability ENUM('dostępna', 'wypożyczona', 'zarezerwowana')
        CHECK (BookAvailability IN ('dostępna', 'wypożyczona', 'zarezerwowana'))
);

-- Create table "Loans"
CREATE TABLE Loans (
    LoanID INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    UserID INT NOT NULL,
    BookID INT NOT NULL,
    LoanDate DATE NOT NULL,
    DueDate DATE NOT NULL,
    ReturnDate DATE,
    Status ENUM('aktywne', 'zrealizowane')
        CHECK (Status IN ('aktywne', 'zrealizowane')),
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID)
);

-- Create table "Reservations"
CREATE TABLE Reservations (
    ReservationID INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    UserID INT NOT NULL,
    BookID INT NOT NULL,
    ReservationDate DATE NOT NULL,
    Status ENUM('aktywna', 'zrealizowana', 'anulowana')
        CHECK (Status IN ('aktywna', 'zrealizowana', 'anulowana')),
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID)
);

-- Create table "Reports"
CREATE TABLE Reports (
    ReportID INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    Title VARCHAR(100) NOT NULL,
    Description TEXT NOT NULL,
    GenerationDate DATETIME NOT NULL,
    ReportContent TEXT NOT NULL,
    GeneratedByUserID INT NOT NULL,
    FOREIGN KEY (GeneratedByUserID) REFERENCES Users(UserID)
);