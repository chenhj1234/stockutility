package com.company.commutil;

import com.company.PageAndFile;
import sqlutil.StockSqlUtil;

import java.util.Date;

public class CurrencyTool {
    static public void main(String[] args) {
        Date todaydate = new Date();
        StockSqlUtil sqlu = new StockSqlUtil();
        String storePath = "currency_" + sqlu.convertJavaDateToMySQLStr(todaydate);
        PageAndFile webpage = new PageAndFile();
        webpage.getPageAndSave(PageAndFile.currencyPage, storePath);

    }
}
