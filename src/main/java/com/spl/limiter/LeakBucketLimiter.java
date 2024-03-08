package com.spl.limiter;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.Getter;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class LeakBucketLimiter {

    private int maxCapacity;   // 漏斗最大容量

    private AtomicInteger leftCapacity;      // 漏斗中目前剩余容量

    private double rate;        // 漏斗流出速率，单位(毫秒)  [最多损失(1000/rate)ms的精度]

    private long lastAdjustTime = System.currentTimeMillis();    // 上一次计算漏斗容量时间

    public LeakBucketLimiter(int maxCapacity, double rate) {
        this.maxCapacity = maxCapacity;
        this.leftCapacity = new AtomicInteger(maxCapacity);
        this.rate = rate;
    }

    // 调整漏斗桶，使用sync关键字修饰
    private synchronized int adjustBucket() {
        int allowableDownCount = (int) ((System.currentTimeMillis() - this.lastAdjustTime) * this.rate);
        if(allowableDownCount < 0) {
            allowableDownCount = this.maxCapacity;
        }


        int nextCandidateLeft = Math.min(this.maxCapacity, this.leftCapacity.get() + allowableDownCount);

//        this.leftCapacity.set(this.leftCapacity.get() + allowableDownCount);
//        this.leftCapacity.set(Math.min(this.leftCapacity.get(), this.maxCapacity));

        if(nextCandidateLeft < 1) {
            System.out.println("当前时间：" + lastAdjustTime + ", 未通过...");
            return -1;
        }
        lastAdjustTime = System.currentTimeMillis();
        this.leftCapacity.set(nextCandidateLeft - 1);
//        int curCount = leftCapacity.decrementAndGet();
        System.out.println("当前时间：" + lastAdjustTime + ", leftCount=" + this.leftCapacity.get());
        return this.leftCapacity.get();
    }

    /**
     * 漏斗整形作用：
     *
     * @return
     */
    public boolean tryAcquire() {
//        if(System.currentTimeMillis() - this.lastAdjustTime < 0) {
//            // 距离上一次调整，100ms以内的请求直接快速判断
//            int leftCount = this.leftCapacity.decrementAndGet();
//            return leftCount > 0;
//        }

        int leftCount = adjustBucket();

//        int leftCount = this.leftCapacity.decrementAndGet();
        return leftCount >= 0;
    }


    @Test
    public void test(){
        LeakBucketLimiter limiter = new LeakBucketLimiter(10, 20.0/1000);

    }

    private static long startTime = 0;
    private static long endTime = 0;

    public static void main(String[] args) {
        LeakBucketLimiter limiter = new LeakBucketLimiter(1, 20.0/1000);   // 限制f=20qps  损失50ms的精度

        //线程池，用于多线程模拟测试
        ExecutorService pool = Executors.newFixedThreadPool(10);

        // 被限制的次数
        AtomicInteger limited = new AtomicInteger(0);
        // 线程数
        final int threads = 5;
        // 每条线程的执行轮数
        final int turns = 20;
        // 执行间隔
        final int sleepTime = 50;   // 任务每秒执行次数 1000/sleepTime
        // 同步器
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            pool.submit(() ->
            {
                try {
                    for (int j = 0; j < turns; j++) {
                        if(startTime == 0) {
                            startTime = System.currentTimeMillis();
                        }
                        boolean flag = limiter.tryAcquire();        // 单线程4s发送20个请求  相当于5qps【与下相符】
                        if (!flag) {
                            // 被限制的次数累积
                            limited.getAndIncrement();
                        }
                        endTime = System.currentTimeMillis();
                        Thread.sleep(sleepTime);      // f = 200->5qps  50 --> 20qps
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
        }finally {
            pool.shutdown();
        }
        float time = (endTime - startTime) / 1000F;
        //输出统计结果
        System.out.println("限制的次数为：" + limited.get() +
                ",通过的次数为：" + (threads * turns - limited.get()));
        System.out.println("限制的比例为：" + (float) limited.get() / (float) (threads * turns));
        System.out.println("运行的时长为：" + time + "s");


        System.out.println((1000.0/sleepTime) * time);
    }

}
