package com.egov.docvalidation.audit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.egov.docvalidation.service.AuditService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuditAspect {
    @Autowired
    private AuditService auditService;

    @AfterReturning("execution(* com.egov.docvalidation.service.*.*(..)) && !execution(* com.egov.docvalidation.service.AuditService.logAction(..))")
    public void logAction(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : "anonymous";
        String actionType = joinPoint.getSignature().toShortString();
        String ip = "unknown";
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            ip = request.getRemoteAddr();
        }
        auditService.logAction(userId, actionType, ip);
    }
}
