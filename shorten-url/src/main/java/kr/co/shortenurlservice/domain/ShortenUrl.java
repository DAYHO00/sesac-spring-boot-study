package kr.co.shortenurlservice.domain;

import java.util.Random;

public class ShortenUrl {

    private String originalUrl;
    private String shortenUrlKey;
    private Long redirectCount;
    private boolean active;

    public ShortenUrl(String originalUrl, String shortenUrlKey) {
        this(originalUrl, shortenUrlKey, 0L, true);
    }

    public ShortenUrl(
            String originalUrl,
            String shortenUrlKey,
            Long redirectCount,
            boolean active
    ) {
        this.originalUrl = originalUrl;
        this.shortenUrlKey = shortenUrlKey;
        this.redirectCount = redirectCount;
        this.active = active;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortenUrlKey() {
        return shortenUrlKey;
    }

    public Long getRedirectCount() {
        return redirectCount;
    }

    public boolean isActive() {
        return active;
    }

    public static String generateShortenUrlKey() {

        String characters =
                "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";

        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            key.append(characters.charAt(index));
        }

        return key.toString();
    }
}