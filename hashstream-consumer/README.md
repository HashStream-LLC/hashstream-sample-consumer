# lambda-spring-boot-app Documentation

## Overview

This project is a Spring Boot application designed to run on AWS Lambda which can handle notifications from HashStream's hedera smart contract streaming service.

## Project Structure

The following sample project was used as inspiration to this project setup: <https://github.com/aws/serverless-java-container/blob/main/samples/springboot3/pet-store>


## Dependencies

The project includes the following dependencies in the `build.gradle` file:

## Getting Started

To run the application, use the Gradle wrapper scripts provided in the project. You can build and run the application using the following commands:

```bash
./gradlew build
./gradlew bootRun
```

## Testing

Unit tests can be executed using the following command:

```bash
./gradlew test
```

This will run all tests defined in the `src/test` directory.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
