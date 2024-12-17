# Spring Boot Invoice System

## Description
This project is a Spring Boot-based application to manage invoices. It allows you to create, update, and process overdue invoices, with support for partial and complete payments.

## Features
- Create and fetch invoices
- Pay invoices partially or completely
- Process overdue invoices, marking them as paid or void and generating new ones with penalties
- Global exception handling

## Endpoints
### 1. Create an Invoice
**POST /invoices**
```json
{
  "amount": 199.99,
  "due_date": "2023-12-31"
}
```
Response:
```json
{
  "id": 1,
  "amount": 199.99,
  "paidAmount": 0.0,
  "dueDate": "2023-12-31",
  "status": "pending"
}
```

### 2. Get All Invoices
**GET /invoices**
Response:
```json
[
  {
    "id": 1,
    "amount": 199.99,
    "paidAmount": 0.0,
    "dueDate": "2023-12-31",
    "status": "pending"
  }
]
```

### 3. Pay an Invoice
**POST /invoices/{id}/payments**
```json
{
  "amount": 50.0
}
```
Response:
```json
{
  "id": 1,
  "amount": 199.99,
  "paidAmount": 50.0,
  "dueDate": "2023-12-31",
  "status": "pending"
}
```

### 4. Process Overdue Invoices
**POST /invoices/process-overdue**
```json
{
  "late_fee": 10.5,
  "overdue_days": 10
}
```
Response: `204 No Content`

## Running Locally

### Prerequisites
- Docker and Docker Compose installed

### Steps
1. Clone the repository.
2. Package the application:
   ```bash
   mvn clean package
   ```
3. Start the application:
   ```bash
   docker-compose up --build
   ```
4. Access the application at `http://localhost:8080`.

## Database
- MySQL is used as the database.
- Update the database credentials in `application.yml` and `docker-compose.yml` as needed.

## Testing
- Use tools like Postman to interact with the endpoints.

