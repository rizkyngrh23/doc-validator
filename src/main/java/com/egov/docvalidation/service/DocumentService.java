package com.egov.docvalidation.service;

import com.egov.docvalidation.entity.Document;
import com.egov.docvalidation.entity.User;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

public interface DocumentService {
    Document uploadDocument(MultipartFile file, String metadata, User owner);
    List<Document> getDocumentsByUser(Long userId);
    byte[] downloadDocument(UUID documentId);
}
