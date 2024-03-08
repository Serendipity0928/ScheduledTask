package com.spl.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class JsonUtils {

    public static <T> T parseObject(String data, TypeReference<T> t) {
        try {
            return JSON.parseObject(data, t);
        } catch (Exception e) {
            System.out.println("Json转换失败，data=" + data + ", 转换类型为：" + t.getType());
            e.printStackTrace();
        }
        return null;
    }

}
