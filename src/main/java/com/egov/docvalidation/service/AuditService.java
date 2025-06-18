package com.egov.docvalidation.service;

public interface AuditService {
    void logAction(String userId, String actionType, String ip);
}
