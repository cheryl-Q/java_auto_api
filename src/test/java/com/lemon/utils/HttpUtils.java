package com.lemon.utils;

/*
    @auther:cheryl
    @date:2020/7/29-21:35
*/


import com.alibaba.fastjson.JSONObject;
import com.lemon.cases.BaseCase;
import com.lemon.pojo.CaseInfo;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


public class HttpUtils {
    /*
    发送一个get请求
    @param url  携带参数的url

    @throws Exception
     */

    public static String get(String url,Map<String,String> headers) throws Exception {
        // 1、创建请求
        HttpGet get = new HttpGet(url);
        // 2.添加请求体
//        get.setHeader("X-Lemonban-Media-Type","lemonban.v2");
        setHeaders(headers,get);
        // 3.添加客户端
        CloseableHttpClient client = HttpClients.createDefault();
        //4、发送请求，获取响应对象
        HttpResponse response = client.execute(get);
        // 5、格式化响应对象 response = 响应状态码+响应头+响应体
        return printResponse(response);
    }

    /*
    发送一个get请求
    @param url  携带参数的url
    @param param 接口参数
    @throws Exception
     */
    public static String post(String url, String param, Map<String,String> headers) throws Exception {
        // 1、创建请求
        HttpPost post = new HttpPost(url);
        // 2.设置请求头
//        post.setHeader("X-Lemonban-Media-Type","lemonban.v2");
//        post.setHeader("Content-Type","application/json");
//        post.setHeader("Content-Type","application/x-www-form-urlencoded");

//        headers.put("X-Lemonban-Media-Type","lemonban.v2");
//        headers.put("Content-Type","application/json");

        setHeaders(headers,post);

        // 3.设置请求体
        StringEntity body = new StringEntity(param,"UTF-8");
        post.setEntity(body);
        // 4.创建请求客户端
        HttpClient client = HttpClients.createDefault();
        // execute(HttpUriRequest):多态的方法，接受HttpUriRequest所有子实现类
        ///5、获取响应对象
        HttpResponse response = client.execute(post);
        return printResponse(response);
    }

    public static String patch(String url,String param,Map<String,String> headers) throws Exception {
        // 1、创建请求
        HttpPatch patch = new HttpPatch(url);
        // 2.设置请求头
//        patch.setHeader("X-Lemonban-Media-Type","lemonban.v2");
//        patch.setHeader("Content-Type","application/json");

        setHeaders(headers,patch);
        // 3.设置请求体
        StringEntity body = new StringEntity(param,"UTF-8");
        patch.setEntity(body);
        // 4.创建请求客户端
        HttpClient client = HttpClients.createDefault();
        // execute(HttpUriRequest):多态的方法，接受HttpUriRequest所有子实现类
        ///5、获取响应对象
        HttpResponse response = client.execute(patch);
        return printResponse(response);
    }

    private static String printResponse(HttpResponse response) throws IOException {
        // 6、格式化响应对象 response = 响应状态码+响应头+响应体
        // 6.1响应状态码
        int statusCode = response.getStatusLine().getStatusCode();
//        BaseCase.logger.info(statusCode);
        // 6.2响应头
        Header[] allHeaders = response.getAllHeaders();
//        BaseCase.logger.info(Arrays.toString(allHeaders));
//        System.out.println(Arrays.toString(allHeaders));
        // 6.3响应体
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        BaseCase.logger.info("响应结果"+body);
//        System.out.println(body);
        return body;
    }

    /**
     *
     * 设置请求头
     * @param headers 包含了请求头的map集合
     * @param request 请求对象
     */
    public static void setHeaders(Map<String,String> headers, HttpRequest request){
        // 获取请求头name
        Set<String> headerNames = headers.keySet();
        // 遍历所有的请求头
        for (String headerName : headerNames) {
            // 获取请求头对应的value
            String headerValue = headers.get(headerName);
            // 设置请求头name和value
            request.setHeader(headerName,headerValue);
        }
    }

    /**
     * http请求方法
     * @param caseInfo 请求参数
     * @param headers 请求头
     * @return
     * @throws Exception
     */
    public static String call(CaseInfo caseInfo, Map<String, String> headers) throws Exception {

        String params = caseInfo.getParams();
        String url = "http://api.lemonban.com/futureloan" + caseInfo.getUrl();
        String method = caseInfo.getMethod();
        String contentType = caseInfo.getContentType();
        String responseBody = "";
        // 2.判断请求方式，如果是post
        if ("post".equalsIgnoreCase(method)) {
            // 2.1判断请求类型，如果是json
            if ("json".equalsIgnoreCase(contentType)) {
//                headers.put("Content-Type", "application/json");
                // 2.1判断请求类型，如果是form
            } else if ("form".equalsIgnoreCase(contentType)) {
                // json参数转成key=value的形式
                params = jsonStr2KeyValueStr(params);
                // 覆盖默认请求头中的contenttype
                headers.put("Content-Type", "application/x-www-form-urlencoded");
            }
            responseBody = HttpUtils.post(url, params, headers);
        } else if ("get".equalsIgnoreCase(method)) {
            responseBody = HttpUtils.get(url, headers);
        } else if ("patch".equalsIgnoreCase(method)) {
            headers.put("Content-Type", "application/json");
            responseBody = HttpUtils.patch(url, params, headers);
        }
        return responseBody;
    }


    /**
     * json字符串转化成key=value
     * 例如：{"mobile_phone":"13877788811","pwd":"12345678"} -》mobile_phone=13877788811&pwd=12345678
     * @param json json字符串
     */
    public static String jsonStr2KeyValueStr(String json){
        Map<String,String> map = JSONObject.parseObject(json, Map.class);
        Set<String> keySet = map.keySet();
        String formParams = "";
        for (String key : keySet) {
            //key=value&key=value&key=value&
            String value = map.get(key);
            formParams += key + "=" + value + "&";
        }
        return formParams.substring(0,formParams.length()-1);
    }

}
