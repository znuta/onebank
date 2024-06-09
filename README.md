# How to Run Your Spring Boot Application Using Docker Compose

## Summary of the Application
This app implements a payment account system using a double ledger approach to manage credit and debit transactions. The authentication uses JWT for secure access and an API key approach for programmatic use on third-party applications.

### Key Features:
1. **Payment Account**: Allows users to create multiple payment accounts, each potentially for a separate currency.
2. **Ledger System**: Manages the credit and debit transactions.
3. **Transactions**: Enables deposits and transfers.

### Usage Instructions:
**Run the application.**
**Create an account.**
**Make a deposit to your account.**
**Perform a transfer using the system account number.**
   1. You can get the account number as part of the response after signup or by calling the Get User API to retrieve user account information.

## Prerequisites
Before you begin, ensure you have the following installed on your local machine:

1. **Git**: Java Development Kit (JDK) 11 or higher
2. **Docker**: Apache Maven (for building the project)
3. **Git**: (for cloning the repository)Ensure Git is installed on your machine.
4. **Docker** (optional, if you want to run this app with Docker) Ensure Docker and Docker Compose are installed on your machine.

## Steps to Run the Application

### 1. Clone the Repository
Open a terminal and run the following command to clone your repository:

`git clone https://github.com/znuta/onebank.git`

Navigate into the cloned repository:

`cd onebank`

### Building the Project
Ensure you are in the root directory of the cloned repository.

Run the following command to build the project using Maven:

`mvn clean install`

This command will clean any previous builds and install the required dependencies.

## Running the Application
### 1. Using Maven

After the build is complete, run the following command to start the application:

`mvn spring-boot:run`


### 2. Build the Docker Images
If you have a Dockerfile in your project, you might want to build the Docker images first. You can do this by running:

`docker-compose build` 

### 3. Run the Docker Containers
Use Docker Compose to start the containers for your application and database:

 `docker-compose up`

### 4. API documentation with swagger
on your browser goto:
 ```bash
http://127.0.0.1:8080/swagger-ui/index.html#
