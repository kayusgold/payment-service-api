# Payment Processing System

A Spring Boot application that simulates a simple payment processing system, allowing users to make payments between accounts, check transaction statuses, and retrieve account balances.

## Features

- Process payments between accounts
- Check transaction status
- Retrieve account balances
- Secure API endpoints with JWT Authentication
- In-memory H2 database for data storage
- Input validation and error handling

## Technical Stack

- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- H2 Database
- Maven/Gradle (build tool)
- JUnit 5 & Mockito (testing)

## API Endpoints

### Payment Operations

```
POST /api/v1/authenticate
POST /api/v1/payments
GET /api/v1/transactions/{transactionId}
GET /api/v1/accounts/{accountId}
```

### Request/Response Examples

#### 1. Authentication
```json
POST /api/v1/authenticate
{
    "username": "femi",
    "password": "1234user"
}
```

Response
```json
{
    "status": true,
    "message": "Login successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZW1pIiwiaWF0IjoxNzI5NjgzMzc2LCJleHAiOjE3Mjk2ODY5NzZ9.nZFDV8frey4n-FI3TkaUg8yfPTEUrwn02xOR8t_eAA4"
    }
}
```

#### 2. Process Payment
```json
POST /api/v1/payments
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
    "senderAccountNumber": "2000012345",
    "receiverAccountNumber": "1000012345",
    "amount": 2000.0
}
```

Response:
```json
{
    "status": true,
    "message": "Transaction successful",
    "data": {
        "id": 3,
        "transactionRef": "172968569840812133077",
        "amount": 2000,
        "timestamp": "2024-10-23T13:14:58.4085947",
        "status": "COMPLETED",
        "sender_id": 1,
        "receiver_id": 2
    }
}
```

#### 3. Get Transaction Status - By Id or transactionRef
```json
GET /api/v1/transactions/1
GET /api/v1/transactions/172971161398621653429
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Response:
{
   "status": true,
   "message": "Transaction found",
   "data": {
      "id": 1,
      "transactionRef": "172971060536521344565",
      "amount": 2000.00,
      "timestamp": "2024-10-23T20:10:05.366206",
      "status": "COMPLETED",
      "sender_id": 2,
      "receiver_id": 1
   }
}
```

#### 4. Get Accounts 
###### If authenticated user is ADMIN, all accounts are returned else only authenticated user's account(s) are returned.
```json
GET /api/v1/accounts
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
        
Response:
{
   "status": true,
   "message": "Account(s) fetched successfully",
   "data": [
      {
         "id": 1,
         "user": {
         "fullName": "femi",
         "username": "femi"
      },
      "accountNumber": "1000012345",
      "balance": 100002000.00,
      "status": "ACTIVE"
      }
   ]
}
```

#### 5. Get Account by id
```json
GET /api/v1/accounts/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Response:
{
   "status": true,
   "message": "Account(s) fetched successfully",
   "data": {
      "id": 1,
      "user": {
        "fullName": "femi",
        "username": "femi"
      },
      "accountNumber": "1000012345",
      "balance": 100000000.00,
      "status": "ACTIVE"
   }
}
```

## Database Schema

### Accounts Table
```sql
CREATE TABLE accounts (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     user_id BIGINT,                           -- Foreign key referencing the user
     accountNumber VARCHAR(255) UNIQUE NOT NULL,
     balance DECIMAL(19, 2),                   -- Balance in decimal form with precision
     status VARCHAR(255),                      -- Enum type status
     CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);

```

### Transactions Table
```sql
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_ref VARCHAR(255),               -- Transaction reference
    sender_id BIGINT,                           -- Foreign key referencing sender account
    receiver_id BIGINT,                         -- Foreign key referencing receiver account
    amount DECIMAL(19, 2),                      -- Amount in decimal form
    timestamp TIMESTAMP,                        -- Timestamp for the transaction
    status VARCHAR(255),                        -- Enum type status
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES accounts(id), -- Sender account reference
    CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES accounts(id) -- Receiver account reference
);

```

## Security

- Basic Authentication is implemented using Spring Security
- Default users are configured in memory:
  - Username: `admin` / Password: `admin` (ADMIN role)
  - Username: `user` / Password: `user` (USER role)

## Error Handling

The application returns appropriate HTTP status codes:

- 200: Successful operation
- 400: Invalid input
- 401: Unauthorized
- 404: Resource not found
- 500: Internal server error

## Running the Application

1. Clone the repository:
```bash
git clone https://github.com/kayusgold/assignment-payment-service-api.git
```

2. Navigate to the project directory:
```bash
cd assignment-payment-service-api
```

3. Build the project:
```bash
./mvnw clean install
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`

## Testing

Run the tests using:
```bash
./mvnw test
```

The project includes:
- Unit tests for service layer
- Integration tests for REST endpoints
- Repository layer tests

## Development Guidelines

1. **Transaction Management**
   - Use `@Transactional` annotations appropriately
   - Implement proper rollback mechanisms
   - Handle concurrent transactions

2. **Validation**
   - Validate all input parameters
   - Implement proper exception handling
   - Use appropriate constraint annotations

3. **Security**
   - Implement proper authentication
   - Use role-based access control
   - Secure sensitive data

## Future Improvements

- Implement JWT authentication
- Add support for multiple currencies
- Implement transaction history
- Add pagination for transaction listing
- Implement rate limiting
- Add API documentation using Swagger/OpenAPI

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.