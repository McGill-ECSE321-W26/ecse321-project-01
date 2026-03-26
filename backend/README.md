# Getting started

## Prerequisites

Before you can build and run this project, ensure you have the following software installed on your system:

- [Oracle OpenJDK 21.0.2](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [Docker](https://www.docker.com/)

## Database setup

Make sure Docker Engine is running before this step. The database connection is opened through containerized
PostgreSQL database. To create the Docker image and run the container for the first time, from the root of the 
Spring Boot project directory (`backend`), run

```shell
docker compose up -d
```

A running instance of the container should be opened on port `5432` (default PostgreSQL port). However, if you already
you a local installation of postgres on your computer, you may need to change the port mapping so that it doesn't
conflict with the default local postgres port. The details are in `docker-compose.yml`.

## Usage

### Database

If you have just run `docker compose up -d`, the Docker container `postgres-database` should already be running.
However, subsequently, once the Docker Engine is running, you can simply start the Docker container that was created by running
```shell
docker start postgres-database
```

### Gradle Wrapper

Make sure Oracle OpenJDK 21.0.2 is properly configured in the `JAVA_HOME` environment variable before trying this
method. To run the application using the Gradle Wrapper, execute the following command:

```shell
./gradlew bootRun
```

A local Tomcat server should be open on localhost at port `8080`.

## Running Umple

To run Umple, run this command from the root directory of the Spring Boot project:

```shell
java -jar src/main/java/umple.jar -g Java src/main/java/model.ump
```
