package net.tokensmith.repository.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 12/1/16.
 */
public class TokenLeadToken {
    private UUID id;
    private UUID tokenId;
    private UUID leadTokenId;
    private OffsetDateTime createdAt;

    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTokenId() {
        return tokenId;
    }

    public void setTokenId(UUID tokenId) {
        this.tokenId = tokenId;
    }

    public UUID getLeadTokenId() {
        return leadTokenId;
    }

    public void setLeadTokenId(UUID leadTokenId) {
        this.leadTokenId = leadTokenId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
