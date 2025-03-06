package com.jsonmorberg.ururl.service;

import com.jsonmorberg.ururl.model.Url;
import com.jsonmorberg.ururl.model.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrUrlService {

    private final UrlRepository urlRepository;

    // Constructor injection
    @Autowired
    public UrUrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    // Method to create and store a new short URL
    @Transactional
    public Url createShortUrl(String originalUrl, Long timeToLive) {
        // Check if the original URL already exists
        Optional<Url> existingUrl = urlRepository.findByOriginalUrl(originalUrl);

        if (existingUrl.isPresent()) {
            Url url = existingUrl.get();
            // If the URL exists, check if it's expired
            if (url.isExpired()) {
                String newShortUrl = generateShortUrl();
                url.setShortUrl(newShortUrl);
                url.setCreated(LocalDateTime.now());
                url.setTimeToLive(timeToLive);
                url.setClickCount(0);
                return urlRepository.save(url);
            } else {
                return url;
            }
        } else {
            // If the original URL doesn't exist, create a new one
            String shortUrl = generateShortUrl();
            Url newUrl = new Url(originalUrl, shortUrl, timeToLive);
            return urlRepository.save(newUrl);
        }
    }

    // Method to generate a unique short URL (you can customize this logic)
    private String generateShortUrl() {
        return java.util.UUID.randomUUID().toString().substring(0, 6);
    }

    // Method to find the original URL by short URL
    public Optional<Url> findOriginalUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl);
    }

    // Method to increment the click count for a short URL
    @Transactional
    public void incrementClickCount(String shortUrl) {
        Optional<Url> urlOptional = urlRepository.findByShortUrl(shortUrl);
        urlOptional.ifPresent(url -> {
            url.incrementClickCount();
            urlRepository.save(url);
        });
    }

    // Clean up expired URLs every 24 hours (86400000 milliseconds)
    @Scheduled(fixedRate = 86400000)  // 24 hours interval
    public void cleanupExpiredUrls() {
        LocalDateTime now = LocalDateTime.now();
        urlRepository.deleteAllExpiredUrls(now);
    }
}

