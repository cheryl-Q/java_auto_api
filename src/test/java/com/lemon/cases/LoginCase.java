package com.lemon.cases;

/*
    @auther:cheryl
    @date:2020/8/3-21:54
*/


import com.alibaba.fastjson.JSONPath;
import com.lemon.pojo.CaseInfo;
import com.lemon.utils.Contants;
import com.lemon.utils.ExcelUtils;
import com.lemon.utils.HttpUtils;
import com.lemon.utils.UserData;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class LoginCase extends BaseCase {

    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo) throws Exception {
        // 1.参数化替换
        paramsReplace(caseInfo);
        // 2.数据库前置查询结果（数据断言必须在接口执行前后都查询）
        // 3.调用接口
        String responseBody = HttpUtils.call(caseInfo, UserData.DEFAULT_HEADERS);
        // 从响应中获取token、member_id放入UserData中
        String code = JSONPath.read(responseBody,"$.code").toString();
        if (caseInfo.getTitle().equals("管理员登录成功") && code.equals("0")){
            getParamsToUserData(responseBody,"$.data.token_info.token","#admin_token#");
            getParamsToUserData(responseBody,"$.data.id","#admin_id#");
        }if (caseInfo.getTitle().equals("普通会员登录成功") && code.equals("0")) {
            getParamsToUserData(responseBody, "$.data.token_info.token", "#token#");
            getParamsToUserData(responseBody, "$.data.id", "#member_id#");
        }
        boolean responseAsserFlag = responseAssert(caseInfo.getExpected(), responseBody);
        // 5.添加响应回写内容
        addWriteBackData(sheetIndex,caseInfo.getId(), Contants.RESPONSE_CELL_NUM,responseBody);
        // 6.数据库后置查询结果
        // 7.数据库断言
        // 8，回写接口断言结果
        String assertResult = responseAsserFlag ?Contants.ASSERT_PASSED : Contants.ASSERT_FAILED;
        addWriteBackData(sheetIndex, caseInfo.getId(), Contants.ASSERT_CELL_NUM, assertResult);
        // 9，添加日志
        // 10，报表断言
        Assert.assertEquals(assertResult,Contants.ASSERT_PASSED);
    }




    @DataProvider
    public Object[] datas(){
        List list = ExcelUtils.read(sheetIndex,1, CaseInfo.class);
        return list.toArray();
    }
}
