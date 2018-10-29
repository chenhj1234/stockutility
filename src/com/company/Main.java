package com.company;

import htmlparser.HandleWebPage;
import htmlparser.StackBasicInformation;
import sqlutil.StockSqlUtil;
import sun.jvm.hotspot.debugger.Page;

public class Main {

    public static void main(String[] args) {
        StockListUtil su = new StockListUtil();
        PageAndFile pf = new PageAndFile();
        HandleWebPage hWebPage = new HandleWebPage();
        su.getStockList();

        for(int i =0;i< su.stockIdList.size();i++){
            StockListUtil.StockIdEntry se = su.stockIdList.get(i);
            String savedFile = pf.getStockAndSave(se.id);
            System.out.println("id:" + se.id + " filename:" + (savedFile == null ? "null" : savedFile));
//        String testStockId = "2801";
//        String savedFile = pf.getStockAndSave(testStockId);
//        System.out.println("id:" + testStockId + " filename:" + (savedFile == null ? "null" : savedFile));
            if(savedFile != null) {
                hWebPage.getWebPageFromFile(savedFile);
                StockSqlUtil stu = new StockSqlUtil();
                StackBasicInformation staInfo = hWebPage.mPageListener.infoListener.stockInfo;
                stu.insertAnnualTable(staInfo.StockNumber, staInfo.year, staInfo.ReturnOnEquity, staInfo.ReturnOnAssets,
                        staInfo.StockDividend, staInfo.CashDividend, staInfo.ExclusionDate, staInfo.EliminationDate,
                        staInfo.GrossProfitMargin, staInfo.OperatingProfitMargin, staInfo.BookValuePerShare, staInfo.EarningBeforeTaxMargin);
            }
            else {
                //System.out.println("Stock id:" + se.id + " basic info not exist");
                System.out.println("Stock id:" + "1103" + " basic info not exist");
            }
        }

    }
}
