package com.egov.docvalidation.service;

import com.egov.docvalidation.entity.Document;
import com.egov.docvalidation.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ValidationServiceImpl implements ValidationService {
    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public void approveDocument(UUID documentId, String reviewerId) {
        Document doc = documentRepository.findById(documentId).orElseThrow();
        doc.setStatus("APPROVED");
        documentRepository.save(doc);
    }

    @Override
    public void rejectDocument(UUID documentId, String reason) {
        Document doc = documentRepository.findById(documentId).orElseThrow();
        doc.setStatus("REJECTED: " + reason);
        documentRepository.save(doc);
    }

    @Override
    public List<Document> getPendingDocuments() {
        return documentRepository.findAll().stream()
                .filter(d -> "PENDING".equalsIgnoreCase(d.getStatus()))
                .toList();
    }
}
