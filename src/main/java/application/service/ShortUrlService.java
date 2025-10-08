package application.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import application.dto.CreateUrlDTO;
import application.model.ShortUrlEntity;
import application.repository.ShortUrlRepository;
import jakarta.transaction.Transactional;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.time.Duration;

@Service
public class ShortUrlService {
    private final ShortUrlRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    public ShortUrlService(ShortUrlRepository repository, RedisTemplate<String, Object> redisTemplate) {
        this.repository = repository;

        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void increamentClick(String code) {
        repository.incrementClick(code);
    }

    @Transactional
    public ShortUrlEntity create(CreateUrlDTO request) {
        String normalized = normalizeHttpUrl(request.getLongUrl());
        Instant exp = validateExpiry(request.getExpiresAt());

        ShortUrlEntity row = new ShortUrlEntity();
        row.setLongUrl(normalized);
        row.setExpiresAt(exp);
        row.setEnabled(true);
        row = repository.save(row);

        String code = Base62.encode(row.getId());

        row.setCode(code);
        row = repository.save(row);

        String key = "shorturl:" + code;
        Map<String, Object> map = new HashMap<>();
        map.put("code", row.getCode());
        map.put("longUrl", row.getLongUrl());
        map.put("createdAt", row.getCreatedAt() != null ? row.getCreatedAt().toString() : null);
        map.put("expiresAt", row.getExpiresAt() != null ? row.getExpiresAt().toString() : null);
        map.put("enabled", row.isEnabled());
        map.put("clickCount", row.getClickCount());

        redisTemplate.opsForHash().putAll(key, map);


        if (exp != null) {
            long seconds = Duration.between(Instant.now(), exp).getSeconds();
            redisTemplate.expire(key, Duration.ofSeconds(seconds));
        }

        return row;
    }

    public ShortUrlEntity find(String code) {
        String key = "shorturl:" + code;
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);

        if (map != null && !map.isEmpty()) {
            ShortUrlEntity s = new ShortUrlEntity();
            s.setCode(code);
            s.setLongUrl((String) map.get("longUrl"));
            s.setEnabled(Boolean.parseBoolean(String.valueOf(map.get("enabled"))));
            
            s.setClickCount(Long.parseLong(String.valueOf(map.get("clickCount"))));

            String createdAt = (String) map.get("createdAt");
            if (createdAt != null)
                s.setCreatedAt(Instant.parse(createdAt));

            String expiresAt = (String) map.get("expiresAt");
            if (expiresAt != null)
                s.setExpiresAt(Instant.parse(expiresAt));

            return s;
        }

        ShortUrlEntity entity = repository.findByCode(code).orElse(null);
        if (entity != null) {
            Map<String, Object> cache = new HashMap<>();
            cache.put("code", entity.getCode());
            cache.put("longUrl", entity.getLongUrl());
            cache.put("createdAt", entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
            cache.put("expiresAt", entity.getExpiresAt() != null ? entity.getExpiresAt().toString() : null);
            cache.put("enabled", entity.isEnabled());
            cache.put("clickCount", entity.getClickCount());
            redisTemplate.opsForHash().putAll(key, cache);
        }
        return entity;
    }

    private static String normalizeHttpUrl(String raw) {
        URI u = URI.create(raw.trim());

        String scheme = u.getScheme();
        if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException("仅支持 http/https 链接");
        }
        scheme = scheme.toLowerCase();

        String host = u.getHost();
        if (host == null)
            throw new IllegalArgumentException("URL 缺少主机名");
        host = host.toLowerCase(); 

        String userInfo = u.getUserInfo(); 
        int port = u.getPort();
        boolean omitPort = port == -1 ||
                ("http".equals(scheme) && port == 80) ||
                ("https".equals(scheme) && port == 443);

        String path = u.getRawPath() == null ? "" : u.getRawPath();
        String query = u.getRawQuery() == null ? "" : "?" + u.getRawQuery();

        return scheme + "://" +
                (userInfo != null ? userInfo + "@" : "") +
                host +
                (omitPort ? "" : ":" + port) +
                path + query;
    }

    private static Instant validateExpiry(Instant expiresAt) {
        if (expiresAt == null)
            return null;
        if (expiresAt.isBefore(Instant.now()))
            throw new IllegalArgumentException("过期时间必须晚于当前时间");
        return expiresAt;
    }

}
