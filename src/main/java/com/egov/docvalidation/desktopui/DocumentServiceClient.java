package com.egov.docvalidation.desktopui;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

public class DocumentServiceClient {
    public static List<DocumentDTO> getDocumentsByUser(long userId, String jwt) throws IOException {
        URI uri = URI.create("http://localhost:8080/api/documents/user/" + userId);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + jwt);
        int code = conn.getResponseCode();
        if (code == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                JSONArray arr = new JSONArray(sb.toString());
                List<DocumentDTO> docs = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    docs.add(new DocumentDTO(
                        UUID.fromString(obj.getString("docId")),
                        obj.getString("filePath"),
                        obj.optString("metadata", ""),
                        obj.getString("status")
                    ));
                }
                return docs;
            }
        } else {
            throw new IOException("Failed to get documents: " + code);
        }
    }

    public static String uploadDocument(long userId, String jwt, File file, String metadata) throws IOException {
        String boundary = Long.toHexString(System.currentTimeMillis());
        URI uri = URI.create("http://localhost:8080/api/documents/upload");
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + jwt);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setDoOutput(true);

        try (OutputStream output = conn.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)) {
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"userId\"").append("\r\n\r\n");
            writer.append(String.valueOf(userId)).append("\r\n");
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"metadata\"").append("\r\n\r\n");
            writer.append(metadata == null ? "" : metadata).append("\r\n");
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").append("\r\n");
            writer.append("Content-Type: application/octet-stream").append("\r\n\r\n");
            writer.flush();
            try (FileInputStream inputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                output.flush();
            }
            writer.append("\r\n");
            writer.append("--" + boundary + "--").append("\r\n");
            writer.flush();
        }
        int code = conn.getResponseCode();
        if (code == 200 || code == 201) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                return sb.toString();
            }
        } else {
            throw new IOException("Upload failed: " + code);
        }
    }

    public static void downloadDocument(UUID docId, String jwt, File destination) throws IOException {
        URI uri = URI.create("http://localhost:8080/api/documents/download/" + docId);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + jwt);
        int code = conn.getResponseCode();
        if (code == 200) {
            try (InputStream in = conn.getInputStream();
                 FileOutputStream out = new FileOutputStream(destination)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } else {
            throw new IOException("Download failed: " + code);
        }
    }

    public static String extractUserIdFromJwt(String jwt) {
        if (jwt == null || jwt.isEmpty()) return null;
        String[] parts = jwt.split("\\.");
        if (parts.length < 2) return null;
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        JSONObject obj = new JSONObject(payload);
        return obj.optString("sub", null);
    }

    public static class DocumentDTO {
        public final UUID docId;
        public final String filePath;
        public final String metadata;
        public final String status;
        public DocumentDTO(UUID docId, String filePath, String metadata, String status) {
            this.docId = docId;
            this.filePath = filePath;
            this.metadata = metadata;
            this.status = status;
        }
        @Override
        public String toString() {
            return docId + " | " + filePath + " | " + status;
        }
    }
}
