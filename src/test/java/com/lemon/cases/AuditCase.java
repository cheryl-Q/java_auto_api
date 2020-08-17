package com.lemon.cases;

/*
    @auther:cheryl
    @date:2020-8-14-17:00
*/


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

public class AuditCase extends BaseCase{

    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo) throws Exception {
        // 1.参数化替换
        paramsReplace(caseInfo);
        // 2.数据库前置查询结果（数据断言必须在接口执行前后都查询）
        // 3.调用接口
        // 获取鉴权头
        Map<String, String> authorizationHeader = getAuthorizationHeader();
        // 调用充值接口
        String responseBody = HttpUtils.call(caseInfo,authorizationHeader);
        // 4.接口断言响应结果  断言：期望值和实际值匹配，匹配上了就是断言成功，否则就是断言失败
        boolean responseAsserFlag = responseAssert(caseInfo.getExpected(), responseBody);
        // 5.添加响应回写内容
        addWriteBackData(sheetIndex,caseInfo.getId(),8,responseBody);
        // 6.数据库后置查询结果
        Object afterSQLResult = SQLUtils.getSingleResult(caseInfo.getSql());
        // 7.数据库断言
        boolean sqlAssertResult = sqlAssert(caseInfo,afterSQLResult);
        // 8，回写接口断言结果
        String assertResult = responseAsserFlag ?Contants.ASSERT_PASSED : Contants.ASSERT_FAILED;
        addWriteBackData(sheetIndex, caseInfo.getId(), Contants.ASSERT_CELL_NUM, assertResult);
        // 9，添加日志
        // 10，报表断言
        Assert.assertEquals(assertResult,Contants.ASSERT_PASSED);
    }

    /**
     * 数据库断言
     * @param caseInfo
     * @param afterSQLResult
     */
    public boolean sqlAssert(CaseInfo caseInfo, Object afterSQLResult) {
        boolean flag = false;
        if (StringUtils.isNotBlank(caseInfo.getSql())){
            logger.info("afterSQLResult:" + afterSQLResult);
            // 审核通过，status为2
            if (afterSQLResult.equals(2)){
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
        List list = ExcelUtils.read(sheetIndex,1, CaseInfo.class);
        return list.toArray();
    }
}
