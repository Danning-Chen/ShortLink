package application.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "short_links", indexes = {
        @Index(name = "idx_code_unique", columnList = "code", unique = true)
})
public class ShortUrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 16, unique = true)
    private String code; // 由 id -> Base62 生成

    @Column(nullable = false, columnDefinition = "text")
    private String longUrl;

    private Instant expiresAt; // null = 永不过期

    @Column(nullable = false)
    private Boolean enabled = true;

    // getter/setter
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}