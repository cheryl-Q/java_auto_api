package com.lemon.pojo;

/*
    @auther:cheryl
    @date:2020/8/3-21:55
*/


import cn.afterturn.easypoi.excel.annotation.Excel;

public class CaseInfo {

    // case_id	interface	title	method	url  contentType	data	expected	result	check_sql

    // @Excel:用来Excel用例和java类映射关系
    @Excel(name ="case_id")
    private int id;
    @Excel(name ="interface")
    private String name;
    @Excel(name = "title")
    private String title;
    @Excel(name ="method")
    private String method;
    @Excel(name ="url")
    private String url;
    // 不映射Desc(用例描述)
    @Excel(name = "data")
    private  String params;
    @Excel(name = "contentType")
    private String contentType;
    @Excel(name="expected")
    private String expected;
    @Excel(name = "check_sql")
    private String sql;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return "CaseInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", params='" + params + '\'' +
                ", contentType='" + contentType + '\'' +
                ", expected='" + expected + '\'' +
                ", sql='" + sql + '\'' +
                '}';
    }
}
