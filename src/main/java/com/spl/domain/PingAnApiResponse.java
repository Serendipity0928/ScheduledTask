package com.spl.domain;

import lombok.Data;

/**
 * 平安API的响应类
 */
@Data
public class PingAnApiResponse<T> {

    private int code;

    private String msg;

    private T data;

}
