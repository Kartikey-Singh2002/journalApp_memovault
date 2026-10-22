# MEMO Vault

A Spring Boot journal application for creating and managing personal journal entries. It uses MongoDB to store data and Spring Security for application security.

## Features

- Create and manage journal entries
- MongoDB database integration
- Spring Security configuration

## Requirements

- Java
- Maven
- MongoDB database

## Configuration

Set the `MONGODB_URI` environment variable to your MongoDB connection string before starting the application.

The connection string should include your MongoDB username, password, cluster address, and database name. Keep it private—do not commit it to GitHub.

## Run locally

```bash
./mvnw spring-boot:run
