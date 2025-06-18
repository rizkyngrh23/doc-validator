package com.egov.docvalidation.service;

import com.egov.docvalidation.entity.Document;
import java.util.List;
import java.util.UUID;

public interface ValidationService {
    void approveDocument(UUID documentId, String reviewerId);
    void rejectDocument(UUID documentId, String reason);
    List<Document> getPendingDocuments();
}
