package com.jsonmorberg.ururl.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    // Find a URL by its shortened code
    Optional<Url> findByShortUrl(String shortUrl);

    // Find a URL by original URL
    Optional<Url> findByOriginalUrl(String originalUrl);

    // Custom query to delete expired URLs
    @Modifying
    @Query("DELETE FROM Url u WHERE u.timeToLive IS NOT NULL AND u.timeToLive < ?1")
    void deleteAllExpiredUrls(LocalDateTime currentTime);
}
