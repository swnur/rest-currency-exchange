INSERT INTO Currencies (code, full_name, sign)
VALUES ('USD', 'US Dollar', '$'),
       ('EUR', 'Euro', '€'),
       ('PLN', 'Zloty', 'zł'),
       ('CZK', 'Czech Koruna', 'Kč'),
       ('GBP', 'Pound Sterling', '£'),
       ('CNY', 'Yuan Renminbi', '¥'),
       ('JPY', 'Yen', '¥'),
       ('SEK', 'Swedish Krona', 'kr'),
       ('TRY', 'Turkish Lira', '₺');

INSERT INTO ExchangeRates (base_currency_id, target_currency_id, rate)
VALUES (1, 2, 0.92),
       (1, 4, 22.94),
       (1, 5, 0.79),
       (2, 3, 4.35),
       (3, 2, 0.23),
       (1, 6, 7.18),
       (1, 7, 148.04),
       (3, 9, 0.21),
       (4, 6, 9.11),
       (6, 2, 0.0062),
       (7, 2, 0.088);