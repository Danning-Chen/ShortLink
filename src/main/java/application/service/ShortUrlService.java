package application.service;

import org.springframework.stereotype.Service;

import application.dto.CreateUrlDTO;
import application.model.ShortUrlEntity;
import application.repository.ShortUrlRepository;
import jakarta.transaction.Transactional;

import java.net.URI;
import java.time.Instant;

@Service
public class ShortUrlService {
    private final ShortUrlRepository repository;

    public ShortUrlService(ShortUrlRepository repository) {
        this.repository = repository;
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
        return repository.save(row);
    }

    public ShortUrlEntity find(String code){
        return repository.findByCode(code).orElse(null);
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
        host = host.toLowerCase(); // ✅ only host lowercased

        String userInfo = u.getUserInfo(); // keep or reject
        int port = u.getPort();
        boolean omitPort = port == -1 ||
                ("http".equals(scheme) && port == 80) ||
                ("https".equals(scheme) && port == 443);

        String path = u.getRawPath() == null ? "" : u.getRawPath(); // keep case
        String query = u.getRawQuery() == null ? "" : "?" + u.getRawQuery();

        // (optional) block userinfo for security:
        // if (userInfo != null) throw new IllegalArgumentException("不允许带凭据的 URL");

        return scheme + "://" +
                (userInfo != null ? userInfo + "@" : "") +
                host +
                (omitPort ? "" : ":" + port) +
                path + query; // no fragment
    }

    private static Instant validateExpiry(Instant expiresAt) {
        if (expiresAt == null)
            return null; // 永不过期
        if (expiresAt.isBefore(Instant.now()))
            throw new IllegalArgumentException("过期时间必须晚于当前时间");
        return expiresAt;
    }

}
