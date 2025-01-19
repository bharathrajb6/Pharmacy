# Pharmacy Management System

## Overview

The Pharmacy Management System is a comprehensive solution for managing pharmaceutical operations, including inventory,
prescriptions, billing, and customer data. This project is designed to streamline operations for pharmacies, improve
efficiency, and enhance customer satisfaction.

## Features

- **User Management**: Register and manage accounts for customers and admins.
- **Inventory Management**: Add, update, delete, and track medications and stock levels.
- **Prescription Handling**: Upload and process customer prescriptions.
- **Billing System**: Generate invoices for purchases and track payment statuses.
- **Search Functionality**: Easily search for medications by name, category, or availability.
- **Notifications**: Notify users about low stock, prescription approvals, and order updates.

## Technology Stack

- **Backend**: Spring Boot (Java)
- **Database**: MySQL
- **Authentication**: JWT-based authentication for secure login
- **Other Tools**: Maven/Gradle, Postman (for API testing)

## Prerequisites

Before you begin, ensure you have the following installed:

- Java 17 or later 
- MySQL 8.0 or later 
- Redis
- Maven
- Postman (for API testing)

## Installation

### Backend Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/Pharmacy.git
   cd Pharmacy
   ```
2. Configure the database:
    - Create a new database (e.g., `pharmacy_db`).
    - Update the database configuration in the `application.yml` file for Spring Boot.
3. Build and run the backend:
   ```bash
   ./mvnw spring-boot:run
   ```

## Usage

1. Access the application in your browser at `http://localhost:8091`
2. Log in using the provided credentials or register a new account.
3. Use the APIs to manage inventory, prescriptions, and billing.

## API Endpoints

- **Authentication**
    - POST `login` - Log in to the system
    - POST `/register` - Register a new user
- **Medications**
    - GET `/api/v1/medication` - Fetch all medications
    - GET `/api/v1/medication/{medicationID}` - Fetch medication by id
    - POST `/api/v1/medication` - Add a new medication
    - PUT `/api/v1/medication/{id}` - Update medication details
    - DELETE `/api/medications/{id}` - Delete a medication
    - POST `/api/v1/batch/{medicationID}` - Add a new batch of medication
    - GET `/api/v1/batch/{batchNumber}` - Get batch details by batch number
    - GET `/api/v1/batches/{medicationID}` - Get all batches of medication
- **Prescriptions**
    - POST `/api/v1/prescription/upload` - Upload a prescription
    - GET `/api/v1/prescription/getDetails/{prescriptionID}` - View prescription details
- **Order**
    - POST `/api/v1/order` - Place an order
    - GET `/api/v1/order/{orderID}` - View order details
    - GET `/api/v1/order` - Get all orders of a user
    - PUT `/api/v1/order/{orderID}/cancel` - Cancel order
    - GET `/api/v1/order/{orderID}/track` - Track order
    - GET `/api/v1/order/filter/pdf/{username}` - To get all orders in PDF format
    - GET `/api/v1/order/filter/{username}` - Get the orders of the user by specified time range
  

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add your message here"
   ```
4. Push to the branch:
   ```bash
   git push origin feature/your-feature-name
   ```
5. Open a pull request.

## Contact

For any queries, please contact:

- **Name**: Bharath S R
- **Email**: bharathrajb26@gmail.com
- **GitHub**: bharathrajb6
 