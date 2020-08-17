package com.lemon.utils;

/*
    @auther:cheryl
    @date:2020-8-13-17:17
*/


public class Contants {
    // final修饰变量，变量成为常量，常量只能赋值一次
    // 响应结果回写列号
    public static final int RESPONSE_CELL_NUM = 8;
    // 断言结果回写列号
    public static final int ASSERT_CELL_NUM = 9;
    //  断言成功
    public static final String  ASSERT_PASSED = "PASSED";
    // 断言失败
    public static final String  ASSERT_FAILED = "FAILED";

    // 用例文件地址
    public static final String EXCEL_PATH = "src/test/resources/cases.xlsx";
    // 数据库连接
    public static final String JDBC_URL ="jdbc:mysql://api.lemonban.com:3306/futureloan?useUnicode=true&characterEncoding=utf-8";
    public static final String JDBC_USERNAME ="future";
    public static final String JDBC_PASSOWRD ="123456";

}
