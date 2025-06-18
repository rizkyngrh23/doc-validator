package com.egov.docvalidation.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID docId;

    @Column(nullable = false)
    private String filePath;

    @Column(length = 2048)
    private String metadata;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public Document() {}
    public Document(UUID docId, String filePath, String metadata, String status, User owner) {
        this.docId = docId;
        this.filePath = filePath;
        this.metadata = metadata;
        this.status = status;
        this.owner = owner;
    }
    public UUID getDocId() { return docId; }
    public void setDocId(UUID docId) { this.docId = docId; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private UUID docId;
        private String filePath;
        private String metadata;
        private String status;
        private User owner;
        public Builder docId(UUID docId) { this.docId = docId; return this; }
        public Builder filePath(String filePath) { this.filePath = filePath; return this; }
        public Builder metadata(String metadata) { this.metadata = metadata; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder owner(User owner) { this.owner = owner; return this; }
        public Document build() { return new Document(docId, filePath, metadata, status, owner); }
    }
}
