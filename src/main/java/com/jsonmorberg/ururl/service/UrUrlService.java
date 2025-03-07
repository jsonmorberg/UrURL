package com.jsonmorberg.ururl.service;

import com.jsonmorberg.ururl.model.Url;
import com.jsonmorberg.ururl.model.UrlRepository;
import com.jsonmorberg.ururl.utils.UrlHashGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrUrlService {

    private final UrlRepository urlRepository;
    private final UrlHashGenerator urlHashGenerator;

    @Autowired
    public UrUrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
        this.urlHashGenerator = new UrlHashGenerator();
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
                String newShortCode = urlHashGenerator.generateShortCode();
                url.setShortCode(newShortCode);
                url.setExpirationDate(LocalDateTime.now().plusSeconds(timeToLive));
                url.setClickCount(0);
                return urlRepository.save(url);
            } else {
                return url;
            }
        } else {
            String shortCode = urlHashGenerator.generateShortCode();
            Url newUrl = new Url(originalUrl, shortCode, LocalDateTime.now().plusSeconds(timeToLive));
            return urlRepository.save(newUrl);
        }


    }

    // Method to find the original URL by short URL
    public Optional<Url> findOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    // Method to increment the click count for a short URL
    @Transactional
    public void incrementClickCount(String shortCode) {
        Optional<Url> urlOptional = urlRepository.findByShortCode(shortCode);
        urlOptional.ifPresent(url -> {
            url.incrementClickCount();
            urlRepository.save(url);
        });
    }

    // Clean up expired URLs every 24 hours (86400000 milliseconds)
    @Scheduled(fixedRate = 86400000)  // 24 hours interval
    public void cleanupExpiredUrls() {
        LocalDateTime now = LocalDateTime.now();
        urlRepository.deleteExpiredUrls(now);
    }
}

