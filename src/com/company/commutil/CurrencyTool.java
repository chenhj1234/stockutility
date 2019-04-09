package com.company.commutil;

import com.company.CurrencyUtil;
import com.company.PageAndFile;
import sqlutil.StockSqlUtil;

import java.util.Date;

public class CurrencyTool {
    static public void main(String[] args) {
        Date todaydate = new Date();
        StockSqlUtil sqlu = new StockSqlUtil();
        CurrencyUtil currUtil = new CurrencyUtil();
        String storePath = "currency_" + sqlu.convertJavaDateToMySQLStr(todaydate);
        PageAndFile webpage = new PageAndFile();
        String currFile = webpage.getPageAndSave(PageAndFile.currencyPage, storePath,"utf8");
        currUtil.getCurrency(currFile);
    }
}
