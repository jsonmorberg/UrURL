package com.jsonmorberg.ururl.service;

import com.jsonmorberg.ururl.model.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class UrUrlController {

    @Autowired
    private UrUrlService urlService;

    // Endpoint to create a new short URL w/ optional time-to-live
    @PostMapping("/create")
    public ResponseEntity<?> createShortUrl(@RequestParam String originalUrl,
                                              @RequestParam(required = false) Long timeToLive) {
        if (timeToLive == null) {
            timeToLive = 7 * 24 * 60 * 60L; // default ttl at 7 days (stored as seconds)
        }

        try {
            URI uri = new URI(originalUrl);
            uri.toASCIIString();  // throws exception if url is malformed
        } catch (URISyntaxException e) {
            return ResponseEntity.badRequest().body("Invalid URL format.");
        }

        Url shortUrl = urlService.createShortUrl(originalUrl, timeToLive);
        return ResponseEntity.ok(shortUrl);
    }

    // Endpoint to get the original URL by short URL
    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> getOriginalUrl(@PathVariable String shortUrl) {
        Optional<Url> urlOptional = urlService.findOriginalUrl(shortUrl);
        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();
            if (url.isExpired()) {
                return ResponseEntity.status(410).body("URL has expired");
            }
            // Increment click count when the URL is being accessed
            urlService.incrementClickCount(shortUrl);
            return ResponseEntity.status(302).location(URI.create(url.getOriginalUrl())).build();
        } else {
            return ResponseEntity.status(404).body("Short URL not found");
        }
    }
}
