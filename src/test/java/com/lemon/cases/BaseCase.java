package com.lemon.cases;

/*
    @auther:cheryl
    @date:2020/8/9-22:38
*/


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.lemon.pojo.CaseInfo;
import com.lemon.pojo.Expected;
import com.lemon.pojo.WriteBackData;
import com.lemon.utils.ExcelUtils;
import com.lemon.utils.UserData;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseCase {

    public static Logger logger = Logger.getLogger(BaseCase.class);
    // 获取testng中，sheetIndex变量
    public int sheetIndex;

    @BeforeClass
    @Parameters({"sheetIndex"})
    public void beforeClass(int sheetIndex){
        this.sheetIndex = sheetIndex;
    }

    /**
     * 批量回到到Excel
     * @throws Exception
     */
    @AfterSuite
    public void finish() throws Exception {
        ExcelUtils.batchWrite();
    }


    /**
     * 添加回写对象到回写集合中
     * @param sheetIndex
     * @param rowNum
     * @param cellNum
     * @param content
     */
    public void addWriteBackData(int sheetIndex, int rowNum,int cellNum,String content) {
        // 回写内容到Excel中
        WriteBackData wbd = new WriteBackData(sheetIndex,rowNum,cellNum,content);
        // 批量回写，存储到list集合中
        ExcelUtils.wbdList.add(wbd);
    }

    /**
     * 从responseBody中用jsonpath获取数据，存入UserData中
     * @param responseBody   响应数据
     * @param jsonPathExpression  jsonpath表达式
     * @param userDataKey  UserData.VARS的key
     */
    public void getParamsToUserData(String responseBody,String jsonPathExpression,String userDataKey) {
        // 使用jsonpath获取token和memberID
//        Object token = JSONPath.read(responseBody,jsonPathExpression);
//        Object memberId = JSONPath.read(responseBody,jsonPathExpression);
        Object userDataValue = JSONPath.read(responseBody,jsonPathExpression);

        // 存储到VARS中
        if (userDataValue != null){
            UserData.VARS.put(userDataKey,userDataValue);
        }
        if (userDataValue != null){
            UserData.VARS.put(userDataKey,userDataValue);
        }
    }


    /**
     * 获取普通会员的鉴权头，添加默认请求头，返回headers
     * @return
     */
    public Map<String, String> getAuthorizationHeader() {
        Object token = UserData.VARS.get("#token#");
        HashMap<String, String> headers = new HashMap<>();
        // 添加鉴权头
        headers.put("Authorization","Bearer "+token);
        // 添加默认头
        headers.putAll(UserData.DEFAULT_HEADERS);
        return headers;
    }

    /**
     * 获取管理员登录的鉴权头，添加默认请求头，返回headers
     * @return
     */
    public Map<String, String> getAdminAuthorizationHeader() {
        Object token = UserData.VARS.get("#admin_token#");
        HashMap<String, String> headers = new HashMap<>();
        // 添加鉴权头
        headers.put("Authorization","Bearer "+token);
        // 添加默认头
        headers.putAll(UserData.DEFAULT_HEADERS);
        return headers;
    }


    /**
     * 获取接口响应断言结果
     * @param expectedResult  Excel中的期望结果
     * @param responseBody  响应内容
     * @return
     */
    public boolean responseAssert(String expectedResult, String responseBody) {
        // 期望结果{"code":0,"msg":"OK"}

        // 将json转换为expected对象
        Expected expected = JSONObject.parseObject(expectedResult, Expected.class);
        // 获取预期结果的code
        Object expectedCode = expected.getCode();
        // 获取预期结果的msg
        Object expectedMsg = expected.getMsg();
        // 获取实际结果的code
        Object actualCode = JSONPath.read(responseBody, "$.code");
        // 获取实际结果的msg
        Object actualMsg = JSONPath.read(responseBody, "$.msg");
        // 断言结果
        Boolean responseAssertFlag = true;
        logger.info("实际结果"+ "code:" +actualCode + " "+ "msg:" + actualMsg );
        logger.info("预期结果"+ "code:" +expectedCode + " "+ "msg:" + expectedMsg );
        if (! actualCode.equals(expectedCode)){
            responseAssertFlag = false;
        }else if(! actualMsg.equals(expectedMsg)){
            responseAssertFlag = false;
        }
        logger.info("断言结果：" + responseAssertFlag);
        return responseAssertFlag;
    }

    /**
     * 参数替换
     * @param caseInfo
     */
    public void paramsReplace(CaseInfo caseInfo) {
        // 获取params sql expexted url
        String params = caseInfo.getParams();
        String sql = caseInfo.getSql();
        String url = caseInfo.getUrl();
        // 获取UserData.VARS中的所有key
        Set<String> keySet = UserData.VARS.keySet();
        // 遍历UserData.VARS中的所有key
        for (String placeHolder : keySet) {
            // 获得占位符的值
            String value = UserData.VARS.get(placeHolder).toString();
            // 替换
            if (StringUtils.isNotBlank(params)){
                params = params.replace(placeHolder,value);
            }
            if (StringUtils.isNotBlank(sql)){
                sql = sql.replace(placeHolder,value);
            }
            if (StringUtils.isNotBlank(url)){
                url = url.replace(placeHolder,value);
            }
        }
        caseInfo.setParams(params);
        caseInfo.setSql(sql);
        caseInfo.setUrl(url);
        logger.info(caseInfo);
    }

}
