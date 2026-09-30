-- Executed automatically by the MySQL container on its FIRST start
-- (only when the data volume is empty; "docker compose down -v" resets it).

-- 1. Genres -------------------------------------------------------------
CREATE TABLE genres (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT uk_genres_name UNIQUE (name)
);

-- 2. Authors ------------------------------------------------------------
CREATE TABLE authors (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(60) NOT NULL,
    last_name  VARCHAR(60) NOT NULL,
    country    VARCHAR(60),
    birth_year INT,
    PRIMARY KEY (id)
);

-- 3. Books (many books -> one author, many books -> one genre) ----------
CREATE TABLE books (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    title          VARCHAR(200) NOT NULL,
    author_id      BIGINT       NOT NULL,
    genre_id       BIGINT       NOT NULL,
    isbn           VARCHAR(13)  NOT NULL,
    published_year INT          NOT NULL,
    quantity       INT          NOT NULL,
    shelf_location VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_books_isbn   UNIQUE (isbn),
    CONSTRAINT fk_books_author FOREIGN KEY (author_id) REFERENCES authors (id),
    CONSTRAINT fk_books_genre  FOREIGN KEY (genre_id)  REFERENCES genres (id)
);

-- 4. Members ------------------------------------------------------------
CREATE TABLE members (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    first_name    VARCHAR(60)  NOT NULL,
    last_name     VARCHAR(60)  NOT NULL,
    email         VARCHAR(120) NOT NULL,
    phone         VARCHAR(20),
    registered_on DATE         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_members_email UNIQUE (email)
);

-- 5. Loans (one book copy lent to one member) ---------------------------
CREATE TABLE loans (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    book_id     BIGINT NOT NULL,
    member_id   BIGINT NOT NULL,
    loan_date   DATE   NOT NULL,
    due_date    DATE   NOT NULL,
    return_date DATE,
    PRIMARY KEY (id),
    CONSTRAINT fk_loans_book   FOREIGN KEY (book_id)   REFERENCES books (id),
    CONSTRAINT fk_loans_member FOREIGN KEY (member_id) REFERENCES members (id)
);

-- Sample data -----------------------------------------------------------
INSERT INTO genres (name, description) VALUES
('Programming',      'Software development and programming languages'),
('Computer Science', 'Algorithms, data structures and theory'),
('Fiction',          'Novels and short stories'),
('History',          'World and regional history'),
('Business',         'Management, economics and entrepreneurship');

INSERT INTO authors (first_name, last_name, country, birth_year) VALUES
('Kathy',    'Sierra',           'USA',            1957),  -- 1
('Herbert',  'Schildt',          'USA',            1951),  -- 2
('Robert C.','Martin',           'USA',            1952),  -- 3
('Thomas H.','Cormen',           'USA',            1956),  -- 4
('George',   'Orwell',           'United Kingdom', 1903),  -- 5
('Antoine',  'de Saint-Exupery', 'France',         1900),  -- 6
('Yuval Noah','Harari',          'Israel',         1976),  -- 7
('Eric',     'Ries',             'USA',            1978);  -- 8

INSERT INTO books (title, author_id, genre_id, isbn, published_year, quantity, shelf_location) VALUES
('Head First Java',             1, 1, '9780596009205', 2005, 4, 'A-01'),  -- 1
('Java: A Beginner''s Guide',   2, 1, '9781260440218', 2018, 3, 'A-02'),  -- 2
('Clean Code',                  3, 1, '9780132350884', 2008, 2, 'A-03'),  -- 3
('Introduction to Algorithms',  4, 2, '9780262046305', 2022, 1, 'B-01'),  -- 4
('1984',                        5, 3, '9780451524935', 1949, 5, 'C-10'),  -- 5
('The Little Prince',           6, 3, '9780156012195', 1943, 6, 'C-11'),  -- 6
('Sapiens',                     7, 4, '9780062316097', 2015, 2, 'D-03'),  -- 7
('The Lean Startup',            8, 5, '9780307887894', 2011, 2, 'E-01');  -- 8

INSERT INTO members (first_name, last_name, email, phone, registered_on) VALUES
('Anna',   'Ozola',    'anna.ozola@example.com',    '+37120000001', '2025-09-01'),
('Janis',  'Kalnins',  'janis.kalnins@example.com', '+37120000002', '2025-10-15'),
('Marta',  'Liepina',  'marta.liepina@example.com', NULL,           '2026-02-03'),
('Peteris','Berzs',    'peteris.berzs@example.com', '+37120000004', '2026-05-20');

-- Loan dates are relative to the day the database is created,
-- so there is always one returned, two active and one overdue loan.
INSERT INTO loans (book_id, member_id, loan_date, due_date, return_date) VALUES
(1, 1, CURDATE() - INTERVAL 30 DAY, CURDATE() - INTERVAL 16 DAY, CURDATE() - INTERVAL 18 DAY),
(4, 2, CURDATE() - INTERVAL 5 DAY,  CURDATE() + INTERVAL 9 DAY,  NULL),
(3, 3, CURDATE() - INTERVAL 20 DAY, CURDATE() - INTERVAL 6 DAY,  NULL),
(5, 1, CURDATE() - INTERVAL 2 DAY,  CURDATE() + INTERVAL 12 DAY, NULL);
