# AI Image Enhancer

A Spring Boot REST API that accepts an image, sends it to a pretrained AI image-upscaling model through Replicate, and returns the enhanced image URL.

## Features

* Upload an image through a REST API
* Validate uploaded image files
* Store enhancement request metadata in PostgreSQL
* Send images to an AI image-enhancement model through Replicate
* Handle asynchronous AI predictions by polling for completion
* Track request status as `PROCESSING`, `COMPLETED`, or `FAILED`
* Global exception handling for invalid images and AI-service failures
* Simple web interface for uploading and viewing enhanced images

## Tech Stack

* **Java**
* **Spring Boot**
* **Spring Data JPA / Hibernate**
* **PostgreSQL**
* **REST API**
* **Replicate API**
* **HTML, CSS, JavaScript**
* **Maven**

## Architecture

```text
Browser
   |
   | POST /api/images/enhance
   ↓
ImageEnhancerController
   ↓
ImageEnhancerService
   ├── EnhancementRequestRepository
   │          ↓
   │      PostgreSQL
   │
   └── ReplicateClient
              ↓
        Replicate API
              ↓
      AI Image Enhancement
              ↓
        Enhanced Image URL
```

## Request Flow

1. The user selects an image.
2. The frontend sends the image as a multipart request.
3. `ImageEnhancerController` receives the request.
4. `ImageEnhancerService` validates the image.
5. Request metadata is stored in PostgreSQL.
6. The image is converted into a Base64 data URL.
7. `ReplicateClient` sends the image to the AI model.
8. Replicate returns a prediction ID.
9. The application polls the prediction until it succeeds or fails.
10. The enhanced image URL is returned to the frontend.
11. The frontend displays the enhanced image.

## API

### Enhance Image

```http
POST /api/images/enhance
```

Request:

```text
Content-Type: multipart/form-data
```

Form parameter:

```text
image: <image file>
```

Example using cURL:

```bash
curl -X POST http://localhost:8080/api/images/enhance \
  -F "image=@image.jpg"
```

Example successful response:

```json
{
  "requestId": 1,
  "status": "COMPLETED",
  "enhancedImageUrl": "https://...",
  "processingTimeMs": 8420
}
```

## Database

The application stores enhancement request metadata in the `enhancement_requests` table.

Stored information includes:

* Original file name
* Content type
* File size
* Processing status
* Processing time
* Request creation time

The actual image is processed through the AI service rather than being stored directly in PostgreSQL.

## Exception Handling

The application uses custom exceptions and `@RestControllerAdvice` for centralized error handling.

Examples:

* Invalid or missing image → `400 Bad Request`
* AI service failure → `502 Bad Gateway`
* Unexpected server error → `500 Internal Server Error`

## Configuration

Create the required environment variable:

```text
REPLICATE_API_TOKEN
```

The token is intentionally **not stored in the source code**.

Example `application.properties` configuration:

```properties
replicate.api.url=https://api.replicate.com/v1
replicate.api.token=${REPLICATE_API_TOKEN}
```

You also need a PostgreSQL database configured in `application.properties`.

## Running the Project

Clone the repository:

```bash
git clone https://github.com/codeGeek19/AI_Enhancer.git
```

Go into the project:

```bash
cd AI_Enhancer
```

Run the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Then open:

```text
http://localhost:8080
```

## Project Structure

```text
src
└── main
    ├── java
    │   └── com.example.ai_image_enhancer
    │       ├── client
    │       │   └── ReplicateClient.java
    │       ├── controller
    │       │   └── ImageEnhancerController.java
    │       ├── dto
    │       │   └── EnhancementResponse.java
    │       ├── entity
    │       │   └── EnhancementRequest.java
    │       ├── exception
    │       │   ├── AIServiceException.java
    │       │   ├── GlobalExceptionHandler.java
    │       │   └── InvalidImageException.java
    │       ├── repository
    │       │   └── EnhancementRequestRepository.java
    │       └── service
    │           └── ImageEnhancerService.java
    │
    └── resources
        ├── application.properties
        └── static
            └── index.html
```

## Future Improvements

* Authentication and authorization
* Persistent image storage using object storage
* Support for larger image files
* Background job processing instead of synchronous polling
* Enhancement history API
* Rate limiting
* Automated tests for API and service layers
