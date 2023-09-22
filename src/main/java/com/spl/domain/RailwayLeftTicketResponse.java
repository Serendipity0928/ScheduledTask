package com.spl.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RailwayLeftTicketResponse {
    private int httpstatus;
    private LeftTicketData data;
    private String messages;
    private boolean status;

    @Data
    public static class LeftTicketData {
        private List<String> result;
        private String flag;
        private Map<String, String> map;


    }
}

