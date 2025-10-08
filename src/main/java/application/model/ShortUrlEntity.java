package application.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "short_links", indexes = {
        @Index(name = "idx_code_unique", columnList = "code", unique = true)
})
public class ShortUrlEntity {

    @Id
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = true, length = 16, unique = true)
    private String code;

    @Column(nullable = false, columnDefinition = "text")
    private String longUrl;

    private Instant expiresAt;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private long clickCount = 0L;
    
    public void setId(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public long getClickCount(){
        return clickCount;
    }

    public void setClickCount(long clickCount){
        this.clickCount = clickCount;
    }

    public Instant getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt){
        this.createdAt = createdAt;
    }
}