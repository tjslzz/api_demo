package com.example.make_by_hand.rate_limiter;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
public class TokenBucket {
    private long lastRefillTime;
    private long tokensRemaining;

    public TokenBucket(long lastRefillTime, long tokensRemaining) {
        this.lastRefillTime = lastRefillTime;
        this.tokensRemaining = tokensRemaining;
    }

    public static TokenBucket fromHash(Map<String, String> hash) {
        long lastRefillTime = Long.parseLong(hash.get("lastRefillTime"));
        int tokensRemaining = Integer.parseInt(hash.get("tokensRemaining"));
        return new TokenBucket(lastRefillTime, tokensRemaining);
    }

    public Map<String, String> toHash() {
        Map<String, String> hash = new HashMap<>();
        hash.put("lastRefillTime", String.valueOf(lastRefillTime));
        hash.put("tokensRemaining", String.valueOf(tokensRemaining));
        return hash;
    }
}
