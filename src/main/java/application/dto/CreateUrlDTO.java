package application.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public class CreateUrlDTO {
    @NotBlank(message = "Long url can not be empty!!")
    private String longUrl;
     
    private Instant expiresAt;

    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}