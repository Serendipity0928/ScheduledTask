package com.spl.schedule.PingAn;

import com.spl.schedule.PingAn.Job.PingAnConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingAnTaskBoot {

    private  static  final Logger logger = LoggerFactory.getLogger(PingAnTaskBoot.class);

    public static void main(String[] args) {
        logger.info("=================程序开始运行=================");

        PingAnConfig config = new PingAnConfig();
        config.setSessionId("4c7ae7b5b4b14594a2355177f60b0e52Z3F0");
        config.setSignature("13c4b1602167c5ffbf5f5b4623bc1518");
        config.setStorefrontseq("39807");       // 应该需要改这个
        config.setBusinessType("14");       // 14

        try {
            new BookingService(config).start();
        } catch (Exception e) {
            logger.error("程序运行中出现异常，config={}.", config, e);
        }

        logger.info("=================程序运行结束=================");
    }

}
