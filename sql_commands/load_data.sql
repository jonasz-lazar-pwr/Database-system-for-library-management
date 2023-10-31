-- Clear chosen tables
DELETE FROM Loans;
DELETE FROM Reports;
DELETE FROM Reservations;
DELETE FROM Books;
DELETE FROM Authors;
DELETE FROM Users;

-- Insert sample users into the "Users" table
INSERT INTO Users (FirstName, LastName, Address, PhoneNumber, CardNumber, Role)
VALUES
    ('Jan', 'Kowalski', 'ul. Kwiatowa 1, Warszawa', '+48 123 456 789', 'A12345', 'czytelnik'),
    ('Anna', 'Nowak', 'ul. Leśna 2, Kraków', '+48 987 654 321', 'B67890', 'czytelnik'),
    ('Piotr', 'Wiśniewski', 'ul. Słoneczna 3, Gdańsk', '+48 555 333 111', 'C24680', 'czytelnik'),
    ('Maria', 'Jankowska', 'ul. Zielona 4, Poznań', '+48 111 222 333', 'D13579', 'czytelnik'),
    ('Krzysztof', 'Nowicki', 'ul. Ogrodowa 5, Wrocław', '+48 888 999 000', 'E35791', 'czytelnik'),
    ('Ewa', 'Kwiatkowska', 'ul. Polna 6, Łódź', '+48 222 333 444', 'F24680', 'czytelnik'),
    ('Andrzej', 'Lewandowski', 'ul. Górska 7, Katowice', '+48 777 888 999', 'G13579', 'czytelnik'),
    ('Barbara', 'Kaczor', 'ul. Wiosenna 8, Lublin', '+48 444 555 666', 'H35791', 'czytelnik'),
    ('Marek', 'Dąbrowski', 'ul. Dolna 9, Szczecin', '+48 666 777 888', 'I24680', 'czytelnik'),
    ('Agnieszka', 'Wojciechowska', 'ul. Szkolna 10, Bydgoszcz', '+48 333 444 555', 'J13579', 'czytelnik'),
    ('Alicja', 'Bibliotekarska', 'ul. Biblioteczna 1, Warszawa', '+48 111 222 333', 'B12345', 'bibliotekarz'),
    ('Bartosz', 'Bibliotekarski', 'ul. Książkowa 2, Kraków', '+48 222 333 444', 'B67891', 'bibliotekarz'),
    ('Cezary', 'Bibliotekarski', 'ul. Wypożyczalna 3, Gdańsk', '+48 333 444 555', 'C13579', 'bibliotekarz');

-- Insert sample authors into the "Authors" table
INSERT INTO Authors (FirstName, LastName, BirthDate, Nationality)
VALUES
    ('J.R.R.', 'Tolkien', '1892-01-03', 'brytyjska'),
    ('J.K.', 'Rowling', '1965-07-31', 'brytyjska'),
    ('Fiodor', 'Dostojewski', '1821-11-11', 'rosyjska'),
    ('Andrzej', 'Sapkowski', '1948-06-21', 'polska'),
    ('Stephen', 'King', '1947-09-21', 'amerykańska'),
    ('Michaił', 'Bułhakow', '1891-05-15', 'rosyjska'),
    ('Lucy Maud', 'Montgomery', '1874-11-30', 'kanadyjska'),
    ('Jane', 'Austen', '1775-12-16', 'brytyjska'),
    ('Bram', 'Stoker', '1847-11-08', 'irlandzka'),
    ('George', 'Orwell', '1903-06-25', 'brytyjska');

-- Insert sample books into the "Books" table
INSERT INTO Books (Title, AuthorID, Publisher, PublicationYear, ISBN, Availability)
VALUES
    ('Władca Pierścieni: Drużyna Pierścienia', 1, 'Wydawnictwo Literackie', 2001, '9788373191723', 'dostępna'),
    ('Harry Potter i Kamień Filozoficzny', 2, 'Media Rodzina', 1999, '9788372780331', 'dostępna'),
    ('Zbrodnia i Kara', 3, 'Czytelnik', 2010, '9788307026319', 'dostępna'),
    ('Wiedźmin: Ostatnie Życzenie', 4, 'SuperNOWA', 2008, '9788375542019', 'dostępna'),
    ('To', 5, 'Albatros', 2018, '9788328014584', 'dostępna'),
    ('Hobbit, czyli Tam i Z Powrotem', 1, 'Wydawnictwo Literackie', 2003, '9788373191044', 'dostępna'),
    ('Mistrz i Małgorzata', 6, 'Iskry', 2001, '9788320710048', 'dostępna'),
    ('Ania z Zielonego Wzgórza', 7, 'Wydawnictwo Ameet', 2018, '9788381517854', 'dostępna'),
    ('Duma i Uprzedzenie', 8, 'Zielona Sowa', 2013, '9788362447170', 'dostępna'),
    ('Dracula', 9, 'Prószyński i S-ka', 2003, '9788373270801', 'dostępna'),
    ('1984', 10, 'Dom Wydawniczy REBIS', 2018, '9788389141418', 'dostępna');

-- Insert sample loans into the "Loans" table
INSERT INTO Loans (UserID, BookID, LoanDate, DueDate, ReturnDate)
VALUES
    (1, 1, '2023-10-01', '2023-10-15', '2023-10-14'),
    (2, 2, '2023-10-02', '2023-10-16', '2023-10-15'),
    (3, 3, '2023-10-03', '2023-10-17', '2023-10-16'),
    (4, 4, '2023-10-04', '2023-10-18', '2023-10-17'),
    (5, 5, '2023-10-05', '2023-10-19', '2023-10-18'),
    (6, 6, '2023-10-06', '2023-10-20', '2023-10-19'),
    (7, 7, '2023-10-07', '2023-10-21', '2023-10-20'),
    (8, 8, '2023-10-08', '2023-10-22', '2023-10-21'),
    (9, 9, '2023-10-09', '2023-10-23', '2023-10-22'),
    (10, 10, '2023-10-10', '2023-10-24', '2023-10-23'),
    (11, 11, '2023-10-11', '2023-10-25', '2023-10-24'),
    (12, 1, '2023-10-12', '2023-10-26', '2023-10-25'),
    (13, 2, '2023-10-13', '2023-10-27', '2023-10-26');

-- Insert reservations for each reader into the "Reservations" table
INSERT INTO Reservations (UserID, BookID, ReservationDate, Status)
VALUES
    (1, 1, '2023-10-01', 'aktywna'),
    (1, 2, '2023-10-02', 'aktywna'),
    (2, 3, '2023-10-03', 'aktywna'),
    (2, 4, '2023-10-04', 'aktywna'),
    (3, 5, '2023-10-05', 'aktywna'),
    (3, 6, '2023-10-06', 'aktywna'),
    (4, 7, '2023-10-07', 'aktywna'),
    (4, 8, '2023-10-08', 'aktywna'),
    (5, 9, '2023-10-09', 'aktywna'),
    (5, 10, '2023-10-10', 'aktywna'),
    (6, 11, '2023-10-11', 'aktywna');

-- Insert sample report into the "Reports" table
INSERT INTO Reports (Title, Description, GenerationDate, ReportContent, GeneratedByUserID)
VALUES
    ('Najpopularniejsze książki',
     'Raport przedstawiający listę najczęściej wypożyczanych książek.',
     '2023-10-31 14:30:00',
     '1. "Władca Pierścieni: Drużyna Pierścienia"\n2. "Harry Potter i Kamień Filozoficzny"\n3. "Zbrodnia i Kara"\n4. "Wiedźmin: Ostatnie Życzenie"\n5. "To"',
     13);
