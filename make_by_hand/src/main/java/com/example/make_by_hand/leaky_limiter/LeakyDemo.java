package com.example.make_by_hand.leaky_limiter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeakyDemo {
    private long timeStamp = System.currentTimeMillis();
    private int capacity; // 桶的容量
    private int rate; // 水漏出的速度
    private int water; // 当前水量(当前累积请求数)

    public boolean grant() {
        long now = System.currentTimeMillis();
        water = 0 > (water - (now - timeStamp) * rate) ? 0 : (int) (water - (now - timeStamp) * rate); // 先执行漏水，计算剩余水量
        timeStamp = now;
        if ((water + 1) < capacity) {// 尝试加水,并且水还未满
            water += 1;
            return true;
        } else {// 水满，拒绝加水
            return false;
        }
    }
}
