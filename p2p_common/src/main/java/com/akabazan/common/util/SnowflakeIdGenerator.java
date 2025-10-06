package com.akabazan.common.util;

public class SnowflakeIdGenerator {
  
     private static final long EPOCH = 1704067200000L; // mốc 2024-01-01
    private static final long MACHINE_ID = 1L;        // nếu có nhiều server thì config riêng
    private static long sequence = 0L;
    private static long lastTimestamp = -1L;

    public static synchronized long nextId() {
        long now = System.currentTimeMillis();
        if (now == lastTimestamp) {
            sequence = (sequence + 1) & 4095; // 12 bits
            if (sequence == 0) {
                while (now <= lastTimestamp) {
                    now = System.currentTimeMillis();
                }
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = now;
        return ((now - EPOCH) << 22) | (MACHINE_ID << 12) | sequence;
    }
}
