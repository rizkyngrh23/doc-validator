package com.egov.docvalidation.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "validation_logs")
public class ValidationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_id")
    private Document doc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private Instant timestamp;

    public ValidationLog() {
    }

    public ValidationLog(UUID logId, Document doc, User actor, String action, Instant timestamp) {
        this.logId = logId;
        this.doc = doc;
        this.actor = actor;
        this.action = action;
        this.timestamp = timestamp;
    }

    public UUID getLogId() {
        return logId;
    }

    public void setLogId(UUID logId) {
        this.logId = logId;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public User getActor() {
        return actor;
    }

    public void setActor(User actor) {
        this.actor = actor;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID logId;
        private Document doc;
        private User actor;
        private String action;
        private Instant timestamp;

        public Builder logId(UUID logId) {
            this.logId = logId;
            return this;
        }

        public Builder doc(Document doc) {
            this.doc = doc;
            return this;
        }

        public Builder actor(User actor) {
            this.actor = actor;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ValidationLog build() {
            return new ValidationLog(logId, doc, actor, action, timestamp);
        }
    }
}
