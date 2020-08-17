package com.lemon.pojo;

/*
    @auther:cheryl
    @date:2020/8/9-23:08
*/


public class Expected {
    private Object code;
    private Object msg;

    public Expected() {
    }

    public Expected(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Expected{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
