package com.jsonmorberg.ururl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Original URL
    @Column(nullable = false)
    private String originalUrl;

    // Shortened URL
    @Column(nullable = false, unique = true)
    private String shortCode;

    // Ejection date for shortened URL
    private LocalDateTime expirationDate;

    // Clicks counter
    @Column(nullable = false)
    private long clickCount;

    public Url(String originalUrl, String shortCode, LocalDateTime expirationDate) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.expirationDate = expirationDate;
        this.clickCount = 0;
    }

    public boolean isExpired() {
        if (expirationDate == null) {
            return false;
        }

        return expirationDate.isBefore(LocalDateTime.now());
    }

    public void incrementClickCount() {
        this.clickCount++;
    }
}
