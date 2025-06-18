package com.egov.docvalidation.controller;

import com.egov.docvalidation.entity.Document;
import com.egov.docvalidation.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/validation")
public class ValidationController {
    @Autowired
    private ValidationService validationService;

    @PostMapping("/approve/{docId}")
    public String approveDocument(@PathVariable("docId") UUID docId, @RequestParam("reviewerId") String reviewerId) {
        validationService.approveDocument(docId, reviewerId);
        return "Approved";
    }

    @PostMapping("/reject/{docId}")
    public String rejectDocument(@PathVariable("docId") UUID docId, @RequestParam("reason") String reason) {
        validationService.rejectDocument(docId, reason);
        return "Rejected";
    }

    @GetMapping("/pending")
    public List<Document> getPendingDocuments() {
        return validationService.getPendingDocuments();
    }
}
