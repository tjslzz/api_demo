package com.example.make_by_hand.rate_limiter;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TokenBucketTestDemo {

    // 默认桶大小个数 即最大瞬间流量是64M
    private static final int DEFAULT_BUCKET_SIZE = 1024 * 1024 * 64;

    // 一个桶的单位是1字节
    private int everyTokenSize = 1;

    // 瞬间最大流量
    private int maxFlowRate;

    // 平均流量
    private int avgFlowRate;

    // 队列来缓存桶数量：最大的流量峰值就是 = everyTokenSize*DEFAULT_BUCKET_SIZE 64M = 1 * 1024 *
    // 1024 * 64
    private ArrayBlockingQueue<Byte> tokenQueue = new ArrayBlockingQueue<>(
            DEFAULT_BUCKET_SIZE);

    private ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor();

    private volatile boolean isStart = false;

    private ReentrantLock lock = new ReentrantLock(true);

    private static final byte A_CHAR = 'a';

    public TokenBucketTestDemo() {
    }

    public TokenBucketTestDemo(int maxFlowRate, int avgFlowRate) {
        this.maxFlowRate = maxFlowRate;
        this.avgFlowRate = avgFlowRate;
    }

    public TokenBucketTestDemo(int everyTokenSize, int maxFlowRate, int avgFlowRate) {
        this.everyTokenSize = everyTokenSize;
        this.maxFlowRate = maxFlowRate;
        this.avgFlowRate = avgFlowRate;
    }

    public void addTokens(Integer tokenNum) {

        // 若是桶已经满了，就不再家如新的令牌
        for (int i = 0; i < tokenNum; i++) {
            tokenQueue.offer(Byte.valueOf(A_CHAR));
        }
    }

    public TokenBucketTestDemo build() {

        start();
        return this;
    }

    /**
     * 获取足够的令牌个数
     */
    public boolean getTokens(byte[] dataSize) {

//        Preconditions.checkNotNull(dataSize);
//        Preconditions.checkArgument(isStart,

        int needTokenNum = dataSize.length / everyTokenSize + 1;// 传输内容大小对应的桶个数

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            boolean result = needTokenNum <= tokenQueue.size(); // 是否存在足够的桶数量
            if (!result) {
                return false;
            }

            int tokenCount = 0;
            for (int i = 0; i < needTokenNum; i++) {
                Byte poll = tokenQueue.poll();
                if (poll != null) {
                    tokenCount++;
                }
            }

            return tokenCount == needTokenNum;
        } finally {
            lock.unlock();
        }
    }

    public void start() {

        // 初始化桶队列大小
        if (maxFlowRate != 0) {
            tokenQueue = new ArrayBlockingQueue<>(maxFlowRate);
        }

        // 初始化令牌生产者
        TokenProducer tokenProducer = new TokenProducer(avgFlowRate, this);
        scheduledExecutorService.scheduleAtFixedRate(tokenProducer, 0, 1,
                TimeUnit.SECONDS);
        isStart = true;

    }

    public void stop() {
        isStart = false;
        scheduledExecutorService.shutdown();
    }

    public boolean isStarted() {
        return isStart;
    }

    class TokenProducer implements Runnable {

        private int avgFlowRate;
        private TokenBucketTestDemo tokenBucket;

        public TokenProducer(int avgFlowRate, TokenBucketTestDemo tokenBucket) {
            this.avgFlowRate = avgFlowRate;
            this.tokenBucket = tokenBucket;
        }

        @Override
        public void run() {
            tokenBucket.addTokens(avgFlowRate);
        }
    }

    public static TokenBucketTestDemo newBuilder() {
        return new TokenBucketTestDemo();
    }

    public TokenBucketTestDemo everyTokenSize(int everyTokenSize) {
        this.everyTokenSize = everyTokenSize;
        return this;
    }

    public TokenBucketTestDemo maxFlowRate(int maxFlowRate) {
        this.maxFlowRate = maxFlowRate;
        return this;
    }

    public TokenBucketTestDemo avgFlowRate(int avgFlowRate) {
        this.avgFlowRate = avgFlowRate;
        return this;
    }

    private String stringCopy(String data, int copyNum) {

        StringBuilder sbuilder = new StringBuilder(data.length() * copyNum);

        for (int i = 0; i < copyNum; i++) {
            sbuilder.append(data);
        }

        return sbuilder.toString();

    }

    public static void main(String[] args) throws IOException,
            InterruptedException {

        tokenTest();
    }

    private static void arrayTest() {
        ArrayBlockingQueue<Integer> tokenQueue = new ArrayBlockingQueue<>(
                10);
        tokenQueue.offer(1);
        tokenQueue.offer(1);
        tokenQueue.offer(1);
        log.info("size : {}", tokenQueue.size());
        log.info("capacity : {}", tokenQueue.remainingCapacity());
    }

    private static void tokenTest() throws InterruptedException, IOException {
        TokenBucketTestDemo tokenBucket = TokenBucketTestDemo.newBuilder().avgFlowRate(512)
                .maxFlowRate(1024).build();

        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream("D:/demo")));
        String data = "xxxx";
        for (int i = 1; i <= 1000; i++) {

            Random random = new Random();
            int i1 = random.nextInt(100);
            boolean tokens = tokenBucket.getTokens(tokenBucket.stringCopy(data,
                    i1).getBytes());
            TimeUnit.MILLISECONDS.sleep(100);
            if (tokens) {
                bufferedWriter.write("token pass --- index:" + i1);
                log.info("token pass --- index:" + i1);
            } else {
                bufferedWriter.write("token rejuect --- index" + i1);
                log.info("token rejuect --- index" + i1);
            }

            bufferedWriter.newLine();
            bufferedWriter.flush();
        }

        bufferedWriter.close();
    }

}