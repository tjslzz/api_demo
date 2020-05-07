package com.example.make_by_hand.leaky_limiter;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
public class SemaphoreRunner {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newCachedThreadPool();
        // 信号量，只允许 3个线程同时访问
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i < 10; i++) {
            final long num = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 获取许可
                        semaphore.acquire();
                        // 执行
                        log.info("Accessing: {}", num);
                        Thread.sleep(new Random().nextInt(5000)); // 模拟随机执行时长
                        // 释放
                        semaphore.release();
                        log.info("Release...{}", num);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executorService.shutdown();
    }

}
