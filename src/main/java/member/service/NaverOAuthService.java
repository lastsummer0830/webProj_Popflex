package member.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import common.config.AppConfig;
import member.dto.NaverProfileDTO;

public class NaverOAuthService {

    private static final String AUTHORIZE_URL = "https://nid.naver.com/oauth2.0/authorize";
    private static final String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private static final String PROFILE_URL = "https://openapi.naver.com/v1/nid/me";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String buildAuthorizeUrl(String state) {
        return AUTHORIZE_URL
                + "?response_type=code"
                + "&client_id=" + encode(AppConfig.getNaverClientId())
                + "&redirect_uri=" + encode(AppConfig.getNaverCallbackUrl())
                + "&state=" + encode(state);
    }

    public String requestAccessToken(String code, String state) {
        String tokenUrl = TOKEN_URL
                + "?grant_type=authorization_code"
                + "&client_id=" + encode(AppConfig.getNaverClientId())
                + "&client_secret=" + encode(AppConfig.getNaverClientSecret())
                + "&code=" + encode(code)
                + "&state=" + encode(state);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .GET()
                .build();

        String body = send(request);
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();

        return getString(json, "access_token");
    }

    public NaverProfileDTO requestProfile(String accessToken) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PROFILE_URL))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        String body = send(request);
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        JsonObject response = json.getAsJsonObject("response");

        if (response == null) {
            throw new IllegalStateException("Naver profile response is empty.");
        }

        NaverProfileDTO profile = new NaverProfileDTO();
        profile.setSocialId(getString(response, "id"));
        profile.setEmail(getString(response, "email"));
        profile.setName(getString(response, "name"));

        return profile;
    }

    private String send(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Naver API request failed. status=" + response.statusCode());
            }

            return response.body();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to call Naver API.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Naver API request was interrupted.", e);
        }
    }

    private String getString(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return null;
        }

        return obj.get(key).getAsString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
