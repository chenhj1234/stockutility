package com.company;

import htmlparser.HTMLParserDailyPriseListener;
import htmlparser.HTMLParserEarningListener;
import htmlparser.HandleWebPage;
import htmlparser.StackBasicInformation;
import sqlutil.StockSqlUtil;

public class Main {
    private static boolean DEBUG_SINGLE_STOCK = false;
    private static boolean DEBUG_DATABASE_STOCK = true;
    private static boolean DEBUG_DATABASE_STOCK_INSERT_BASIC = true;
    private static boolean DEBUG_DATABASE_STOCK_INSERT_DAILY = true;
    private static boolean DEBUG_DATABASE_STOCK_INSERT_DIVIDEND = true;
    private static boolean DEBUG_DATABASE_STOCK_INSERT_EARNING = true;
    private static boolean DEBUG_DATABASE_STOCK_ANALYSIS_RATIO = true;
    private static boolean DEBUG_DATABASE_STOCK_APPLY_STRATEGY = true;
    private static boolean DEBUG_DATABASE_STOCK_COUNT_FORMANCE = true;
    private static boolean DEBUG_GET_PAGE = true;
    private static boolean DEBUG_SWITCH_TO_STOCKID_UPDATE = true;

    private static String testStockId = "1102";
    private static void processArgs(String[] args) {
        for(int i = 0;i < args.length;i++) {
            String arg = args[i];
            switch(arg) {
                case "-s":
                    i++;
                    testStockId = args[i];
                    DEBUG_SINGLE_STOCK = true;
                    System.out.println("Applying single stock id:" + testStockId);
                    break;
                case "-getpage":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable get page");
                        DEBUG_GET_PAGE = false;
                    }
                    break;
                case "-updatebasic":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable update basic db");
                        DEBUG_DATABASE_STOCK_INSERT_BASIC = false;
                    }
                    break;
                case "-updatedividend":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable update dividend db");
                        DEBUG_DATABASE_STOCK_INSERT_DIVIDEND = false;
                    }
                    break;
                case "-updatedaily":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable daily db");
                        DEBUG_DATABASE_STOCK_INSERT_DAILY = false;
                    }
                    break;
                case "-updateearning":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable earning db");
                        DEBUG_DATABASE_STOCK_INSERT_EARNING = false;
                    }
                    break;
                case "-analysis":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable analysis db");
                        DEBUG_DATABASE_STOCK_ANALYSIS_RATIO = false;
                    }
                    break;
                case "-apply-strategy":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable analysis db");
                        DEBUG_DATABASE_STOCK_APPLY_STRATEGY = false;
                    }
                    break;
                case "-count-performance":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable analysis db");
                        DEBUG_DATABASE_STOCK_COUNT_FORMANCE = false;
                    }
                    break;
                case "-update-db":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable analysis db");
                        DEBUG_DATABASE_STOCK_INSERT_BASIC = false;
                        DEBUG_DATABASE_STOCK_INSERT_DAILY = false;
                        DEBUG_DATABASE_STOCK_INSERT_DIVIDEND = false;
                        DEBUG_DATABASE_STOCK_INSERT_EARNING = false;
                        DEBUG_DATABASE_STOCK_ANALYSIS_RATIO = false;
                        DEBUG_DATABASE_STOCK_APPLY_STRATEGY = false;
                        DEBUG_DATABASE_STOCK_COUNT_FORMANCE = false;
                    }
                    break;

            }
        }
    }
    public static void main(String[] args) {
        PageAndFile pf = new PageAndFile();
        HandleWebPage hWebPage = new HandleWebPage();
        processArgs(args);
        if(DEBUG_SINGLE_STOCK) {
            StockSqlUtil stua = new StockSqlUtil();
            if(DEBUG_GET_PAGE) {
                int idx;
                for(idx = 0;idx < 2;idx++) {
                    String savedFile = pf.getStockAndSave(testStockId);
                    if(savedFile == null) {
                        System.out.println("Get basic info page failed, try again");
                        continue;
                    }
                    System.out.println("processing basic info page:" + savedFile);
                    hWebPage.getWebPageFromFile(savedFile);
                    if(hWebPage.mPageListener != null &&
                            hWebPage.mPageListener.infoListener != null &&
                            hWebPage.mPageListener.infoListener.stockInfo != null){
                        System.out.println("processing basic info page success");
                        break;
                    }
                    System.out.println("processing basic info failed, try again");
                }
                if(idx == 2) {
                    System.out.println("processing basic info failed 2 times, leave");
                    System.exit(1);
                }
                for(idx = 0;idx < 2;idx++) {
                    String savedDailyFile = pf.getDailyPriseAndSave(testStockId);
                    if(savedDailyFile == null) {
                        System.out.println("Get daily info page failed, try again");
                        continue;
                    }
                    System.out.println("processing daily info page:" + savedDailyFile);
                    hWebPage.getDailyPrisePageFromFile(savedDailyFile);
                    if(hWebPage.mDailyPrisePageListener != null) {
                        System.out.println("processing daily info page success");
                        break;
                    }
                    System.out.println("processing daily info page failed, try again");
                }
                if(idx == 2) {
                    System.out.println("processing daily info page failed 2 times, leave");
                    System.exit(1);
                }
                for(idx = 0;idx < 2;idx++) {
                    String savedDividendFile = pf.getAnnualDividendAndSave(testStockId);
                    if(savedDividendFile == null) {
                        System.out.println("Get dividend page failed, try again");
                        continue;
                    }
                    System.out.println("processing dividend page:" + savedDividendFile);
                    hWebPage.getAnnualDividendPageFromFile(savedDividendFile);
                    if(hWebPage.mDividendListener != null && hWebPage.mDividendListener.divRecList != null) {
                        System.out.println("processing dividend page success");
                        break;
                    }
                    System.out.println("processing dividend page failed, try again");
                }
                if(idx == 2) {
                    System.out.println("processing dividend page failed 2 times, leave");
                    System.exit(1);
                }
                for(idx = 0;idx < 2;idx++) {
                    String saveEarningFile = pf.getEarningPageAndSave(testStockId);
                    if(saveEarningFile == null) {
                        System.out.println("Get earning info page failed, try again");
                        continue;
                    }
                    System.out.println("processing earning info page:" + saveEarningFile);
                    hWebPage.getEarningPageFromFile(saveEarningFile);
                    if(hWebPage.mEarningListener != null &&
                            hWebPage.mEarningListener.searnAfterTaxListener != null &&
                            hWebPage.mEarningListener.searnBeforeTaxListener != null &&
                            hWebPage.mEarningListener.monthListener != null) {
                        System.out.println("processing earning info page success");
                        break;
                    }
                    System.out.println("processing earning info page failed, try again");
                }
                if(idx == 2) {
                    System.out.println("processing earning info page failed 2 times, leave");
                    System.exit(1);
                }
            }
            if(DEBUG_DATABASE_STOCK_INSERT_DAILY) {
                System.out.println("Adding daily db");
                StockSqlUtil stu = new StockSqlUtil();
                HTMLParserDailyPriseListener listener = hWebPage.mDailyPrisePageListener;
                stu.insertDailyTable(testStockId, listener.mLatestRecordDate, listener.buyin, listener.sellout, listener.dealprise, listener.dailyShift, listener.amount);
            }
            if(DEBUG_DATABASE_STOCK_INSERT_BASIC) {
                System.out.println("Adding basic info db");
                StockSqlUtil stu = new StockSqlUtil();
                StackBasicInformation staInfo = hWebPage.mPageListener.infoListener.stockInfo;
                stu.insertAnnualTable(staInfo.StockNumber, staInfo.year, staInfo.ReturnOnEquity, staInfo.ReturnOnAssets,
                        staInfo.StockDividend, staInfo.CashDividend, staInfo.ExclusionDate, staInfo.EliminationDate,
                        staInfo.GrossProfitMargin, staInfo.OperatingProfitMargin, staInfo.BookValuePerShare, staInfo.EarningBeforeTaxMargin);
                stu.insertSeasonShareTable(testStockId, staInfo.seasonRecordList);
                stu.insertAnnualShareTable(testStockId, staInfo.yearRecordList);
            }
            if(DEBUG_DATABASE_STOCK_INSERT_DIVIDEND) {
                System.out.println("Adding dividend info db");
                StockSqlUtil stu = new StockSqlUtil();
                stu.insertAnnualDividendTable(testStockId, hWebPage.mDividendListener.divRecList);
            }
            if(DEBUG_DATABASE_STOCK_INSERT_EARNING) {
                System.out.println("Adding earning info db");
                HTMLParserEarningListener htmlListener = hWebPage.mEarningListener;
                stua.insertSeasonEarningTable(testStockId,
                        htmlListener.searnAfterTaxListener.year1,
                        htmlListener.searnAfterTaxListener.alist1,
                        htmlListener.searnBeforeTaxListener.alist1);
                stua.insertSeasonEarningTable(testStockId,
                        htmlListener.searnAfterTaxListener.year2,
                        htmlListener.searnAfterTaxListener.alist2,
                        htmlListener.searnBeforeTaxListener.alist2);
                stua.insertMonthlyEarningTable(testStockId, htmlListener.monthListener.year1, htmlListener.monthListener.alist1);
                stua.insertMonthlyEarningTable(testStockId, htmlListener.monthListener.year2, htmlListener.monthListener.alist2);

            }
            if(DEBUG_DATABASE_STOCK_ANALYSIS_RATIO) stua.analysisReturnRatio(testStockId);
            if(DEBUG_DATABASE_STOCK_APPLY_STRATEGY) stua.applyStrategy(testStockId, 0);
            if(DEBUG_DATABASE_STOCK_COUNT_FORMANCE) stua.getPerformance(testStockId);
        } else {
            StockListUtil su = null;
            if(DEBUG_DATABASE_STOCK) {
                su = new StockListUtil();
                if(DEBUG_SWITCH_TO_STOCKID_UPDATE) {
                    // We update our list from "market" and "over the counter", and we limit with only 4 digit number stockid here
                    su.getStockListFromUpdatedList();
                } else {
                    su.getStockList();
                }
            }
            float total = 0, perform = 0;
            boolean getPageSuccess = true;
            for (int i = 0; i < su.stockIdList.size(); i++) {
                StockListUtil.StockIdEntry se = null;
                if(DEBUG_DATABASE_STOCK) se = su.stockIdList.get(i);
                System.out.println("Process item i:" + i + " id:" + se.id);
                if(DEBUG_GET_PAGE) {
                    int idx;
                    String savedFile = null, savedDailyFile = null, savedDividendFile = null, saveEarningFile = null;
                    getPageSuccess = true;
                    for(idx = 0;idx < 2;idx++) {
                        savedFile = pf.getStockAndSave(se.id);
                        if(savedFile == null) {
                            System.out.println("Get basic info page failed, try again");
                            continue;
                        }
                        System.out.println("processing basic info page:" + savedFile);
                        hWebPage.getWebPageFromFile(savedFile);
                        if(hWebPage.mPageListener != null &&
                                hWebPage.mPageListener.infoListener != null &&
                                hWebPage.mPageListener.infoListener.stockInfo != null){
                            System.out.println("processing basic info page success");
                            break;
                        }
                        System.out.println("processing basic info failed, try again");
                    }
                    if(idx == 2) {
                        System.out.println("processing basic info failed 2 times, leave");
                        getPageSuccess = false;
                    }
                    if(getPageSuccess) {
                        for (idx = 0; idx < 2; idx++) {
                            savedDailyFile = pf.getDailyPriseAndSave(se.id);
                            if (savedDailyFile == null) {
                                System.out.println("Get daily info page failed, try again");
                                continue;
                            }
                            System.out.println("processing daily info page:" + savedDailyFile);
                            hWebPage.getDailyPrisePageFromFile(savedDailyFile);
                            if (hWebPage.mDailyPrisePageListener != null) {
                                System.out.println("processing daily info page success");
                                break;
                            }
                            System.out.println("processing daily info page failed, try again");
                        }
                        if (idx == 2) {
                            System.out.println("processing daily info page failed 2 times, leave");
                            getPageSuccess = false;
                        }
                    }
                    if(getPageSuccess) {
                        for (idx = 0; idx < 2; idx++) {
                            savedDividendFile = pf.getAnnualDividendAndSave(se.id);
                            if (savedDividendFile == null) {
                                System.out.println("Get dividend page failed, try again");
                                continue;
                            }
                            System.out.println("processing dividend page:" + savedDividendFile);
                            hWebPage.getAnnualDividendPageFromFile(savedDividendFile);
                            if (hWebPage.mDividendListener != null && hWebPage.mDividendListener.divRecList != null) {
                                System.out.println("processing dividend page success");
                                break;
                            }
                            System.out.println("processing dividend page failed, try again");
                        }
                        if (idx == 2) {
                            System.out.println("processing dividend page failed 2 times, leave");
                            getPageSuccess = false;
                        }
                    }
                    if(getPageSuccess) {
                        for (idx = 0; idx < 2; idx++) {
                            saveEarningFile = pf.getEarningPageAndSave(se.id);
                            if (saveEarningFile == null) {
                                System.out.println("Get earning info page failed, try again");
                                continue;
                            }
                            System.out.println("processing earning info page:" + saveEarningFile);
                            hWebPage.getEarningPageFromFile(saveEarningFile);
                            if (hWebPage.mEarningListener != null &&
                                    hWebPage.mEarningListener.searnAfterTaxListener != null &&
                                    hWebPage.mEarningListener.searnBeforeTaxListener != null &&
                                    hWebPage.mEarningListener.monthListener != null) {
                                System.out.println("processing earning info page success");
                                break;
                            }
                            System.out.println("processing earning info page failed, try again");
                        }
                        if (idx == 2) {
                            System.out.println("processing earning info page failed 2 times, leave");
                            getPageSuccess = false;
                        }
                    }

//                    String savedFile = pf.getStockAndSave(se.id);
//                    String savedDailyFile = pf.getDailyPriseAndSave(se.id);
//                    String savedDividendFile = pf.getAnnualDividendAndSave(se.id);
//                    System.out.println("item i:" + i + " basic info page:" + savedFile +
//                            " daily info page:" + savedDailyFile +
//                            " dividend:" + savedDividendFile);
//                    String saveEarningFile = pf.getEarningPageAndSave(se.id);
//                    System.out.println("processing earning info page:" + saveEarningFile);
                    if(!getPageSuccess) {
                        System.out.println("index i:" + i + " id:" + se.id + " get page failed, next");
                        continue;
                    }
                    if (savedFile != null && savedDailyFile != null && savedDividendFile != null) {
//                        hWebPage.getWebPageFromFile(savedFile);
//                        hWebPage.getDailyPrisePageFromFile(savedDailyFile);
//                        hWebPage.getAnnualDividendPageFromFile(savedDividendFile);
//                        hWebPage.getEarningPageFromFile(saveEarningFile);
                        if(DEBUG_DATABASE_STOCK_INSERT_DAILY) {
                            StockSqlUtil stu = new StockSqlUtil();
                            HTMLParserDailyPriseListener listener = hWebPage.mDailyPrisePageListener;
                            stu.insertDailyTable(se.id, listener.mLatestRecordDate, listener.buyin, listener.sellout, listener.dealprise, listener.dailyShift, listener.amount);
                        }
                        if(DEBUG_DATABASE_STOCK_INSERT_BASIC) {
                            StockSqlUtil stu = new StockSqlUtil();
                            StackBasicInformation staInfo = hWebPage.mPageListener.infoListener.stockInfo;
                            stu.insertAnnualTable(staInfo.StockNumber, staInfo.year, staInfo.ReturnOnEquity, staInfo.ReturnOnAssets,
                                    staInfo.StockDividend, staInfo.CashDividend, staInfo.ExclusionDate, staInfo.EliminationDate,
                                    staInfo.GrossProfitMargin, staInfo.OperatingProfitMargin, staInfo.BookValuePerShare, staInfo.EarningBeforeTaxMargin);
                            stu.insertSeasonShareTable(se.id, staInfo.seasonRecordList);
                            stu.insertAnnualShareTable(se.id, staInfo.yearRecordList);
                        }
                        if(DEBUG_DATABASE_STOCK_INSERT_DIVIDEND) {
                            StockSqlUtil stu = new StockSqlUtil();
                            stu.insertAnnualDividendTable(se.id, hWebPage.mDividendListener.divRecList);
                        }
                        if(DEBUG_DATABASE_STOCK_INSERT_EARNING) {
                            StockSqlUtil stu = new StockSqlUtil();
                            System.out.println("Adding earning info db with stock:" + se.id);
                            HTMLParserEarningListener htmlListener = hWebPage.mEarningListener;
                            stu.insertSeasonEarningTable(se.id,
                                    htmlListener.searnAfterTaxListener.year1,
                                    htmlListener.searnAfterTaxListener.alist1,
                                    htmlListener.searnBeforeTaxListener.alist1);
                            stu.insertSeasonEarningTable(se.id,
                                    htmlListener.searnAfterTaxListener.year2,
                                    htmlListener.searnAfterTaxListener.alist2,
                                    htmlListener.searnBeforeTaxListener.alist2);
                            stu.insertMonthlyEarningTable(se.id, htmlListener.monthListener.year1, htmlListener.monthListener.alist1);
                            stu.insertMonthlyEarningTable(se.id, htmlListener.monthListener.year2, htmlListener.monthListener.alist2);

                        }

                    } else {
                        System.out.println("Stock id:" + se.id + " basic info not exist");
//                    System.out.println("Stock id:" + "1103" + " basic info not exist");
                    }
                }
                StockSqlUtil stu = new StockSqlUtil();
                if(DEBUG_DATABASE_STOCK_ANALYSIS_RATIO) stu.analysisReturnRatio(se.id);
                if(DEBUG_DATABASE_STOCK_APPLY_STRATEGY) stu.applyStrategy(se.id, 0);
                if(DEBUG_DATABASE_STOCK_COUNT_FORMANCE) {
                    perform = stu.getPerformance(se.id);
                    total += perform;
                    System.out.println("stockid:" + se.id + " performance:" + perform + " total:" + total);
                }
            }
        }
    }
}
