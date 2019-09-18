package com.company.commutil;

import com.company.GetArgs;
import com.company.PageAndFile;
import com.company.StockListUtil;
import htmlparser.HTMLParserEarningListener;
import htmlparser.HandleWebPage;
import htmlparser.StackBasicInformation;
import sqlutil.StockSqlUtil;

public class UpdateStockBasicInfo {
    private static final String kOptBasic = "--basic";
    private static final String kOptDividend = "--dividend";
    private static final String kOptEarning = "--earning";
    public static void main(String[] args) {
        GetArgs argParser = new GetArgs();
        argParser.addOption(kOptBasic, false);
        argParser.addOption(kOptDividend, false);
        argParser.addOption(kOptEarning, false);
        argParser.processArgs(args);
        PageAndFile pf = new PageAndFile();
        HandleWebPage hWebPage = new HandleWebPage();

        String savedFile = null;
        StockListUtil su = new StockListUtil();
        su.getStockListFromUpdatedList();
        StockListUtil.StockIdEntry se = null;
        int retry;
        for (int i = 0; i < su.stockIdList.size(); i++) {
            se = su.stockIdList.get(i);
            String stockid = se.id;
            System.out.println("Process item i:" + i + " id:" + stockid);
            if(argParser.isArgOn(kOptBasic)) {
                for(retry = 0; retry < 4;retry++) {
                    savedFile = pf.getStockAndSave(stockid);
                    if (savedFile == null) {
                        System.out.println("Get basic info page failed, try again");
                        continue;
                    }
                    hWebPage.getWebPageFromFile(savedFile);
                    if (hWebPage.mPageListener != null &&
                            hWebPage.mPageListener.infoListener != null &&
                            hWebPage.mPageListener.infoListener.stockInfo != null) {
                        System.out.println("processing basic info page success");
                        break;
                    }
                    System.out.println("processing basic info page failed, retry:" + retry);
                }
                if(retry < 4) {
                    StockSqlUtil stu = StockSqlUtil.getInstence();
                    StackBasicInformation staInfo = hWebPage.mPageListener.infoListener.stockInfo;
                    stu.insertAnnualTable(staInfo.StockNumber, staInfo.year, staInfo.ReturnOnEquity, staInfo.ReturnOnAssets,
                            staInfo.StockDividend, staInfo.CashDividend, staInfo.ExclusionDate, staInfo.EliminationDate,
                            staInfo.GrossProfitMargin, staInfo.OperatingProfitMargin, staInfo.BookValuePerShare, staInfo.EarningBeforeTaxMargin);
                    stu.insertSeasonShareTable(stockid, staInfo.seasonRecordList);
                    stu.insertAnnualShareTable(stockid, staInfo.yearRecordList);
                }
            }
            if(argParser.isArgOn(kOptDividend)) {
                for(retry = 0; retry < 4;retry++) {
                    savedFile = pf.getAnnualDividendAndSave(stockid);
                    if (savedFile == null) {
                        System.out.println("Get dividend page failed, try again");
                        continue;
                    }
                    System.out.println("processing dividend page:" + savedFile);
                    hWebPage.getAnnualDividendPageFromFile(savedFile);
                    if (hWebPage.mDividendListener != null && hWebPage.mDividendListener.divRecList != null) {
                        System.out.println("processing dividend page success");
                        break;
                    }
                    System.out.println("processing dividend page failed, retry:" + retry);
                }
                if(retry < 4) {
                    StockSqlUtil stu = StockSqlUtil.getInstence();
                    stu.insertAnnualDividendTable(stockid, hWebPage.mDividendListener.divRecList);
                }
            }
            if(argParser.isArgOn(kOptEarning)) {
                savedFile = pf.getEarningPageAndSave(stockid);
                if (savedFile == null) {
                    System.out.println("Get earning info page failed, try again");
                    continue;
                }
                System.out.println("processing earning info page:" + savedFile);
                hWebPage.getEarningPageFromFile(savedFile);
                if (hWebPage.mEarningListener == null ||
                        hWebPage.mEarningListener.searnAfterTaxListener == null ||
                        hWebPage.mEarningListener.searnBeforeTaxListener == null ||
                        hWebPage.mEarningListener.monthListener == null) {
                    System.out.println("processing earning info page failed");
                    break;
                }
                StockSqlUtil stu = StockSqlUtil.getInstence();
                System.out.println("Adding earning info db with stock:" + stockid);
                HTMLParserEarningListener htmlListener = hWebPage.mEarningListener;
                stu.insertSeasonEarningTable(stockid,
                        htmlListener.searnAfterTaxListener.year1,
                        htmlListener.searnAfterTaxListener.alist1,
                        htmlListener.searnBeforeTaxListener.alist1);
                stu.insertSeasonEarningTable(stockid,
                        htmlListener.searnAfterTaxListener.year2,
                        htmlListener.searnAfterTaxListener.alist2,
                        htmlListener.searnBeforeTaxListener.alist2);
                stu.insertMonthlyEarningTable(stockid, htmlListener.monthListener.year1, htmlListener.monthListener.alist1);
                stu.insertMonthlyEarningTable(stockid, htmlListener.monthListener.year2, htmlListener.monthListener.alist2);
            }
        }
    }
}
