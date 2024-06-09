# How to Run Your Spring Boot Application Using Docker Compose

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
