package com.spl.schedule.PingAn;

import com.spl.schedule.PingAn.Job.PingAnConfig;
import com.spl.schedule.PingAn.Job.PingAnStoreBookingTimeJob;
import com.spl.schedule.PingAn.Job.ReservePingAnOffline;
import com.spl.schedule.PingAn.exception.PingAnBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class BookingService {

    private PingAnConfig bookConfig;

    private static ExecutorService executorsForQuery = Executors.newFixedThreadPool(4);

    private static ExecutorService doExecutors = Executors.newFixedThreadPool(4);

    private static volatile Map<String, PingAnStoreBookingTimeJob.BookingTimeBO> bookTimeMaps = new HashMap<>();

    private static volatile Set<String> filterItems = new HashSet<>();

    public BookingService(PingAnConfig bookConfig) {
        this.bookConfig = bookConfig;
    }

    public void start() {
        if(!checkBookingConfig()) {
            log.error("平安预定任务的配置不正确！config={}", bookConfig);
            return;
        }

        Future<?> queryItemFuture = doExecutors.submit(() -> {
            monitorBookableItem(this.bookConfig);
        });


        ReservePingAnOffline reserveJob1 = new ReservePingAnOffline(bookConfig);
//        ReservePingAnOffline reserveJob2 = new ReservePingAnOffline(bookConfig);
//        ReservePingAnOffline reserveJob3 = new ReservePingAnOffline(bookConfig);
//        ReservePingAnOffline reserveJob4 = new ReservePingAnOffline(bookConfig);
        try {
            // 主线程等待数据
            queryItemFuture.get();
        } catch (Exception e) {
            log.error("主线程等待查询任务异常", e);
            return;
        }

        if(MapUtils.isEmpty(bookTimeMaps)) {
            log.error("主线程未查询到可预约项，无法继续！");
            return;
        }


        doExecutors.submit(() -> {
            doBook(reserveJob1);
        });




    }

    private boolean checkBookingConfig() {
        return Objects.nonNull(this.bookConfig)
                && StringUtils.isNoneBlank(this.bookConfig.getSessionId(), this.bookConfig.getSignature());
    }

    private void monitorBookableItem(PingAnConfig bookConfig) {

        try {
            PingAnStoreBookingTimeJob bookingTimeJob = new PingAnStoreBookingTimeJob(bookConfig);

            while (true) {
                if(MapUtils.isNotEmpty(bookTimeMaps)) {
                    return;
                }

                Optional<List<PingAnStoreBookingTimeJob.BookingTimeBO>> bookingTimeBOSOpt = bookingTimeJob.requestAPI();
                if(!bookingTimeBOSOpt.isPresent()) {
                    log.error("获取平安预约项任务API响应异常，请检查请求设置. bookConfig={}", bookConfig);
                    throw new PingAnBusinessException("获取平安预约项任务API响应异常");
                }

                List<PingAnStoreBookingTimeJob.BookingTimeBO> bookingTimeBOS = bookingTimeBOSOpt.get();
                if(CollectionUtils.isEmpty(bookingTimeBOS)) {
                    // 无相关数据，重试; 10ms重试，1s 重试100次
                    Thread.sleep(10);
                    continue;
                }

                // 已获取到数据
                bookTimeMaps = bookingTimeBOS.stream()
                        .collect(Collectors.toMap(
                                PingAnStoreBookingTimeJob.BookingTimeBO::getIdBookingSurvey, Function.identity()
                        ));
            }
        } catch (PingAnBusinessException e) {
            log.error("获取平安预约项任务业务异常，bookConfig={}", bookConfig, e);
        } catch (Exception e) {
            log.error("获取平安预约项任务中出现未知异常，bookConfig={}", bookConfig, e);
        }


    }

    private void doBook(ReservePingAnOffline reserveJob) {
        if(MapUtils.isEmpty(bookTimeMaps)) {
            return;
        }

        try {
            while (true) {
                PingAnStoreBookingTimeJob.BookingTimeBO currentBookItem = null;
                for (Map.Entry<String, PingAnStoreBookingTimeJob.BookingTimeBO> entry : bookTimeMaps.entrySet()) {
                    if(filterItems.contains(entry.getKey())) {
                        continue;
                    }
                    currentBookItem = entry.getValue();
                    break;
                }

                if(currentBookItem == null) {
                    break;
                }

                reserveJob.addRuntimeBody(currentBookItem);
                Optional<Boolean> resultOpt = reserveJob.requestAPI();
                if(!resultOpt.isPresent()) {
                    Thread.sleep(10);    // 睡眠10ms
                    continue;
                }

                boolean result = resultOpt.isPresent();
                filterItems.add(currentBookItem.getIdBookingSurvey());
            }
        } catch (PingAnBusinessException e) {
            log.error("执行平安预约任务业务异常，reserveJob={}", reserveJob, e);
        } catch (Exception e) {
            log.error("执行平安预约任务中出现未知异常，reserveJob={}", reserveJob, e);
        }





    }

}
