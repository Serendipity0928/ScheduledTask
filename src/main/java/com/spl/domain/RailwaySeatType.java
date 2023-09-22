package com.spl.domain;

public enum RailwaySeatType {
    BUSINESS_SEAT("商务座/特等座", false, 32),
    FIRST_CLASS_SEAT("一等座", false, 31),
    SECOND_CLASS_SEAT("二等座", true, 30),
    HIGH_SOFT_SLEEPER("高级软卧", false, -1),          // 暂不支持
    FIRST_SOFT_SLEEPER("软卧一等卧", false, 23),
    DYNAMIC_SLEEPER("动卧", false, -1),                // 暂不支持
    HARD_SLEEPER("硬卧", false, 28),
    SOFT_SEAT("软座", false, -1),                       // 暂不支持
    HARD_SEAT("硬座", true, 29),
    NO_SEAT("无座", false, 26);

    private String name;
    private boolean defaultFocus;
    private int index;

    RailwaySeatType(String name, boolean defaultFocus, int index) {
        this.name = name;
        this.defaultFocus = defaultFocus;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public boolean isDefaultFocus() {
        return defaultFocus;
    }

    public int getIndex() {
        return index;
    }
}
