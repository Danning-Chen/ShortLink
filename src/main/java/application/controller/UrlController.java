package application.controller;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import application.dto.CreateUrlDTO;
import application.model.ShortUrlEntity;
import application.service.ShortUrlService;
import jakarta.validation.Valid;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class UrlController {

    private final ShortUrlService service;

    @Autowired
    public UrlController(ShortUrlService service) {
        this.service = service;
    }

    @PostMapping("create")
    public ResponseEntity<?> createUrl(@Valid @RequestBody CreateUrlDTO request) throws IOException {

        ShortUrlEntity s = service.create(request);
        String shortUrl = "http://localhost:8080/" + s.getCode();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", s.getCode());
        body.put("shortUrl", shortUrl);
        body.put("expiresAt", s.getExpiresAt()); // 可以为 null
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> redirect(@PathVariable String shortUrl) throws IOException {

        ShortUrlEntity s = service.find(shortUrl);
        if (s == null || !s.getEnabled()) {
            return ResponseEntity.notFound().build();
        }

        if (s.getExpiresAt() != null && s.getExpiresAt().isBefore(Instant.now())) {
            return ResponseEntity.status(HttpStatus.GONE).body("短链已过期");
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(s.getLongUrl()))
                .build();
    }
}
