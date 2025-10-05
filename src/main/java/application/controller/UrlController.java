package application.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public class UrlController {
    
    @PostMapping("create-url")
    public ResponseEntity<?> createUrl() throws IOException {
        
        return ResponseEntity.ok("A");
    }

    @GetMapping("/{short-url}")
    public ResponseEntity<?> redirect() throws IOException {

        return ResponseEntity.ok("a");
    }
}
