package com.example.make_by_hand.leaky_limiter;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CounterDemo {
    private long timeStamp = System.currentTimeMillis();
    private int reqCount = 0;
    private final int limit = 100; // 时间窗口内最大请求数
    private final long interval = 1000; // 时间窗口ms

    public boolean grant() {
        long now = System.currentTimeMillis();
        if (now < timeStamp + interval) {// 在时间窗口内
            reqCount++;// 判断当前时间窗口内是否超过最大请求控制数
            return reqCount <= limit;
        } else {
            timeStamp = now;// 超时后重置
            reqCount = 1;
            return true;
        }
    }
}
