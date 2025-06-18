package com.egov.docvalidation.desktopui;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class VerificationService {
    public static String verifyDocument(String docId, String jwt) throws IOException {
        URL url = new URL("http://localhost:8080/api/verify/" + docId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        if (jwt != null && !jwt.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + jwt);
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
            throw new IOException("Verification failed: " + code);
        }
    }
}
