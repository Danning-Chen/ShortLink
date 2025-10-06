package application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import application.dto.CreateUrlDTO;
import application.model.ShortUrlEntity;
import application.repository.ShortUrlRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ShortUrlServiceTest {
    @Test
    void create_generatesCodeFromId_andSavesTwice() {
        ShortUrlRepository repo = mock(ShortUrlRepository.class);
        ShortUrlService svc = new ShortUrlService(repo);

        // 1st save: returns entity with generated id=125
        when(repo.save(any(ShortUrlEntity.class)))
                .thenAnswer(inv -> {
                    ShortUrlEntity s = inv.getArgument(0);
                    // simulate DB assigning id
                    var withId = new ShortUrlEntity();
                    withId.setLongUrl(s.getLongUrl());
                    withId.setEnabled(true);
                    withId.setExpiresAt(s.getExpiresAt());
                    // reflect generated id
                    try {
                        var f = ShortUrlEntity.class.getDeclaredField("id");
                        f.setAccessible(true);
                        f.set(withId, 125L);
                    } catch (Exception ignored) {
                    }
                    return withId;
                })
                // 2nd save: returns updated entity (with code)
                .thenAnswer(inv -> inv.getArgument(0));

        CreateUrlDTO req = new CreateUrlDTO();
        req.setLongUrl("https://Example.com/Hello");
        req.setExpiresAt(null);

        ShortUrlEntity saved = svc.create(req);

        // verify code is Base62(id=125) == "21"
        assertThat(saved.getCode()).isEqualTo(Base62.encode(125L)); // "21"
        assertThat(saved.getLongUrl()).isEqualTo("https://example.com/Hello"); // normalized
        assertThat(saved.getExpiresAt()).isNull();
        assertThat(saved.getEnabled()).isTrue();

        verify(repo, times(2)).save(any(ShortUrlEntity.class));

        // also capture 2nd save to check code set
        ArgumentCaptor<ShortUrlEntity> cap = ArgumentCaptor.forClass(ShortUrlEntity.class);
        verify(repo, times(2)).save(cap.capture());
        assertThat(cap.getAllValues().get(1).getCode()).isEqualTo("21");
    }
}
