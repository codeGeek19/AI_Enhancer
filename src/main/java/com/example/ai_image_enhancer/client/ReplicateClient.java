package com.example.ai_image_enhancer.client;

import com.example.ai_image_enhancer.exception.AIServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class ReplicateClient {

    private final RestClient restClient;

    @Value("${replicate.api.token}")
    private String apiToken;

    public ReplicateClient(
            @Value("${replicate.api.url}") String apiUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    public String enhanceImage(String imageDataUrl) {

        Map<String, Object> input = Map.of(
                "image", imageDataUrl,
                "enhance_model", "Standard V2",
                "upscale_factor", "2x",
                "output_format", "jpg",
                "face_enhancement", false,
                "subject_detection", "None",
                "face_enhancement_strength", 0.8,
                "face_enhancement_creativity", 0.0
        );

        Map<String, Object> body = Map.of(
                "input", input
        );

        try {

            Map response = restClient.post()
                    .uri(
                            "/models/topazlabs/image-upscale/predictions"
                    )
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + apiToken
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            System.out.println("Replicate response:");
            System.out.println(response);

            if (response == null) {

                throw new AIServiceException(
                        "Replicate returned an empty response"
                );
            }

            Object idObject = response.get("id");

            if (idObject == null) {

                throw new AIServiceException(
                        "Replicate did not return a prediction ID. Response: "
                                + response
                );
            }

            String predictionId =
                    idObject.toString();

            return waitForResult(predictionId);

        } catch (AIServiceException exception) {

            throw exception;

        } catch (Exception exception) {

            exception.printStackTrace();

            throw new AIServiceException(
                    "Replicate error: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    private String waitForResult(
            String predictionId) {

        for (int i = 0; i < 30; i++) {

            try {

                Map response = restClient.get()
                        .uri(
                                "/predictions/"
                                        + predictionId
                        )
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + apiToken
                        )
                        .retrieve()
                        .body(Map.class);

                System.out.println(
                        "Prediction status: "
                                + response
                );

                if (response == null) {

                    throw new AIServiceException(
                            "Empty response while checking prediction"
                    );
                }

                Object statusObject =
                        response.get("status");

                if (statusObject == null) {

                    throw new AIServiceException(
                            "Replicate response has no status"
                    );
                }

                String status =
                        statusObject.toString();

                if ("succeeded".equals(status)) {

                    Object output =
                            response.get("output");

                    if (output == null) {

                        throw new AIServiceException(
                                "Prediction succeeded but output is null"
                        );
                    }

                    System.out.println(
                            "Replicate output: "
                                    + output
                    );

                    /*
                     * Topaz normally returns a URL string.
                     * This also handles a list just in case
                     * the API returns multiple outputs.
                     */
                    if (output instanceof String) {

                        return output.toString();
                    }

                    if (output instanceof List<?> outputList
                            && !outputList.isEmpty()) {

                        return outputList
                                .get(0)
                                .toString();
                    }

                    throw new AIServiceException(
                            "Unexpected output format from Replicate: "
                                    + output
                    );
                }

                if ("failed".equals(status)) {

                    Object error =
                            response.get("error");

                    throw new AIServiceException(
                            "Replicate prediction failed: "
                                    + error
                    );
                }

                if ("canceled".equals(status)) {

                    throw new AIServiceException(
                            "Replicate prediction was canceled"
                    );
                }

                Thread.sleep(2000);

            } catch (InterruptedException exception) {

                Thread.currentThread().interrupt();

                throw new AIServiceException(
                        "Image processing was interrupted",
                        exception
                );

            } catch (AIServiceException exception) {

                throw exception;

            } catch (Exception exception) {

                throw new AIServiceException(
                        "Error while checking Replicate prediction: "
                                + exception.getMessage(),
                        exception
                );
            }
        }

        throw new AIServiceException(
                "Replicate prediction timed out"
        );
    }
}