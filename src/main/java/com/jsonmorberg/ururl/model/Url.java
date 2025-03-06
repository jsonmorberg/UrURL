package com.jsonmorberg.ururl.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Original URL
    @Column(nullable = false)
    private String originalUrl;

    // Shortened URL
    @Column(nullable = false, unique = true)
    private String shortUrl;

    // Date/Time of URL generation
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    // Postgres ejection date for shortened URL
    private Long timeToLive;

    // Clicks counter
    @Column(nullable = false)
    private long clickCount;

    public Url(String originalUrl, String shortCode, Long timeToLive) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortCode;
        this.created = LocalDateTime.now();
        this.timeToLive = timeToLive;
        this.clickCount = 0;
    }

    public boolean isExpired() {
        if (timeToLive == null) {
            return false; // No TTL, never expires
        }
        LocalDateTime expirationTime = created.plusSeconds(timeToLive);
        return expirationTime.isBefore(LocalDateTime.now());
    }

    public void incrementClickCount() {
        this.clickCount++;
    }
}
