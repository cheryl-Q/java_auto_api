package com.lemon.cases;

/*
    @auther:cheryl
    @date:2020-8-13-18:38
*/


import com.alibaba.fastjson.JSONPath;
import com.lemon.pojo.CaseInfo;
import com.lemon.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class WithdrawCase extends BaseCase{


    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo) throws Exception {
        // 1.参数化替换
        paramsReplace(caseInfo);
        // 2，数据库前置查询
        BigDecimal beforeSQLResult = (BigDecimal)SQLUtils.getSingleResult(caseInfo.getSql());

        // 3，调用接口
        // 获取鉴权头
        Map<String, String> authorizationHeader = getAuthorizationHeader();
        // 调用取现接口
        String responseBody = HttpUtils.call(caseInfo, authorizationHeader);
        // 4，接口断言响应结果
        boolean responseAssertFlag = responseAssert(caseInfo.getExpected(), responseBody);
        // 5，回写响应结果
        addWriteBackData(sheetIndex,caseInfo.getId(),Contants.RESPONSE_CELL_NUM,responseBody);
        // 6，数据库后置查询
        BigDecimal afterSQLResult = (BigDecimal)SQLUtils.getSingleResult(caseInfo.getSql());
        // 7，数据库断言结果
        boolean sqlAssertFlag = sqlAssert(caseInfo, beforeSQLResult, afterSQLResult);
        // 8，回写接口断言结果
        String assertResult = responseAssertFlag ?Contants.ASSERT_PASSED : Contants.ASSERT_FAILED;
        addWriteBackData(sheetIndex, caseInfo.getId(), Contants.ASSERT_CELL_NUM, assertResult);
        // 9，添加日志
        // 10，报表断言
        Assert.assertEquals(assertResult,Contants.ASSERT_PASSED);

    }

    /**
     * 数据库断言
     * @param caseInfo
     * @param beforeSQLResult
     */
    public boolean sqlAssert(CaseInfo caseInfo, BigDecimal beforeSQLResult,BigDecimal afterSQLResult) {
        boolean flag = false;
        if (StringUtils.isNotBlank(caseInfo.getSql())){
            String amountStr = JSONPath.read(caseInfo.getParams(),"$.amount").toString();
            BigDecimal amount = new BigDecimal(amountStr);
            BigDecimal subtract = beforeSQLResult.subtract(afterSQLResult);
            logger.info("beforeSQLResult:" + beforeSQLResult);
            logger.info("afterSQLResult:" + afterSQLResult);
            logger.info("subtract:" + subtract);
            //subtract.compareTo(amount) == 0 说明 subtractResult == amount
            if (subtract.compareTo(amount) == 0){
                logger.info("数据库断言成功");
                flag = true;
            }else {
                logger.info("数据库断言失败");
            }
        }
        return flag;
    }


    @DataProvider
    public Object[] datas(){
        List list = ExcelUtils.read(sheetIndex, 1, CaseInfo.class);
        return list.toArray();
    }
}
