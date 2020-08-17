package com.lemon.cases;

/*
    @auther:cheryl
    @date:2020/7/31-20:22
*/


import cn.binarywang.tools.generator.ChineseMobileNumberGenerator;
import com.alibaba.fastjson.JSONPath;
import com.lemon.pojo.CaseInfo;
import com.lemon.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/**
 * 1.准备一个默认请求头headers
 * 2.判断method调用get，post，patch
 * 3.如果post请求再判断是json，form
 * 4，如果是form，把json->map->key=value&key=value
 * 5，调用httputils。post
 * 6.post（）-》读取headers并循环所有的key添加到请求对象request
 *
 *
 *
 * 1.testng框架驱动test方法 -》dataprovider
 * 2.excelutils使用easypoi读取数据并返回List
 * 3.datas接收list集合转成数组返回
 * 4.test执行，使用CaseInfo中url和params
 * 5.把url和params传入httpUtils.post();
 * 注册接口测试类型
 * 用例类解析Excel，解析完后得到一个对象列表并转换成数组，提取数组中对应的元素调用相应的请求方法
 */
public class RegisterCase extends BaseCase {

    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo) throws Exception {
        // 1.参数化替换
        // 随机生成手机号码,并替换params中的#phone#
        String params = caseInfo.getParams();
            if (params.contains("#phone#")){
                String phone = ChineseMobileNumberGenerator.getInstance().generate();
                params = params.replace("#phone#",phone);
                caseInfo.setParams(params);
                // 将生成的手机号码存到UserData.VARS中，为了替换sql中的#phone#
                getParamsToUserData(params,"$.mobile_phone","#phone#");
            }

        paramsReplace(caseInfo);
        // 2.数据库前置查询结果（数据断言必须在接口执行前后都查询）
        Long beforeSQLResult = (Long)SQLUtils.getSingleResult(caseInfo.getSql());
        // 3.调用接口
        String responseBody = HttpUtils.call(caseInfo, UserData.DEFAULT_HEADERS);

        String code = JSONPath.read(responseBody,"$.code").toString();
        // 将管理员账号存入UserData.VARS中
        if(caseInfo.getTitle().equals("成功-type为0") && code.equals("0")){
            getParamsToUserData(responseBody,"$.data.mobile_phone","#admin_phone#");
            // 将普通用户账号存入UserData.VARS中
        }if(caseInfo.getTitle().equals("成功-type为1") && code.equals("0")){
            getParamsToUserData(responseBody,"$.data.mobile_phone","#common_phone#");
        }


        // 4.断言响应结果  断言：期望值和实际值匹配，匹配上了就是断言成功，否则就是断言失败
        boolean responseAsserFlag = responseAssert(caseInfo.getExpected(), responseBody);
        // 5.添加响应回写内容
        addWriteBackData(sheetIndex,caseInfo.getId(),Contants.RESPONSE_CELL_NUM,responseBody);
//         6.数据库后置查询结果
        Long afterSQLResult = (Long)SQLUtils.getSingleResult(caseInfo.getSql());
//         7.数据库断言
        boolean sqlAssertFlag = sqlAssert(caseInfo, beforeSQLResult, afterSQLResult);
        // 8.添加响应断言回写内容
        String assertResult = responseAsserFlag ? Contants.ASSERT_PASSED : Contants.ASSERT_FAILED;
        addWriteBackData(sheetIndex,caseInfo.getId(), Contants.ASSERT_CELL_NUM,assertResult);
        // 9.添加日志
        // 10.报表断言 如果断言失败应该在报表中提现
        Assert.assertEquals(assertResult,Contants.ASSERT_PASSED);
    }

    /**
     * 数据库断言
     * @param caseInfo
     * @param beforeSQLResult 请求前查询数据库
     * @param afterSQLResult  请求后查询数据库
     */
    public boolean sqlAssert(CaseInfo caseInfo, Long beforeSQLResult, Long afterSQLResult) {
        boolean sqlAssertFlag = false;
        if(StringUtils.isNotBlank(caseInfo.getSql())) {
            boolean flag = false;
            logger.info("beforeSQLResult:" + beforeSQLResult);
            logger.info("afterSQLResult:" + afterSQLResult);
            if (beforeSQLResult == 0 && afterSQLResult == 1) {
                logger.info("数据库断言成功");
                flag = true;
            } else {
                logger.info("数据库断言失败");
            }
        }
        return sqlAssertFlag;
    }


    @DataProvider
        public Object[] datas(){
            List list = ExcelUtils.read(sheetIndex,1, CaseInfo.class);
            return list.toArray();
        }
}
