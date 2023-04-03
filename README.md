# Spring boot integration test demo project with Kotlin, Reactor and MongoDB

This is a demo project about how to write integration tests against a Kotlin Spring Boot reactive web application backed by MongoDB.

## Requirements
 - Java 17
 - Maven
 - Docker

## Running the application
You have to run the docker-compose file from the `dev` folder. 
It starts a containerized MongoDB instance, creates the database and the default user.
It also starts a mongo-express instance for debugging purposes.

You can run the application simply from IntelliJ IDEA, the entry point is the `ItTestDemoApplication#main` method.
You have to set the MongoDB connection URL with the `MONGO_DB_CONNECTION_STRING` environment variable.

For local development, it can be
```properties
MONGO_DB_CONNECTION_STRING=mongodb://root:example@localhost:27017/personDB?authSource=admin
```

## Testing
You can run the integration tests with the `mvn test` command or from the IDE.
`Testcontainers` is used to run the MongoDB Docker container for the integration tests.