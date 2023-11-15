-- Clear chosen tables
--DELETE FROM Loans;
--DELETE FROM Reports;
--DELETE FROM Reservations;
--DELETE FROM Books;
--DELETE FROM Users;

-- Insert sample users into the "Users" table
INSERT INTO Users (FirstName, LastName, Address, PhoneNumber, CardNumber, Email, Login, Password, Role)
VALUES
    ('Jan', 'Kowalski', 'ul. Kwiatowa 1, Warszawa', '+48 123 456 789', 'A12345', 'jan.kowalski@email.com', 'jan123', 'password123', 'czytelnik'),
    ('Anna', 'Nowak', 'ul. Leśna 2, Kraków', '+48 987 654 321', 'B67890', 'anna.nowak@email.com', 'anna456', 'password456', 'czytelnik'),
    ('Piotr', 'Wiśniewski', 'ul. Słoneczna 3, Gdańsk', '+48 555 333 111', 'C24680', 'piotr.wisniewski@email.com', 'piotr789', 'password789', 'czytelnik'),
    ('Maria', 'Jankowska', 'ul. Zielona 4, Poznań', '+48 111 222 333', 'D13579', 'maria.jankowska@email.com', 'maria101', 'password101', 'czytelnik'),
    ('Krzysztof', 'Nowicki', 'ul. Ogrodowa 5, Wrocław', '+48 888 999 000', 'E35791', 'krzysztof.nowicki@email.com', 'krzysztof2022', 'password2022', 'czytelnik'),
    ('Ewa', 'Kwiatkowska', 'ul. Polna 6, Łódź', '+48 000 333 444', 'F24680', 'ewa.kwiatkowska@email.com', 'ewa333', 'password333', 'czytelnik'),
    ('Andrzej', 'Lewandowski', 'ul. Górska 7, Katowice', '+48 777 888 999', 'G13579', 'andrzej.lewandowski@email.com', 'andrzej777', 'password777', 'czytelnik'),
    ('Barbara', 'Kaczor', 'ul. Wiosenna 8, Lublin', '+48 444 555 666', 'H35791', 'barbara.kaczor@email.com', 'barbara888', 'password888', 'czytelnik'),
    ('Marek', 'Dąbrowski', 'ul. Dolna 9, Szczecin', '+48 666 777 888', 'I24680', 'marek.dabrowski@email.com', 'marek999', 'password999', 'czytelnik'),
    ('Agnieszka', 'Wojciechowska', 'ul. Szkolna 10, Bydgoszcz', '+48 333 000 555', 'J13579', 'agnieszka.wojciechowska@email.com', 'agnieszka11', 'password11', 'czytelnik'),
    ('Alicja', 'Bibliotekarska', 'ul. Biblioteczna 1, Warszawa', '+48 111 222 555', 'B12345', 'alicja.bibliotekarska@email.com', 'alicja_lib', 'password_lib', 'bibliotekarz'),
    ('Bartosz', 'Bibliotekarski', 'ul. Książkowa 2, Kraków', '+48 222 333 444', 'B67891', 'bartosz.bibliotekarski@email.com', 'bartosz_lib', 'password_lib', 'bibliotekarz'),
    ('Cezary', 'Bibliotekarski', 'ul. Wypożyczalna 3, Gdańsk', '+48 333 444 555', 'C13579', 'cezary.bibliotekarski@email.com', 'cezary_lib', 'password_lib', 'bibliotekarz');


-- Insert sample books into the "Books" table
INSERT INTO Books (Title, AuthorLastName, AuthorFirstName, Publisher, PublicationYear, ISBN, Availability)
VALUES
    ('Władca Pierścieni: Drużyna Pierścienia', 'Tolkien', 'J.R.R.', 'Wydawnictwo Literackie', 2001, '9788373191723', 'dostępna'),
    ('Harry Potter i Kamień Filozoficzny', 'Rowling', 'J.K.', 'Media Rodzina', 1999, '9788372780331', 'dostępna'),
    ('Zbrodnia i Kara', 'Dostojewski', 'Fiodor', 'Czytelnik', 2010, '9788307026319', 'dostępna'),
    ('Wiedźmin: Ostatnie Życzenie', 'Sapkowski', 'Andrzej', 'SuperNOWA', 2008, '9788375542019', 'dostępna'),
    ('To', 'King', 'Stephen', 'Albatros', 2018, '9788328014584', 'dostępna'),
    ('Hobbit, czyli Tam i Z Powrotem', 'Tolkien', 'J.R.R.', 'Wydawnictwo Literackie', 2003, '9788373191044', 'dostępna'),
    ('Mistrz i Małgorzata', 'Bułhakow', 'Michaił', 'Iskry', 2001, '9788320710048', 'dostępna'),
    ('Ania z Zielonego Wzgórza', 'Montgomery', 'Lucy Maud', 'Wydawnictwo Ameet', 2018, '9788381517854', 'dostępna'),
    ('Duma i Uprzedzenie', 'Austen', 'Jane', 'Zielona Sowa', 2013, '9788362447170', 'dostępna'),
    ('Dracula', 'Stoker', 'Bram', 'Prószyński i S-ka', 2003, '9788373270801', 'dostępna'),
    ('1984', 'Orwell', 'George', 'Dom Wydawniczy REBIS', 2018, '9788389141418', 'dostępna');


-- Insert sample loans into the "Loans" table
INSERT INTO Loans (UserID, BookID, LoanDate, DueDate, ReturnDate, Status)
VALUES
    (1, 1, '2023-10-01', '2023-10-15', '2023-10-14', 'zrealizowane'),
    (2, 2, '2023-10-02', '2023-10-16', '2023-10-15', 'zrealizowane'),
    (3, 3, '2023-10-03', '2023-10-17', '2023-10-16', 'zrealizowane'),
    (4, 4, '2023-10-04', '2023-10-18', '2023-10-17', 'zrealizowane'),
    (5, 5, '2023-10-05', '2023-10-19', '2023-10-18', 'zrealizowane'),
    (6, 6, '2023-10-06', '2023-10-20', '2023-10-19', 'zrealizowane'),
    (7, 7, '2023-10-07', '2023-10-21', '2023-10-20', 'zrealizowane'),
    (8, 8, '2023-10-08', '2023-10-22', '2023-10-21', 'zrealizowane'),
    (9, 9, '2023-10-09', '2023-10-23', '2023-10-22', 'zrealizowane'),
    (10, 10, '2023-10-10', '2023-10-24', '2023-10-23', 'zrealizowane'),
    (11, 11, '2023-10-11', '2023-10-25', '2023-10-24', 'zrealizowane'),
    (12, 1, '2023-10-12', '2023-10-26', '2023-10-25', 'aktywne'),
    (13, 2, '2023-10-13', '2023-10-27', '2023-10-26', 'aktywne');

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