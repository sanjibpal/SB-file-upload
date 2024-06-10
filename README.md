# File Download Engine
FileUploadApplication is a Spring Boot application that uses Spring Batch to download a large ZIP file. The application includes robust error handling to resume the download if interrupted.

## Features
- Download Large Files: Download large ZIP files (5GB+) with retry capabilities.
- Extract ZIP Files: Extract the contents of the ZIP file.
- Process Extracted Files: Split and process the extracted files.
- Web Interface: Start batch jobs via a REST API.

## Requirements
- Java 17
- Gradle
- Spring Boot 3.3.0
- Spring Batch 5.0
- H2 Database (for development and testing)

### Future Phases
- Extract the zip contents
- Process the extracted files


## Build the Project
```
$  ./gradlew clean :engine:build 
```
To build the Docker image
```
$ ./gradlew clean :engine:build :engine:docker 
```

## Running the Application
To run in your local
```
$ java -jar -Dhostname=localhost ./engine/build/libs/engine.jar
```

### Related Documentations

* [Swagger](http://localhost:8082/localhost/sample/swagger-ui.html) - available when the application starts
* [Code Coverage](./engine/build/reports/jacoco/test/html/index.html) - Jacoco code coverage after the build
* [Test Reports](./engine/build/reports/tests/test/index.html) - Test report after the build

