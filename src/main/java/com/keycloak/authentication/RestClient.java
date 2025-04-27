package com.keycloak.authentication;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.jboss.logging.Logger;

public class RestClient {
  private static final Logger log = Logger.getLogger(RestClient.class);
  private static final String SAB_BACKEND_BASE_URL = "SAB_BACKEND_BASE_URL";
  private static final String VERIFY_PATH = "/api/v1/codes/verify";

  public static int sendRequest(String pincode) throws IOException {
    try {
      String apiUrl = System.getenv(SAB_BACKEND_BASE_URL);
      log.infof("SAB_BACKEND_BASE_URL: %s", apiUrl);

      if (apiUrl == null || apiUrl.isEmpty()) {
        throw new IllegalArgumentException(
            "Environment variable SAB_BACKEND_BASE_URL is not set or is empty.");
      }

      apiUrl = apiUrl + VERIFY_PATH + "/" + pincode;

      URI uri = URI.create(apiUrl);
      HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(uri)
              .timeout(Duration.ofSeconds(10))
              .header("Content-Type", "application/json; utf-8")
              .POST(HttpRequest.BodyPublishers.noBody())
              .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      return response.statusCode();
    } catch (IOException | InterruptedException e) {
      throw new IOException("Failed to post service: " + e.getMessage(), e);
    }
  }
}
