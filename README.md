
# Portfolio Tracker Application

## Overview
This is a full-stack portfolio tracker application where users can add, view, edit, and delete stock holdings. It fetches real-time stock prices and calculates profit or loss based on the current stock price and initial buy price.

## Technologies Used
- **Frontend**: React
- **Backend**: Spring Boot (Java)
- **Database**: MySQL
- **Stock API**: Alpha Vantage API for real-time stock prices
- **Deployment**: [Vercel] (Frontend), [Render] (Backend),[aiven](db hosting)

## Features
- Add, edit, delete stock holdings.
- View real-time stock prices.
- Calculate and display profit or loss.
- Color-coded profit/loss visualization (green for profit, red for loss).
- Portfolio metrics: total value, top stock, portfolio distribution.

---

## Steps to Run the Project Locally

### Prerequisites
- [Node.js](https://nodejs.org/) (for running the frontend)
- [MySQL](https://www.mysql.com/) (for the database)
- [Java 11+](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) (for running Spring Boot backend)
- [Maven](https://maven.apache.org/) (for building the Spring Boot application)

### Backend (Spring Boot)

1. **Clone the repository**:
    ```bash
    git clone https://github.com/repo-name/portfolio-tracker.git
    cd portfolio-tracker/backend
    ```

2. **Configure MySQL**:
    - Create a database for the project.
    - Update the `application.properties` file with your database credentials:
      ```properties
      spring.datasource.url=jdbc:mysql://localhost:3306/your_database
      spring.datasource.username=your_username
      spring.datasource.password=your_password
      ```

3. **Run the backend**:
    - Build and run the Spring Boot application:
      ```bash
      mvn clean install
      mvn spring-boot:run
      ```
    - The backend will run on `http://localhost:8080`.

### Frontend (React)

1. **Navigate to the frontend folder**:
    ```bash
    cd portfolio-tracker/frontend
    ```

2. **Install dependencies**:
    ```bash
    npm install
    ```

3. **Start the frontend**:
    ```bash
    npm start
    ```
    - The frontend will be available at `http://localhost:3000`.

---

## Assumptions or Limitations

- **Alpha Vantage API Rate Limiting**: The free tier of the Alpha Vantage API has a limit on the number of API requests per minute. If this limit is exceeded, real-time stock data updates may fail. Consider upgrading the API plan for production environments.
- **VPN Required for IP Rate Limiting**: When running the project locally, you may need to use a VPN to avoid rate-limiting based on IP addresses, especially if making frequent API requests.
- **No Authentication**: The app currently doesn't support user authentication. All users have access to the same portfolio data.
- **Caching**: Stock price updates are cached to reduce API calls. However, this may result in slightly outdated prices if the API isn't called frequently.

---

## Deployment Links

- **Frontend**: [Link to deployed frontend (Vercel)](https://growtrack-six.vercel.app/)
- **Backend**: [Link to deployed backend (Render)](https://spring-capx.onrender.com)

## API Documentation
- **Swagger API Docs** (if applicable): [Link to API documentation](https://www.alphavantage.co/documentation/)
