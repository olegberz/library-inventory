-- Executed automatically by the MySQL container on its FIRST start
-- (only when the data volume is empty).

CREATE TABLE IF NOT EXISTS books (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    title          VARCHAR(200) NOT NULL,
    author         VARCHAR(150) NOT NULL,
    isbn           VARCHAR(13)  NOT NULL,
    genre          VARCHAR(50)  NOT NULL,
    published_year INT          NOT NULL,
    quantity       INT          NOT NULL,
    shelf_location VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_books_isbn UNIQUE (isbn)
);

INSERT INTO books (title, author, isbn, genre, published_year, quantity, shelf_location) VALUES
('Head First Java',                 'Kathy Sierra, Bert Bates', '9780596009205', 'Programming',  2005, 4, 'A-01'),
('Java: A Beginner''s Guide',       'Herbert Schildt',          '9781260440218', 'Programming',  2018, 3, 'A-02'),
('Clean Code',                      'Robert C. Martin',         '9780132350884', 'Programming',  2008, 2, 'A-03'),
('Introduction to Algorithms',      'Thomas H. Cormen',         '9780262046305', 'Computer Science', 2022, 1, 'B-01'),
('The Pragmatic Programmer',        'David Thomas, Andrew Hunt','9780135957059', 'Programming',  2019, 2, 'A-04'),
('1984',                            'George Orwell',            '9780451524935', 'Fiction',      1949, 5, 'C-10'),
('The Little Prince',               'Antoine de Saint-Exupery', '9780156012195', 'Fiction',      1943, 6, 'C-11'),
('Sapiens',                         'Yuval Noah Harari',        '9780062316097', 'History',      2015, 2, 'D-03');
