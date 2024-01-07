-- ------------ CZYTELNIK ------------

-- ZARZĄDZANIE KONTEM (zmiana adresu e-mail)
UPDATE Users
SET Email = 'kowalski.jan@email.com'
WHERE Login = 'jan123';

-- UTWORZENIE KONTA
INSERT INTO Users (FirstName, LastName, Address, PhoneNumber, CardNumber, Email, Login, UserPassword, UserRole)
VALUES ('Nowy', 'Użytkownik', 'ul. Testowa 123, Miasto', '+48 555 666 777', 'X98765', 'nowy.uzytkownik@email.com', 'nowy123', MD5('nowe_haslo'), 'czytelnik');

-- WYPOŻYCZENIE KSIĄŻKI
INSERT INTO Loans (UserID, BookID, LoanDate, DueDate, ReturnDate, Status)
VALUES (1, 1, '2023-12-01', '2023-12-15', NULL, 'aktywne');

-- PRZEGLĄDANIE KSIĄŻEK
SELECT * FROM Books
WHERE BookAvailability = 'dostępna';

-- REZERWOWANIE KSIĄŻKI
INSERT INTO Reservations (UserID, BookID, ReservationDate, Status)
VALUES (1, 1, '2023-12-01', 'aktywna');

-- PRZEGLĄDANIE HISTORII WYPOŻYCZEŃ
SELECT
    Books.Title AS BookTitle,
    Loans.LoanDate,
    Loans.DueDate,
    Loans.ReturnDate,
    Loans.Status
FROM
    Loans
JOIN
    Books ON Loans.BookID = Books.BookID
WHERE
    Loans.UserID = 1;

-- ZARZĄDZANIE REZERWACJAMI
UPDATE Reservations
SET Status = 'anulowana'
WHERE ReservationID = 1;

-- ---------- BIBLIOTEKARZ -----------

-- GENEROWANIE RAPORTÓW
INSERT INTO Reports (Title, Description, GenerationDate, ReportContent, GeneratedByUserID)
VALUES ('Raport Nr 1', 'Raport popularności książek', '2023-12-01 15:30:00', 'Przykładowa treść', 1);

-- ZWRÓCENIE WYPOŻYCZONEJ KSIĄŻKI
UPDATE Loans
SET
    ReturnDate = CURRENT_DATE,
    Status = 'zrealizowane'
WHERE
    LoanID = 3;

UPDATE Books
SET
    BookAvailability = 'dostępna'
WHERE
    BookID = (SELECT BookID FROM Loans WHERE LoanID = 3);

-- DODANIE KSIĄŻKI
INSERT INTO Books (Title, AuthorLastName, AuthorFirstName, Publisher, PublicationYear, ISBN, BookAvailability)
VALUES ('Kubuś Puchatek', 'Milne', 'A.A.', 'Wydawnictwo XYZ', 1926, '9780416239102', 'dostępna');

-- USUNIĘCIE KSIĄŻKI
DELETE FROM Reservations
WHERE BookID = 5;

DELETE FROM Loans
WHERE BookID = 5;

DELETE FROM Books
WHERE BookID = 5;

-- ---------- ZARZĄDZANIE ----------
CREATE VIEW BookView AS
SELECT
    Title AS BookTitle,
    CONCAT(AuthorFirstName, ' ', AuthorLastName) AS AuthorFullName,
    PublicationYear,
    BookAvailability,
    Amount
FROM
    Books;

-- -------------------------------------------------


CREATE VIEW UserReservationsView AS
SELECT
    U.Login AS login,
    B.Title AS title,
    R.ReservationDate AS reservation_date,
    R.Status AS status
FROM
    Reservations R
JOIN Users U ON R.UserID = U.UserID
JOIN Books B ON R.BookID = B.BookID;

-- -----------------------------------------------

CREATE VIEW UserLoansView AS
SELECT
    L.LoanID AS loan_id,
    U.Login AS login,
    B.Title AS title,
    L.LoanDate AS loan_date,
    L.ReturnDate AS return_date,
    L.Status AS status
FROM
    Loans L
JOIN Users U ON L.UserID = U.UserID
JOIN Books B ON L.BookID = B.BookID;


-- ---------- RAPORTY ----------
CREATE VIEW mostPopularBooksView AS
SELECT
    b.Title AS BookTitle,
    CONCAT(b.AuthorFirstName, ' ', b.AuthorLastName) AS AuthorName,
    b.Publisher AS Publisher,
    b.PublicationYear AS PublicationYear,
    COUNT(l.LoanID) AS NumberOfLoans
FROM
    Books b
LEFT JOIN
    Loans l ON b.BookID = l.BookID
GROUP BY
    b.BookID
ORDER BY
    NumberOfLoans DESC;

-- -------------------------------------------------

CREATE VIEW mostActiveUsersView AS
SELECT
    u.UserID AS UserID,
    CONCAT(u.FirstName, ' ', u.LastName) AS UserName,
    u.Login AS UserLogin,
    COUNT(l.LoanID) AS NumberOfLoans
FROM
    Users u
JOIN
    Loans l ON u.UserID = l.UserID
GROUP BY
    u.UserID
ORDER BY
    NumberOfLoans DESC;

-- -----------------------------------------------

CREATE VIEW mostPopularAuthorsView AS
SELECT
    b.AuthorFirstName AS AuthorFirstName,
    b.AuthorLastName AS AuthorLastName,
    COUNT(l.LoanID) AS NumberOfLoans
FROM
    Books b
LEFT JOIN
    Loans l ON b.BookID = l.BookID
GROUP BY
    b.AuthorFirstName, b.AuthorLastName
ORDER BY
    NumberOfLoans DESC;

-- -----------------------------------------------

CREATE VIEW longestNotReturnedBooksView AS
SELECT
    b.Title AS BookTitle,
    CONCAT(b.AuthorFirstName, ' ', b.AuthorLastName) AS AuthorName,
    l.LoanDate AS LoanDate,
    u.Login AS UserLogin,
    DATEDIFF(NOW(), l.LoanDate) AS DaysSinceLoan
FROM
    Books b
JOIN
    Loans l ON b.BookID = l.BookID
JOIN
    Users u ON l.UserID = u.UserID
WHERE
    l.Status = 'aktywne' AND l.ReturnDate IS NULL
ORDER BY
    DaysSinceLoan DESC;
