package com.jsonmorberg.ururl.utils;

import com.jsonmorberg.ururl.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    // Find a URL by its shortened code
    Optional<Url> findByShortCode(String shortCode);

    // Find a URL by original URL
    Optional<Url> findByOriginalUrl(String originalUrl);

    // Custom query to delete expired URLs
    @Modifying
    @Transactional
    @Query("DELETE FROM Url u WHERE u.expirationDate IS NOT NULL AND u.expirationDate < :currentTime")
    void deleteExpiredUrls(LocalDateTime currentTime);
}
