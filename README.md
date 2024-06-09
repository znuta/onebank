# How to Run Your Spring Boot Application Using Docker Compose

## Prerequisites
1. **Git**: Ensure Git is installed on your machine. You can download it from [here](https://git-scm.com/downloads).
2. **Docker**: Ensure Docker and Docker Compose are installed on your machine. You can download Docker from [here](https://www.docker.com/products/docker-desktop).

## Steps to Run the Application

### 1. Clone the Repository
Open a terminal and run the following command to clone your repository:

git clone https://github.com/yourusername/your-repo.git 

Navigate into the cloned repository:

cd your-repo

### 2. Build the Docker Images
If you have a Dockerfile in your project, you might want to build the Docker images first. You can do this by running:
docker-compose build

### 3. Run the Docker Containers
Use Docker Compose to start the containers for your application and database:
docker-compose up
