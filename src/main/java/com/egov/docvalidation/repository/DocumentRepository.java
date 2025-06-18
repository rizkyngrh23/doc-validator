package com.egov.docvalidation.repository;

import com.egov.docvalidation.entity.Document;
import com.egov.docvalidation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByOwner(User owner);
}
