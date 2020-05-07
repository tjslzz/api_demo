package rate_limiter;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RateLimiter {

    private Map<String, Map<String, String>> mapPool;

    private long intervalInMills;
    private long limit;
    private double intervalPerPermit;

    public RateLimiter() {
        mapPool = new HashMap<>();
        intervalInMills = 10000;
        limit = 3;
        intervalPerPermit = intervalInMills * 1.0 / limit;
    }


    public boolean access(String userId) {

        String key = genKey(userId);
        Map<String, String> counter = mapPool.get(key);

        if (counter.size() == 0) {
            TokenBucket tokenBucket = new TokenBucket(System.currentTimeMillis(), limit - 1);
            mapPool.put(key, tokenBucket.toHash());
            return true;
        } else {
            TokenBucket tokenBucket = TokenBucket.fromHash(counter);

            long lastRefillTime = tokenBucket.getLastRefillTime();
            long refillTime = System.currentTimeMillis();
            long intervalSinceLast = refillTime - lastRefillTime;

            long currentTokensRemaining;
            if (intervalSinceLast > intervalInMills) {
                currentTokensRemaining = limit;
            } else {
                long grantedTokens = (long) (intervalSinceLast / intervalPerPermit);
                currentTokensRemaining = Math.min(grantedTokens + tokenBucket.getTokensRemaining(), limit);
            }

            tokenBucket.setLastRefillTime(refillTime);
            assert currentTokensRemaining >= 0;
            if (currentTokensRemaining == 0) {
                tokenBucket.setTokensRemaining(currentTokensRemaining);
                mapPool.put(key, tokenBucket.toHash());
                return false;
            } else {
                tokenBucket.setTokensRemaining(currentTokensRemaining - 1);
                mapPool.put(key, tokenBucket.toHash());
                return true;
            }
        }
    }

    private String genKey(String userId) {
        return "rate:limiter:" + intervalInMills + ":" + limit + ":" + userId;
    }

    public static void main(String[] args) throws InterruptedException {
        RateLimiter rateLimiter = new RateLimiter();

        for (int i = 0; i < 3; i++) {
            log.info("access status : {}", rateLimiter.access("jerry"));
        }
        log.info("access status : {}", rateLimiter.access("jerry"));
        Thread.sleep(7000);
        log.info("access status : {}", rateLimiter.access("jerry"));
    }
}