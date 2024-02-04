package com.spl.domain;

import lombok.Data;

@Data
public class PhoneDistrictResponse {

    private int code;

    private District data;

    @Data
    public static class District {
        private String province;
        private String city;
        private String sp;
    }

}

