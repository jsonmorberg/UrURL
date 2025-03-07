package com.jsonmorberg.ururl.service;

import com.jsonmorberg.ururl.model.Url;
import com.jsonmorberg.ururl.model.UrlRepository;
import com.jsonmorberg.ururl.utils.UrlHashGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UrUrlService {

    private final UrlRepository urlRepository;
    private final UrlHashGenerator urlHashGenerator;
    private final RedisTemplate<String, Url> redisTemplate;

    @Autowired
    public UrUrlService(UrlRepository urlRepository, RedisTemplate<String, Url> redisTemplate) {
        this.urlRepository = urlRepository;
        this.urlHashGenerator = new UrlHashGenerator();
        this.redisTemplate = redisTemplate;
    }

    // Method to create and store a new short URL
    @Transactional
    public Url createShortUrl(String originalUrl, Long timeToLive) {

        Optional<Url> existingUrl = urlRepository.findByOriginalUrl(originalUrl);

        if (existingUrl.isPresent()) {
            Url url = existingUrl.get();
            // If the URL exists, check if it's expired
            if (url.isExpired()) {
                String newShortCode = urlHashGenerator.generateShortCode();
                url.setShortCode(newShortCode);
                url.setExpirationDate(LocalDateTime.now().plusSeconds(timeToLive));
                url.setClickCount(0);

                cacheUrl(url);
                return urlRepository.save(url);
            } else {
                return url;
            }
        } else {
            String shortCode = urlHashGenerator.generateShortCode();
            Url newUrl = new Url(originalUrl, shortCode, LocalDateTime.now().plusSeconds(timeToLive));

            cacheUrl(newUrl);
            return urlRepository.save(newUrl);
        }


    }

    // Method to find the original URL by short URL
    public Optional<Url> findOriginalUrl(String shortCode) {
        ValueOperations<String, Url> ops = redisTemplate.opsForValue();

        Url cachedUrl = ops.get(shortCode);
        if (cachedUrl != null && !cachedUrl.isExpired()) {
            return Optional.of(cachedUrl);
        }

        Optional<Url> urlOptional = urlRepository.findByShortCode(shortCode);
        urlOptional.ifPresent(this::cacheUrl);

        return urlOptional;
    }


    // Method to increment the click count for a short URL
    @Transactional
    public void incrementClickCount(String shortCode) {
        Optional<Url> urlOptional = urlRepository.findByShortCode(shortCode);
        urlOptional.ifPresent(url -> {
            url.incrementClickCount();
            urlRepository.save(url);
            cacheUrl(url);
        });
    }

    // Clean up expired URLs every 24 hours (86400000 milliseconds)
    @Scheduled(fixedRate = 86400000)  // 24 hours interval
    public void cleanupExpiredUrls() {
        LocalDateTime now = LocalDateTime.now();
        urlRepository.deleteExpiredUrls(now);
    }

    private void cacheUrl(Url url) {
        long timeToLive = ChronoUnit.SECONDS.between(LocalDateTime.now(), url.getExpirationDate());
        if (timeToLive > 0) {
            redisTemplate.opsForValue().set(url.getShortCode(), url, timeToLive, TimeUnit.SECONDS);
        }
    }
}

