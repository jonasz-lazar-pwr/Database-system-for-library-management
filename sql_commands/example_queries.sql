--CREATE (Dodaj nową książkę)
INSERT INTO Books (Title, AuthorLastName, AuthorFirstName, Publisher, PublicationYear, ISBN, Availability)
VALUES ('Ogniem i Mieczem', 'Sienkiewicz', 'Henryk', 'Znak', 1998, '9788324005457', 'dostępna');

--READ (Pobierz informacje o bibliotekarzach)
SELECT * FROM Users WHERE role = 'bibliotekarz';

--UPDATE (Zaktualizuj e-mail czytelnika o loginie jan123)
UPDATE Users SET email = 'kowalski.jan@email.com' WHERE login = 'jan123';

--DELETE (Usuń rezerwację o ID 2)
DELETE FROM Reservations WHERE ReservationID = 2;

--CREATE VIEW (Utwórz widok z informacjami o wypożyczeniach użytkowników)
CREATE VIEW BookLoansView AS
SELECT
    Loans.LoanID,
    Users.FirstName AS UserFirstName,
    Users.LastName AS UserLastName,
    Books.Title AS BookTitle,
    Loans.LoanDate,
    Loans.DueDate,
    Loans.ReturnDate,
    Loans.Status
FROM
    Loans
JOIN Users ON Loans.UserID = Users.UserID
JOIN Books ON Loans.BookID = Books.BookID;

--SELECT (Sprawdzenie dostępności konkretnej książki)
SELECT Title, Availability FROM Books WHERE Title = 'Władca Pierścieni: Drużyna Pierścienia';