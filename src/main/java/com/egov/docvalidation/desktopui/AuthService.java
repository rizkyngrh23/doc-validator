package com.egov.docvalidation.desktopui;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthService {
    public static String login(String email, String password) throws IOException {
        URL url = new URL("http://localhost:8080/api/auth/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        String json = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        int code = conn.getResponseCode();
        if (code == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                return sb.toString();
            }
        } else {
            throw new IOException("Login failed: " + code);
        }
    }

    public static String register(String email, String password) throws IOException {
        URL url = new URL("http://localhost:8080/api/auth/register");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        String json = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        int code = conn.getResponseCode();
        if (code == 200 || code == 201) {
            return "Registration successful!";
        } else {
            throw new IOException("Registration failed: " + code);
        }
    }
}
