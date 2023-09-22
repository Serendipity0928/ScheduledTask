package com.spl.domain;

public enum RailwaySeatType {
    BUSINESS_SEAT("商务座/特等座", false),
    FIRST_CLASS_SEAT("一等座", false),
    SECOND_CLASS_SEAT("二等座", true),
    HIGH_SOFT_SLEEPER("高级软卧", false),
    FIRST_SOFT_SLEEPER("软卧一等卧", false),
    DYNAMIC_SLEEPER("动卧", false),
    HARD_SLEEPER("硬卧", false),
    SOFT_SEAT("软座", false),
    HARD_SEAT("硬座", true),
    NO_SEAT("无座", false);

    private String name;
    private boolean defaultFocus;

    RailwaySeatType(String name, boolean defaultFocus) {
        this.name = name;
        this.defaultFocus = defaultFocus;
    }

    public String getName() {
        return name;
    }

    public boolean isDefaultFocus() {
        return defaultFocus;
    }
}
