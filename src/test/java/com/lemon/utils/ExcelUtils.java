package com.lemon.utils;

/*
    @auther:cheryl
    @date:2020/7/31-20:27
*/


import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.lemon.pojo.WriteBackData;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    // 批量回写到list集合中
    public static List<WriteBackData> wbdList = new ArrayList<>();

    /**
     * 读取Excel数据并封装到指定对象中
     * @param sheetIndex 开始sheet索引
     * @param sheetNum  sheet个数
     * @param clazz Excel映射字节码对象
     * @return 返回值
     */
    public static List read(int sheetIndex,int sheetNum,Class clazz){
        // 1.excel文件流
        try {
            FileInputStream fis = new FileInputStream(Contants.EXCEL_PATH);
            // 2.easypoi导入参数
            ImportParams params = new ImportParams();
            params.setStartSheetIndex(sheetIndex);   // 可以指定读取第几个sheet
            params.setSheetNum(sheetNum);  // 每次读几个sheet
            // 3.导入 importExcel（Excel文件流，映射关系字节码对象，导入参数）
            List caseInfoList = ExcelImportUtil.importExcel(fis, clazz,params);
            // 4.关流
            fis.close();
            // 按行打印用例对象
            return caseInfoList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 批量回写
     * @throws Exception
     */
    public static void batchWrite() throws Exception {
        // 回写的逻辑：遍历wbdList集合，取出sheetIndex,rowNum,cellNum,content
        // 读取Excel文件流
        FileInputStream fis = new FileInputStream(Contants.EXCEL_PATH);
        // 获取所有的sheet
        Workbook sheets = WorkbookFactory.create(fis);
        // 遍历wbdList集合
        for (WriteBackData wbd : wbdList) {
            int sheetIndex = wbd.getSheetIndex();
            int rowNum = wbd.getRowNum();
            int cellNum = wbd.getCellNum();
            String content = wbd.getContent();
            // 获取对应的sheet对象
            Sheet sheet = sheets.getSheetAt(sheetIndex);
            // 获取对应的row对象
            Row row = sheet.getRow(rowNum);
            // 获取对应的cell对象
            Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            // 写入对应的内容
            cell.setCellValue(content);
        }
        // 回写到Excel
        FileOutputStream fos = new FileOutputStream(Contants.EXCEL_PATH);
        sheets.write(fos);
    }
}
