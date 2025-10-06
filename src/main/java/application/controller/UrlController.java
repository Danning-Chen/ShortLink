package application.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import application.dto.CreateUrlDTO;
import application.model.ShortUrlEntity;
import application.service.ShortUrlService;
import jakarta.validation.Valid;
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
        return ResponseEntity.ok(Map.of(
                "code", s.getCode(),
                "shortUrl", shortUrl,
                "expiresAt", s.getExpiresAt()
        ));
    }

    @GetMapping("/{short-url}")
    public ResponseEntity<?> redirect() throws IOException {

        return ResponseEntity.ok("a");
    }
}
