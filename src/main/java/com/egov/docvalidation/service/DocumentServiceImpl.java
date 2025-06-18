package com.egov.docvalidation.service;

import com.egov.docvalidation.entity.Document;
import com.egov.docvalidation.entity.User;
import com.egov.docvalidation.repository.DocumentRepository;
import com.egov.docvalidation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    private final String uploadDir = "uploads";

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Override
    public Document uploadDocument(MultipartFile file, String metadata, User owner) {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());
            Document doc = Document.builder()
                    .filePath(filePath.toString())
                    .metadata(metadata)
                    .status("PENDING")
                    .owner(owner)
                    .build();
            return documentRepository.save(doc);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    @Override
    public List<Document> getDocumentsByUser(Long userId) {
        logger.info("Fetching documents for userId={}", userId);
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found for userId={}", userId);
            return List.of();
        }
        return documentRepository.findByOwner(userOpt.get());
    }

    @Override
    public byte[] downloadDocument(UUID documentId) {
        Optional<Document> docOpt = documentRepository.findById(documentId);
        if (docOpt.isEmpty()) {
            throw new RuntimeException("Document not found");
        }
        try {
            return Files.readAllBytes(Paths.get(docOpt.get().getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("File read failed", e);
        }
    }
}
