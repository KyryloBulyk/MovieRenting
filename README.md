# 🎬 Rent-a-Movie

Welcome to the Rent-a-Movie project! This application is built with Quarkus and React, providing users with the ability to rent movies, search for movies, and rate them.

## 🚀 Getting Started

To get the project up and running, follow these steps:

### Prerequisites

Make sure you have the following installed:
- Docker or Podman
- Java 17
- Maven

### Project Structure

The project is divided into two main parts:
1. **Backend**: Built with Quarkus
2. **Frontend**: Built with React

### Running the Project

1. **Clone the repository**:

    ```sh
    git clone https://github.com/your-username/rent-a-movie.git
    cd rent-a-movie
    ```

2. **Start the services**:

   Navigate to the `utils` directory and start the services using Docker Compose or Podman Compose:

    ```sh
    cd utils
    docker-compose up -d
    # or
    podman-compose up -d
    ```

3. **Backend Setup**:

   Navigate to the backend directory and start the Quarkus application:

    ```sh
    cd backend
    ./mvnw quarkus:dev
    ```

4. **Frontend Setup**:

   Navigate to the frontend directory, install dependencies, and start the React application:

    ```sh
    cd frontend
    npm install
    npm start
    ```

## 🛠️ Project Components

### Backend (Quarkus)

- **Movie Management**: Rent, search, and rate movies.
- **User Management**: Manage user details and their rented movies.
- **Database**: Uses PostgreSQL for storing movie and user data.
- **APIs**: Exposes RESTful endpoints for movie and user operations.

### Frontend (React)

- **Search Movies**: Allows users to search for movies.
- **Rent Movies**: Users can rent movies from the available list.
- **Rate Movies**: Users can rate the movies they have watched.

## 📚 API Documentation

API documentation is automatically generated and can be accessed at `http://localhost:8080/q/swagger-ui` once the backend is running.

## 🐳 Docker & Podman

We use Docker and Podman for containerization. To manage and run services like the database, use the following commands:

- **Start services**:

    ```sh
    docker-compose up -d
    # or
    podman-compose up -d
    ```

- **Stop services**:

    ```sh
    docker-compose down
    # or
    podman-compose down
    ```

## 🛡️ Security

- Ensure to secure your database and sensitive data.
- Implement proper authentication and authorization in the future releases.

## 👏 Acknowledgements

- Thanks to the Quarkus and React teams for their awesome frameworks.
- Special thanks to all contributors and supporters of this project.

## 📬 Contact

If you have any questions, feel free to open an issue or contact us at [kyrylo.bulyk@gmail.com].