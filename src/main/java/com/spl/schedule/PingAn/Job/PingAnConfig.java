package com.spl.schedule.PingAn.Job;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class PingAnConfig {

    private String signature;

    private String sessionId;

    private String storefrontseq;       // 12836

    private String businessType;        // 03--> ?

//    private long hotTime;

}
