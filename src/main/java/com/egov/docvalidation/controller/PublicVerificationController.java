package com.egov.docvalidation.controller;

import com.egov.docvalidation.entity.Document;
import com.egov.docvalidation.service.PublicVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/verify")
public class PublicVerificationController {
    @Autowired
    private PublicVerificationService publicVerificationService;

    @GetMapping("/{docId}")
    public String verifyDocument(@PathVariable("docId") String docId) {
        Optional<Document> docOpt = publicVerificationService.verifyDocument(UUID.fromString(docId));
        if (docOpt.isPresent()) {
            Document doc = docOpt.get();
            return "Document Status: " + doc.getStatus();
        } else {
            return "Document not found";
        }
    }
}
