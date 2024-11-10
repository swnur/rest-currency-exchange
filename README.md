# REST API Currency Exchange App

**Deployed Application**: [Currency Exchange Service](http://167.235.250.176:8080/rest-currency-exchange/)

This project is a **REST API** for managing currencies and exchange rates. It allows users to view, add, and update lists of currencies and exchange rates, as well as calculate conversions between different currencies.

## Project Overview

This project was developed following a roadmap https://zhukovsd.github.io/java-backend-learning-course/ provided by Sergey Zhukov. It demonstrates core concepts of **Java servlets**, **MVC pattern**, **REST API design**, and basic **database management** using SQLite. The application is built with minimal frameworks to provide hands-on experience with foundational Java EE concepts.
For testing and visualizing results, the frontend interface is provided by [currency-exchange-frontend](https://github.com/zhukovsd/currency-exchange-frontend). Currency codes adhere to the [ISO 4217 standard](https://www.iban.com/currency-codes) as listed by IBAN.


---

## Technologies Used

This project is built with **Java Servlets** for handling HTTP requests and follows the MVC architecture. It runs on **Tomcat** as the application server and uses **Maven** for managing dependencies and building the project. For simplicity in deployment, an **SQLite** database is embedded to store currency and exchange rate data, avoiding the need for external database setup on the server.

## Database Schema

The project uses an SQLite database with two main tables:

### `Currencies` Table
Stores information about each currency. Example structure:
| Column   | Type    | Description                      |
|----------|---------|----------------------------------|
| ID       | int     | Auto-increment primary key      |
| Code     | Varchar | Currency code (e.g., USD)       |
| FullName | Varchar | Full name of the currency       |
| Sign     | Varchar | Currency symbol (e.g., $)       |

- **Primary Key**: ID
- **Unique Index**: Code

### `ExchangeRates` Table
Stores exchange rates between currency pairs.
| Column           | Type        | Description                                        |
|------------------|-------------|----------------------------------------------------|
| ID               | int         | Auto-increment primary key                         |
| BaseCurrencyId   | int         | Foreign key to `Currencies.ID`                     |
| TargetCurrencyId | int         | Foreign key to `Currencies.ID`                     |
| Rate             | Decimal(6)  | Exchange rate from base currency to target currency|

- **Primary Key**: ID
- **Unique Index**: Pair of BaseCurrencyId and TargetCurrencyId

---

## REST API Endpoints

### Currencies

- **`GET /currencies`**  
  Retrieves the list of all currencies.
  - **Response**: JSON array of currency objects.
  - **Status Codes**: 200 (OK), 500 (Error).

- **`GET /currency/{code}`**  
  Retrieves details of a specific currency by code.
  - **Response**: JSON object of the currency.
  - **Status Codes**: 200 (OK), 400 (Bad Request), 404 (Not Found), 500 (Error).

- **`POST /currencies`**  
  Adds a new currency.
  - **Body**: Form data (`code`, `name`, `sign`).
  - **Response**: JSON object of the created currency.
  - **Status Codes**: 201 (Created), 400 (Bad Request), 409 (Conflict), 500 (Error).

### Exchange Rates

- **`GET /exchangeRates`**  
  Retrieves all exchange rates.
  - **Response**: JSON array of exchange rate objects.
  - **Status Codes**: 200 (OK), 500 (Error).

- **`GET /exchangeRate/{baseCode}{targetCode}`**  
  Retrieves a specific exchange rate for a currency pair.
  - **Response**: JSON object of the exchange rate.
  - **Status Codes**: 200 (OK), 400 (Bad Request), 404 (Not Found), 500 (Error).

- **`POST /exchangeRates`**  
  Adds a new exchange rate.
  - **Body**: Form data (`baseCurrencyCode`, `targetCurrencyCode`, `rate`).
  - **Response**: JSON object of the created exchange rate.
  - **Status Codes**: 201 (Created), 400 (Bad Request), 409 (Conflict), 404 (Currency Not Found), 500 (Error).

- **`PATCH /exchangeRate/{baseCode}{targetCode}`**  
  Updates an existing exchange rate.
  - **Body**: Form data (`rate`).
  - **Response**: JSON object of the updated exchange rate.
  - **Status Codes**: 200 (OK), 400 (Bad Request), 404 (Not Found), 500 (Error).

### Currency Conversion

- **`GET /exchange`**  
  Converts an amount from one currency to another.
  - **Parameters**: `from`, `to`, `amount`
  - **Response**: JSON object with conversion details.
  - **Status Codes**: 200 (OK), 400 (Bad Request), 404 (Not Found), 500 (Error).
