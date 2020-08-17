package com.lemon.utils;

/*
    @auther:cheryl
    @date:2020/8/5-21:27
*/


import java.util.HashMap;
import java.util.Map;

public class UserData {
    // 存储接口响应变量
    public static Map<String, Object> VARS = new HashMap<>();
//Authorization token

    // 存储默认请求头
    public static Map<String,String> DEFAULT_HEADERS = new HashMap<>();

    static{
        // 静态代码块：类在加载时自动加载一次本代码
        DEFAULT_HEADERS.put("X-Lemonban-Media-Type", "lemonban.v2");
        DEFAULT_HEADERS.put("Content-Type", "application/json");

    }
}
