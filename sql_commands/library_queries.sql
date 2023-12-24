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
    BookAvailability
FROM
    Books;




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



CREATE VIEW UserLoansView AS
SELECT
    U.Login AS login,
    B.Title AS title,
    L.LoanDate AS loan_date,
    L.ReturnDate AS return_date,
    L.Status AS status
FROM
    Loans L
JOIN Users U ON L.UserID = U.UserID
JOIN Books B ON L.BookID = B.BookID;