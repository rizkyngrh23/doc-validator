package com.egov.docvalidation.service;

import com.egov.docvalidation.entity.User;
import com.egov.docvalidation.entity.ValidationLog;
import com.egov.docvalidation.repository.ValidationLogRepository;
import com.egov.docvalidation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditServiceImpl implements AuditService {
    @Autowired
    private ValidationLogRepository validationLogRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void logAction(String userId, String actionType, String ip) {
        Long id = null;
        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            return;
        }
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return;
        ValidationLog log = ValidationLog.builder()
                .actor(user)
                .action(actionType + " (ip: " + ip + ")")
                .timestamp(Instant.now())
                .build();
        validationLogRepository.save(log);
    }
}