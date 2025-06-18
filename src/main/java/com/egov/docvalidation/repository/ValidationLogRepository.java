package com.egov.docvalidation.repository;

import com.egov.docvalidation.entity.ValidationLog;
import com.egov.docvalidation.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ValidationLogRepository extends JpaRepository<ValidationLog, UUID> {
    List<ValidationLog> findByDoc(Document doc);
}
