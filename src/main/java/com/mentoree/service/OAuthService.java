package com.mentoree.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class OAuthService {

    @Value("spring.security.oauth2.client.registration.google.client-id")
    private String google_client_id;
    @Value("spring.security.oauth2.client.registration.google.client-secret")
    private String google_secret;
    @Value("spring.security.oauth2.client.registration.google.redirect-uri")
    private String google_redirect_uri;

    public String getAccessToken(String authorize_code) {
        String access_token = "";
        String reqURL = "https://oauth2.googleapis.com/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();

            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + google_client_id);
            sb.append("&redirect_uri=" + google_redirect_uri);
            sb.append("&client_secret=" + google_secret);
            sb.append("&code=" + authorize_code);
            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while((line = br.readLine()) != null) {
                result += line;
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_token = element.getAsJsonObject().get("access_token").getAsString();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return access_token;
    }

    public String getUserInfo(String id_token) {

        String reqURL = "https://oauth2.googleapis.com/tokeninfo?id_token=" + id_token;
        String email = "";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            email = element.getAsJsonObject().get("email").getAsString();

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return email;
    }

}
