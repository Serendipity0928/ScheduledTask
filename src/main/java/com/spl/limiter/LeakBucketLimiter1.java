package com.spl.limiter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LeakBucketLimiter1 {
    //桶的大小
    private static long capacity = 10;
    //流出速率,每秒两个
    private static long rate = 2;
    //开始时间
    private static long startTime = System.currentTimeMillis();
    //桶中剩余的水
    private static AtomicLong water = new AtomicLong();

    /**
     * true 代表放行，请求可已通过
     * false 代表限制，不让请求通过
     */
    public synchronized static boolean tryAcquire() {
        //如果桶的余量问0，直接放行
        if (water.get() == 0) {
            startTime = System.currentTimeMillis();
            water.set(1);
            return true;
        }
        //计算从当前时间到开始时间流出的水，和现在桶中剩余的水
        //桶中剩余的水
        water.set(water.get() - (System.currentTimeMillis() - startTime) / 1000 * rate);
        //防止出现<0的情况
        water.set(Math.max(0, water.get()));
        //设置新的开始时间
        startTime += (System.currentTimeMillis() - startTime) / 1000 * 1000;
        //如果当前水小于容量，表示可以放行
        if (water.get() < capacity) {
            water.incrementAndGet();
            return true;
        } else {
            return false;
        }
    }


    // 测试
    public static void main(String[] args) {

        //线程池，用于多线程模拟测试
        ExecutorService pool = Executors.newFixedThreadPool(10);
        // 被限制的次数
        AtomicInteger limited = new AtomicInteger(0);
        // 线程数
        final int threads = 2;
        // 每条线程的执行轮数
        final int turns = 20;
        // 同步器
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            pool.submit(() ->
            {
                try {

                    for (int j = 0; j < turns; j++) {

                        boolean flag = tryAcquire();
                        if (!flag) {
                            // 被限制的次数累积
                            limited.getAndIncrement();
                        }
                        Thread.sleep(200);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //等待所有线程结束
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        float time = (System.currentTimeMillis() - start) / 1000F;
        //输出统计结果
        System.out.println("限制的次数为：" + limited.get() +
                ",通过的次数为：" + (threads * turns - limited.get()));
        System.out.println("限制的比例为：" + (float) limited.get() / (float) (threads * turns));
        System.out.println("运行的时长为：" + time + "s");
    }

}
