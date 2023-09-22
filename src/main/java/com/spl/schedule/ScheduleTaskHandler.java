package com.spl.schedule;

import java.io.IOException;

public class ScheduleTaskHandler {

    public static void scheduleTaskStart() {

        try {
            RailwayTicketRemainMonitorJob.startJob();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
