package com.spl.boot;

import com.spl.schedule.ScheduleTaskHandler;
import com.spl.util.ThreadUtil;

public class Boot {

    public static void main(String[] args)  {
        long lastExecuteTimeStamp = 0L;
        long silenceTimeStampDiff = 0L;
        if(args.length != 0) {
            lastExecuteTimeStamp = Long.parseLong(args[0]);  // 第一个参数为上一次执行时间
            silenceTimeStampDiff = Long.parseLong(args[1]);  // 第一个参数为静默时间差
            ThreadUtil.CURRENT_LAST_THREAD_LOCAL.set(lastExecuteTimeStamp);
            ThreadUtil.CURRENT_THREAD_LOCAL.set(silenceTimeStampDiff);
        }
        if(System.currentTimeMillis() < lastExecuteTimeStamp + silenceTimeStampDiff ) {
            System.out.println("系统静默，任务不继续执行~");
            return;
        }

        ScheduleTaskHandler.scheduleTaskStart();

    }

}
