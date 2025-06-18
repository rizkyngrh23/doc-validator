package com.egov.docvalidation.controller;

import com.egov.docvalidation.entity.Document;
import com.egov.docvalidation.entity.User;
import com.egov.docvalidation.service.DocumentService;
import com.egov.docvalidation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.Principal;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;
    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @PostMapping("/upload")
    public String uploadDocument(@RequestParam("file") MultipartFile file,
                                 @RequestParam("metadata") String metadata,
                                 @RequestParam("userId") String userId) {
        Long id;
        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            logger.warn("Invalid userId: {}", userId);
            return "Invalid userId";
        }
        logger.info("Received upload request: userId={}, filename={}, metadata={}", userId, file.getOriginalFilename(), metadata);
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            logger.warn("User not found: {}", userId);
            return "User not found";
        }
        documentService.uploadDocument(file, metadata, userOpt.get());
        logger.info("File uploaded successfully for userId={}", userId);
        return "Uploaded";
    }

    @GetMapping("/user/{userId}")
    public List<Document> getDocumentsByUser(@PathVariable("userId") String userId, Principal principal) {
        if (principal == null || !principal.getName().equals(userId)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "You can only access your own documents");
        }
        Long id;
        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            return List.of();
        }
        return documentService.getDocumentsByUser(id);
    }

    @GetMapping("/download/{docId}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable("docId") UUID docId) {
        byte[] fileData = documentService.downloadDocument(docId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=downloaded_file")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }
}
