package com.lemon.cases;

/*
    @auther:cheryl
    @date:2020-8-14-15:04
*/


import com.alibaba.fastjson.JSONPath;
import com.lemon.pojo.CaseInfo;
import com.lemon.utils.Contants;
import com.lemon.utils.ExcelUtils;
import com.lemon.utils.HttpUtils;
import com.lemon.utils.SQLUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class AddCase extends BaseCase{


    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo) throws Exception {
        // 1.参数化替换
        paramsReplace(caseInfo);
        // 2，数据库前置查询
        Long beforeSQLResult = (Long) SQLUtils.getSingleResult(caseInfo.getSql());

        // 3，调用接口
        // 获取管理员鉴权头
        Map<String, String> authorizationHeader = getAdminAuthorizationHeader();
        // 调用取现接口
        String responseBody = HttpUtils.call(caseInfo, authorizationHeader);
        // 4，接口断言响应结果
        boolean responseAssertFlag = responseAssert(caseInfo.getExpected(), responseBody);
        String code = JSONPath.read(responseBody,"$.code").toString();
        if (caseInfo.getTitle().equals("新增项目成功-按天借款期限10天竞标1天") && code.equals("0")){
            getParamsToUserData(responseBody,"$.data.id","#pass_loan_id#");
        }if (caseInfo.getTitle().equals("新增项目成功-按天借款期限45天竞标1天") && code.equals("0")){
            getParamsToUserData(responseBody,"$.data.id","#fail_loan_id#");
        }if (caseInfo.getTitle().equals("新增项目成功-按月借款期限1个月竞标10天") && code.equals("0")){
            getParamsToUserData(responseBody,"$.data.id","#loan_id#");
        }
        // 5，回写响应结果
        addWriteBackData(sheetIndex, caseInfo.getId(), Contants.RESPONSE_CELL_NUM, responseBody);
        // 6，数据库后置查询
        Long afterSQLResult = (Long)SQLUtils.getSingleResult(caseInfo.getSql());
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
     *
     * @param caseInfo
     * @param beforeSQLResult
     */
    public boolean sqlAssert(CaseInfo caseInfo, Long beforeSQLResult, Long afterSQLResult) {
        boolean flag = false;
        if (StringUtils.isNotBlank(caseInfo.getSql())) {
            Long subtract = afterSQLResult-beforeSQLResult;
            logger.info("beforeSQLResult:" + beforeSQLResult);
            logger.info("afterSQLResult:" + afterSQLResult);

            if (subtract.compareTo(1L) == 0) {
                logger.info("数据库断言成功");
                flag = true;
            } else {
                logger.info("数据库断言失败");
            }
        }
        return flag;
    }


    @DataProvider
    public Object[] datas() {
        List list = ExcelUtils.read(sheetIndex, 1, CaseInfo.class);
        return list.toArray();
    }
}