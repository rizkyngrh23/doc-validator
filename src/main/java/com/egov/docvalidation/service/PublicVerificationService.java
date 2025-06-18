package com.egov.docvalidation.service;

import com.egov.docvalidation.entity.Document;
import com.egov.docvalidation.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class PublicVerificationService {
    @Autowired
    private DocumentRepository documentRepository;

    public Optional<Document> verifyDocument(UUID docId) {
        return documentRepository.findById(docId);
    }
}
