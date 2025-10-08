package application.repository;

import application.model.ShortUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrlEntity, Long> {
    Optional<ShortUrlEntity> findByCode(String code);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ShortUrlEntity u set u.clickCount = u.clickCount + 1 where u.code = :code")
    int incrementClick(@Param("code") String code);
}