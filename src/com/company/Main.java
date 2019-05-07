package com.company;

import com.company.commutil.CurrencyTool;
import htmlparser.HTMLParserDailyPriseListener;
import htmlparser.HTMLParserEarningListener;
import htmlparser.HandleWebPage;
import htmlparser.StackBasicInformation;
import sqlutil.StockSqlUtil;

import java.util.Calendar;
import java.util.Date;

public class Main {
    private static boolean DEBUG_SINGLE_STOCK = false;
    private static boolean DEBUG_DATABASE_STOCK = true;
    private static boolean DEBUG_DATABASE_STOCK_INSERT_BASIC = false;
    private static boolean DEBUG_DATABASE_STOCK_INSERT_DAILY = false;
    private static boolean DEBUG_DATABASE_STOCK_INSERT_DIVIDEND = false;
    private static boolean DEBUG_DATABASE_STOCK_INSERT_EARNING = false;
    private static boolean DEBUG_DATABASE_STOCK_ANALYSIS_RATIO = false;
    private static boolean DEBUG_DATABASE_STOCK_APPLY_STRATEGY = false;
    private static boolean DEBUG_DATABASE_STOCK_COUNT_FORMANCE = false;
    private static boolean DEBUG_GET_PAGE = false;
    private static boolean DEBUG_GET_PAGE_BASIC = false;
    private static boolean DEBUG_GET_PAGE_DAILY = false;
    private static boolean DEBUG_GET_PAGE_DIVIDEND = false;
    private static boolean DEBUG_GET_PAGE_EARNING = false;
    private static boolean DEBUG_PARSE_PAGE_BASIC = true;
    private static boolean DEBUG_PARSE_PAGE_DAILY = true;
    private static boolean DEBUG_PARSE_PAGE_DIVIDEND = true;
    private static boolean DEBUG_PARSE_PAGE_EARNING = true;
    //private static boolean DEBUG_SWITCH_TO_STOCKID_UPDATE = true;
    private static boolean DEBUG_CURRENCY_UPDATE = false;
    //private static boolean DEBUG_UPDATE_BUYIN_PERFORMANCE = false;

    private static boolean DEBUG_USE_TWSE_TPEX_FOR_DAILY = true;
    private static String testStockId = "1102";

    private static final int USE_STOCK_LIST_ID_LIST = 0;
    private static final int USE_STOCK_LIST_ID_LIST_UPDATE = 1;
    private static final int USE_STOCK_LIST_BUYIN_LIST = 2;
    private static final int USE_STOCK_LIST_BUYIN_ANALYSIS_LIST = 3;
    private static int stockIdListUse = USE_STOCK_LIST_ID_LIST_UPDATE;

    private static int startIndex = 0;
    private static int endIndex = -1;
    private static int getPageYear = -1;
    private static int getPageMonth = -1;
    private static int getPageDay = -1;
    private static void processArgs(String[] args) {
        for(int i = 0;i < args.length;i++) {
            String arg = args[i];
            switch(arg) {
                case "-start-index":
                    i++;
                    startIndex = Integer.parseInt(args[i]);
                    System.out.println("Applying start index:" + startIndex);
                    break;
                case "-end-index":
                    i++;
                    endIndex = Integer.parseInt(args[i]);
                    System.out.println("Applying end index:" + endIndex);
                    break;
                case "-s":
                    i++;
                    testStockId = args[i];
                    DEBUG_SINGLE_STOCK = true;
                    System.out.println("Applying single stock id:" + testStockId);
                    break;
                case "-year":
                    i++;
                    getPageYear = Integer.parseInt(args[i]);
                    System.out.println("Applying year:" + getPageYear);
                    break;
                case "-month":
                    i++;
                    getPageMonth = Integer.parseInt(args[i]);
                    System.out.println("Applying month:" + getPageMonth);
                    break;
                case "-day":
                    i++;
                    getPageDay = Integer.parseInt(args[i]);
                    System.out.println("Applying day:" + getPageDay);
                    break;
                case "-getpage":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable get page");
                        DEBUG_GET_PAGE = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable get page");
                        DEBUG_GET_PAGE = true;
                    }
                    break;
                case "-getpage-daily":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable get daily page");
                        DEBUG_GET_PAGE_DAILY = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable get daily page");
                        DEBUG_GET_PAGE_DAILY = true;
                    }
                    break;
                case "-apply-daily-twse-tpex":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable get daily page");
                        DEBUG_USE_TWSE_TPEX_FOR_DAILY = false;
                        DEBUG_DATABASE_STOCK = true;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable get daily page");
                        DEBUG_USE_TWSE_TPEX_FOR_DAILY = true;
                        DEBUG_DATABASE_STOCK = false;
                    }
                    break;
                case "-getpage-basic":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable get basic page");
                        DEBUG_GET_PAGE_BASIC = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable get basic page");
                        DEBUG_GET_PAGE_BASIC = true;
                    }
                    break;
                case "-getpage-dividend":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable get dividend page");
                        DEBUG_GET_PAGE_DIVIDEND = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable get dividend page");
                        DEBUG_GET_PAGE_DIVIDEND = true;
                    }
                    break;
                case "-getpage-earning":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable get earning page");
                        DEBUG_GET_PAGE_EARNING = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable get earning page");
                        DEBUG_GET_PAGE_EARNING = true;
                    }
                    break;
                case "-parsepage-daily":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable parse daily page");
                        DEBUG_PARSE_PAGE_DAILY = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable parse daily page");
                        DEBUG_PARSE_PAGE_DAILY = true;
                    }
                    break;
                case "-parsepage-basic":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable parse basic page");
                        DEBUG_PARSE_PAGE_BASIC = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable parse basic page");
                        DEBUG_PARSE_PAGE_BASIC = true;
                    }
                    break;
                case "-parsepage-dividend":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable parse dividend page");
                        DEBUG_PARSE_PAGE_DIVIDEND = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable parse dividend page");
                        DEBUG_PARSE_PAGE_DIVIDEND = true;
                    }
                    break;
                case "-parsepage-earning":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable parse earning page");
                        DEBUG_PARSE_PAGE_EARNING = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable parse earning page");
                        DEBUG_PARSE_PAGE_EARNING = true;
                    }
                    break;
                case "-updatebasic":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable update basic db");
                        DEBUG_DATABASE_STOCK_INSERT_BASIC = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable update basic db");
                        DEBUG_DATABASE_STOCK_INSERT_BASIC = true;
                    }
                    break;
                case "-updatedividend":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable update dividend db");
                        DEBUG_DATABASE_STOCK_INSERT_DIVIDEND = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable update dividend db");
                        DEBUG_DATABASE_STOCK_INSERT_DIVIDEND = true;
                    }
                    break;
                case "-updatedaily":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable update daily db");
                        DEBUG_DATABASE_STOCK_INSERT_DAILY = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable update daily db");
                        DEBUG_DATABASE_STOCK_INSERT_DAILY = true;
                    }
                    break;
                case "-updateearning":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable update earning db");
                        DEBUG_DATABASE_STOCK_INSERT_EARNING = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable update get page");
                        DEBUG_DATABASE_STOCK_INSERT_EARNING = true;
                    }
                    break;
                case "-analysis":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable analysis db");
                        DEBUG_DATABASE_STOCK_ANALYSIS_RATIO = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable analysis db");
                        DEBUG_DATABASE_STOCK_ANALYSIS_RATIO = true;
                    }
                    break;
                case "-apply-strategy":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable strategy db");
                        DEBUG_DATABASE_STOCK_APPLY_STRATEGY = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable strategy db");
                        DEBUG_DATABASE_STOCK_APPLY_STRATEGY = true;
                    }
                    break;
                case "-count-performance":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable analysis performance");
                        DEBUG_DATABASE_STOCK_COUNT_FORMANCE = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable analysis performance");
                        DEBUG_DATABASE_STOCK_COUNT_FORMANCE = true;
                    }
                    break;
                case "-update-currency":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable update currency");
                        DEBUG_CURRENCY_UPDATE = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable update currency");
                        DEBUG_CURRENCY_UPDATE = true;
                    }
                    break;
                case "-update-buyin-analysis":
                    i++;
                    if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("update buyin analysis");
                        stockIdListUse = USE_STOCK_LIST_BUYIN_ANALYSIS_LIST;
//                        DEBUG_SWITCH_TO_STOCKID_UPDATE = false;
//                        DEBUG_UPDATE_BUYIN_PERFORMANCE = true;
//                    } else if("no".equalsIgnoreCase(args[i])) {
//                        System.out.println("enable buyin analysis");
//                        DEBUG_SWITCH_TO_STOCKID_UPDATE = true;
//                        DEBUG_UPDATE_BUYIN_PERFORMANCE = false;
                    }
                    break;
                case "-update-buyin":
                    i++;
                    if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("update buyin table");
                        stockIdListUse = USE_STOCK_LIST_BUYIN_LIST;
//                        DEBUG_SWITCH_TO_STOCKID_UPDATE = false;
//                        DEBUG_UPDATE_BUYIN_PERFORMANCE = true;
//                    } else if("no".equalsIgnoreCase(args[i])) {
//                        System.out.println("enable buyin analysis");
//                        DEBUG_SWITCH_TO_STOCKID_UPDATE = true;
//                        DEBUG_UPDATE_BUYIN_PERFORMANCE = false;
                    }
                    break;
                case "-update-db":
                    i++;
                    if("no".equalsIgnoreCase(args[i])) {
                        System.out.println("disable all db");
                        DEBUG_DATABASE_STOCK_INSERT_BASIC = false;
                        DEBUG_DATABASE_STOCK_INSERT_DAILY = false;
                        DEBUG_DATABASE_STOCK_INSERT_DIVIDEND = false;
                        DEBUG_DATABASE_STOCK_INSERT_EARNING = false;
                        DEBUG_DATABASE_STOCK_ANALYSIS_RATIO = false;
                        DEBUG_DATABASE_STOCK_APPLY_STRATEGY = false;
                        DEBUG_DATABASE_STOCK_COUNT_FORMANCE = false;
//                        DEBUG_UPDATE_BUYIN_PERFORMANCE = false;
                        DEBUG_CURRENCY_UPDATE = false;
                    } else if("yes".equalsIgnoreCase(args[i])) {
                        System.out.println("enable all db");
                        DEBUG_DATABASE_STOCK_INSERT_BASIC = true;
                        DEBUG_DATABASE_STOCK_INSERT_DAILY = true;
                        DEBUG_DATABASE_STOCK_INSERT_DIVIDEND = true;
                        DEBUG_DATABASE_STOCK_INSERT_EARNING = true;
                        DEBUG_DATABASE_STOCK_ANALYSIS_RATIO = true;
                        DEBUG_DATABASE_STOCK_APPLY_STRATEGY = true;
                        DEBUG_DATABASE_STOCK_COUNT_FORMANCE = true;
//                        DEBUG_UPDATE_BUYIN_PERFORMANCE = true;
                        DEBUG_CURRENCY_UPDATE = true;
                    }
                    break;

            }
        }
    }
    private static final long MEGABYTE = 1024L * 1024L;
    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
    public static boolean processOneStock(String stockid) {
        PageAndFile pf = new PageAndFile();
        HandleWebPage hWebPage = new HandleWebPage();
        float total = 0, perform = 0;
        boolean getPageSuccess = true;

        if(DEBUG_GET_PAGE) {
            int idx;
            String savedFile = null, savedDailyFile = null, savedDividendFile = null, saveEarningFile = null;
            getPageSuccess = true;
            if(DEBUG_GET_PAGE_BASIC) {
                for (idx = 0; idx < 2; idx++) {
                    savedFile = pf.getStockAndSave(stockid);
                    if (savedFile == null) {
                        System.out.println("Get basic info page failed, try again");
                        continue;
                    }
                    if(DEBUG_PARSE_PAGE_BASIC) {
                        System.out.println("processing basic info page:" + savedFile);
                        hWebPage.getWebPageFromFile(savedFile);
                        if (hWebPage.mPageListener != null &&
                                hWebPage.mPageListener.infoListener != null &&
                                hWebPage.mPageListener.infoListener.stockInfo != null) {
                            System.out.println("processing basic info page success");
                            break;
                        }
                        System.out.println("processing basic info failed, try again");
                    }
                }
                if (idx == 2) {
                    System.out.println("processing basic info failed 2 times, leave");
                    getPageSuccess = false;
                }
            }
            if(DEBUG_GET_PAGE_DAILY) {
                if (getPageSuccess) {
                    for (idx = 0; idx < 2; idx++) {
                        savedDailyFile = pf.getDailyPriseAndSave(stockid);
                        if (savedDailyFile == null) {
                            System.out.println("Get daily info page failed, try again");
                            continue;
                        }
//                                Runtime runtime = Runtime.getRuntime();
//                                runtime.gc();
//                                long memory = runtime.totalMemory() - runtime.freeMemory();
//                                System.out.println("Used memory is bytes: " + memory);
//                                System.out.println("Used memory is megabytes: "
//                                        + bytesToMegabytes(memory));
                        if(DEBUG_PARSE_PAGE_DAILY) {
                            System.out.println("processing daily info page:" + savedDailyFile);
                            hWebPage.getDailyPrisePageFromFile(savedDailyFile);
                            if (hWebPage.mDailyPrisePageListener != null) {
                                System.out.println("processing daily info page success");
                                break;
                            }
                            System.out.println("processing daily info page failed, try again");
                        }
                    }
                    if (idx == 2) {
                        System.out.println("processing daily info page failed 2 times, leave");
                        getPageSuccess = false;
                    }
                }
            }
            if(DEBUG_GET_PAGE_DIVIDEND) {
                if (getPageSuccess) {
                    for (idx = 0; idx < 2; idx++) {
                        savedDividendFile = pf.getAnnualDividendAndSave(stockid);
                        if (savedDividendFile == null) {
                            System.out.println("Get dividend page failed, try again");
                            continue;
                        }
                        if(DEBUG_PARSE_PAGE_DIVIDEND) {
                            System.out.println("processing dividend page:" + savedDividendFile);
                            hWebPage.getAnnualDividendPageFromFile(savedDividendFile);
                            if (hWebPage.mDividendListener != null && hWebPage.mDividendListener.divRecList != null) {
                                System.out.println("processing dividend page success");
                                break;
                            }
                            System.out.println("processing dividend page failed, try again");
                        }
                    }
                    if (idx == 2) {
                        System.out.println("processing dividend page failed 2 times, leave");
                        getPageSuccess = false;
                    }
                }
            }
            if(DEBUG_GET_PAGE_EARNING) {
                if (getPageSuccess) {
                    for (idx = 0; idx < 2; idx++) {
                        saveEarningFile = pf.getEarningPageAndSave(stockid);
                        if (saveEarningFile == null) {
                            System.out.println("Get earning info page failed, try again");
                            continue;
                        }
                        if(DEBUG_PARSE_PAGE_EARNING) {
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
                    }
                    if (idx == 2) {
                        System.out.println("processing earning info page failed 2 times, leave");
                        getPageSuccess = false;
                    }
                }
            }
            if(!getPageSuccess) {
                System.out.println("id:" + stockid + " get page failed, next");
                return false;
            }
            if (getPageSuccess) {
                if(DEBUG_DATABASE_STOCK_INSERT_DAILY) {
                    StockSqlUtil stu = new StockSqlUtil();
                    HTMLParserDailyPriseListener listener = hWebPage.mDailyPrisePageListener;
                    stu.insertDailyTable(stockid, listener.mLatestRecordDate, listener.buyin, listener.sellout, listener.dealprise, listener.dailyShift, listener.amount, true);
                }
                if(DEBUG_DATABASE_STOCK_INSERT_BASIC) {
                    StockSqlUtil stu = new StockSqlUtil();
                    StackBasicInformation staInfo = hWebPage.mPageListener.infoListener.stockInfo;
                    stu.insertAnnualTable(staInfo.StockNumber, staInfo.year, staInfo.ReturnOnEquity, staInfo.ReturnOnAssets,
                            staInfo.StockDividend, staInfo.CashDividend, staInfo.ExclusionDate, staInfo.EliminationDate,
                            staInfo.GrossProfitMargin, staInfo.OperatingProfitMargin, staInfo.BookValuePerShare, staInfo.EarningBeforeTaxMargin);
                    stu.insertSeasonShareTable(stockid, staInfo.seasonRecordList);
                    stu.insertAnnualShareTable(stockid, staInfo.yearRecordList);
                }
                if(DEBUG_DATABASE_STOCK_INSERT_DIVIDEND) {
                    StockSqlUtil stu = new StockSqlUtil();
                    stu.insertAnnualDividendTable(stockid, hWebPage.mDividendListener.divRecList);
                }
                if(DEBUG_DATABASE_STOCK_INSERT_EARNING) {
                    StockSqlUtil stu = new StockSqlUtil();
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

            } else {
                System.out.println("Stock id:" + stockid + " basic info not exist");
//                    System.out.println("Stock id:" + "1103" + " basic info not exist");
            }
        }
        StockSqlUtil stu = new StockSqlUtil();
        if(DEBUG_DATABASE_STOCK_ANALYSIS_RATIO) stu.analysisReturnRatio(stockid);
        if(DEBUG_DATABASE_STOCK_APPLY_STRATEGY)
            stu.applyBuyinSelloutStrategy(stockid, true);
//            stu.applyStrategy(stockid, 0);
        if(DEBUG_DATABASE_STOCK_COUNT_FORMANCE) {
            perform = stu.getPerformance(stockid);
            total += perform;
            System.out.println("stockid:" + stockid + " performance:" + perform + " total:" + total);
        }
        return true;
    }
    public static void main(String[] args) {
        PageAndFile pf = new PageAndFile();
        HandleWebPage hWebPage = new HandleWebPage();
        processArgs(args);
        if(DEBUG_SINGLE_STOCK) {
            if(processOneStock(testStockId)) {
                System.exit(0);
            } else {
                System.exit(1);
            }
//            StockSqlUtil stua = new StockSqlUtil();
//            if(DEBUG_GET_PAGE) {
//                int idx;
//                if(DEBUG_GET_PAGE_BASIC) {
//                    for (idx = 0; idx < 2; idx++) {
//                        String savedFile = pf.getStockAndSave(testStockId);
//                        if (savedFile == null) {
//                            System.out.println("Get basic info page failed, try again");
//                            continue;
//                        }
//                        System.out.println("processing basic info page:" + savedFile);
//                        hWebPage.getWebPageFromFile(savedFile);
//                        if (hWebPage.mPageListener != null &&
//                                hWebPage.mPageListener.infoListener != null &&
//                                hWebPage.mPageListener.infoListener.stockInfo != null) {
//                            System.out.println("processing basic info page success");
//                            break;
//                        }
//                        System.out.println("processing basic info failed, try again");
//                    }
//                    if (idx == 2) {
//                        System.out.println("processing basic info failed 2 times, leave");
//                        System.exit(1);
//                    }
//                }
//                if(DEBUG_GET_PAGE_DAILY) {
//                    for (idx = 0; idx < 2; idx++) {
//                        String savedDailyFile = pf.getDailyPriseAndSave(testStockId);
//                        if (savedDailyFile == null) {
//                            System.out.println("Get daily info page failed, try again");
//                            continue;
//                        }
//                        if(DEBUG_PARSE_PAGE_DAILY) {
//                            System.out.println("processing daily info page:" + savedDailyFile);
//                            hWebPage.getDailyPrisePageFromFile(savedDailyFile);
//                            if (hWebPage.mDailyPrisePageListener != null) {
//                                System.out.println("processing daily info page success");
//                                break;
//                            }
//                            System.out.println("processing daily info page failed, try again");
//                        }
//                    }
//                    if (idx == 2) {
//                        System.out.println("processing daily info page failed 2 times, leave");
//                        System.exit(1);
//                    }
//                }
//                if(DEBUG_GET_PAGE_DIVIDEND) {
//                    for (idx = 0; idx < 2; idx++) {
//                        String savedDividendFile = pf.getAnnualDividendAndSave(testStockId);
//                        if (savedDividendFile == null) {
//                            System.out.println("Get dividend page failed, try again");
//                            continue;
//                        }
//                        System.out.println("processing dividend page:" + savedDividendFile);
//                        hWebPage.getAnnualDividendPageFromFile(savedDividendFile);
//                        if (hWebPage.mDividendListener != null && hWebPage.mDividendListener.divRecList != null) {
//                            System.out.println("processing dividend page success");
//                            break;
//                        }
//                        System.out.println("processing dividend page failed, try again");
//                    }
//                    if (idx == 2) {
//                        System.out.println("processing dividend page failed 2 times, leave");
//                        System.exit(1);
//                    }
//                }
//                if(DEBUG_GET_PAGE_EARNING) {
//                    for (idx = 0; idx < 2; idx++) {
//                        String saveEarningFile = pf.getEarningPageAndSave(testStockId);
//                        if (saveEarningFile == null) {
//                            System.out.println("Get earning info page failed, try again");
//                            continue;
//                        }
//                        System.out.println("processing earning info page:" + saveEarningFile);
//                        hWebPage.getEarningPageFromFile(saveEarningFile);
//                        if (hWebPage.mEarningListener != null &&
//                                hWebPage.mEarningListener.searnAfterTaxListener != null &&
//                                hWebPage.mEarningListener.searnBeforeTaxListener != null &&
//                                hWebPage.mEarningListener.monthListener != null) {
//                            System.out.println("processing earning info page success");
//                            break;
//                        }
//                        System.out.println("processing earning info page failed, try again");
//                    }
//                    if (idx == 2) {
//                        System.out.println("processing earning info page failed 2 times, leave");
//                        System.exit(1);
//                    }
//                }
//            }
//            if(DEBUG_DATABASE_STOCK_INSERT_DAILY) {
//                System.out.println("Adding daily db");
//                StockSqlUtil stu = new StockSqlUtil();
//                HTMLParserDailyPriseListener listener = hWebPage.mDailyPrisePageListener;
//                stu.insertDailyTable(testStockId, listener.mLatestRecordDate, listener.buyin, listener.sellout, listener.dealprise, listener.dailyShift, listener.amount);
//            }
//            if(DEBUG_DATABASE_STOCK_INSERT_BASIC) {
//                System.out.println("Adding basic info db");
//                StockSqlUtil stu = new StockSqlUtil();
//                StackBasicInformation staInfo = hWebPage.mPageListener.infoListener.stockInfo;
//                stu.insertAnnualTable(staInfo.StockNumber, staInfo.year, staInfo.ReturnOnEquity, staInfo.ReturnOnAssets,
//                        staInfo.StockDividend, staInfo.CashDividend, staInfo.ExclusionDate, staInfo.EliminationDate,
//                        staInfo.GrossProfitMargin, staInfo.OperatingProfitMargin, staInfo.BookValuePerShare, staInfo.EarningBeforeTaxMargin);
//                stu.insertSeasonShareTable(testStockId, staInfo.seasonRecordList);
//                stu.insertAnnualShareTable(testStockId, staInfo.yearRecordList);
//            }
//            if(DEBUG_DATABASE_STOCK_INSERT_DIVIDEND) {
//                System.out.println("Adding dividend info db");
//                StockSqlUtil stu = new StockSqlUtil();
//                stu.insertAnnualDividendTable(testStockId, hWebPage.mDividendListener.divRecList);
//            }
//            if(DEBUG_DATABASE_STOCK_INSERT_EARNING) {
//                System.out.println("Adding earning info db");
//                HTMLParserEarningListener htmlListener = hWebPage.mEarningListener;
//                stua.insertSeasonEarningTable(testStockId,
//                        htmlListener.searnAfterTaxListener.year1,
//                        htmlListener.searnAfterTaxListener.alist1,
//                        htmlListener.searnBeforeTaxListener.alist1);
//                stua.insertSeasonEarningTable(testStockId,
//                        htmlListener.searnAfterTaxListener.year2,
//                        htmlListener.searnAfterTaxListener.alist2,
//                        htmlListener.searnBeforeTaxListener.alist2);
//                stua.insertMonthlyEarningTable(testStockId, htmlListener.monthListener.year1, htmlListener.monthListener.alist1);
//                stua.insertMonthlyEarningTable(testStockId, htmlListener.monthListener.year2, htmlListener.monthListener.alist2);
//
//            }
//            if(DEBUG_DATABASE_STOCK_ANALYSIS_RATIO) stua.analysisReturnRatio(testStockId);
//            if(DEBUG_DATABASE_STOCK_APPLY_STRATEGY) stua.applyStrategy(testStockId, 0);
//            if(DEBUG_DATABASE_STOCK_COUNT_FORMANCE) stua.getPerformance(testStockId);
        } else {

            if(DEBUG_USE_TWSE_TPEX_FOR_DAILY) {
                Date today;
                if(getPageDay != -1 && getPageMonth != -1 && getPageYear != -1) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(getPageYear, getPageMonth - 1,getPageDay);
                    today = cal.getTime();
                } else {
                    today = new Date();
                }
                CSVStackParser csp = new CSVStackParser();
                String savefile = pf.getTPEXPageAndSave(today);
                System.out.println("Use TPEX file" + savefile);
                csp.parseCSVFile(savefile);
                csp.insertOTCStockDailyInfo(csp.mCsvListener.infoList, today);
                savefile = pf.getTWSEPageAndSave(today);
                System.out.println("Use TWSE file" + savefile);
                csp.resetInfoList();
                csp.parseCSVFile(savefile);
                csp.insertStockDailyInfo(csp.mCsvListener.infoList, today);
            }
            if(!DEBUG_DATABASE_STOCK)
                System.exit(0);
            StockListUtil su = new StockListUtil();
            switch(stockIdListUse) {
                case USE_STOCK_LIST_ID_LIST:
                    su.getStockList();
                    break;
                case USE_STOCK_LIST_ID_LIST_UPDATE:
                    su.getStockListFromUpdatedList();
                    break;
                case USE_STOCK_LIST_BUYIN_ANALYSIS_LIST:
                    su.getStockListFromBuyinAnalysis();
                    break;
                case USE_STOCK_LIST_BUYIN_LIST:
                    su.getStockListFromBuyin(true);
                    break;
                default:
                    su.getStockListFromUpdatedList();
                    break;

            }
//            if(DEBUG_SWITCH_TO_STOCKID_UPDATE) {
//                // We update our list from "market" and "over the counter", and we limit with only 4 digit number stockid here
//                su.getStockListFromUpdatedList();
//            } else if(DEBUG_UPDATE_BUYIN_PERFORMANCE) {
//                su.getStockListFromBuyinAnalysis();
//            } else {
//                su.getStockList();
//            }
            float total = 0, perform = 0;
            boolean getPageSuccess = true;
            StockListUtil.StockIdEntry se = null;
            if(endIndex == -1) {
                endIndex = su.stockIdList.size();
            }
            for (int i = startIndex; i < endIndex; i++) {
                se = su.stockIdList.get(i);
                System.out.println("Process item i:" + i + " id:" + se.id);
                if(!processOneStock(se.id)) continue;
//                if(DEBUG_GET_PAGE) {
//                    int idx;
//                    String savedFile = null, savedDailyFile = null, savedDividendFile = null, saveEarningFile = null;
//                    getPageSuccess = true;
//                    if(DEBUG_GET_PAGE_BASIC) {
//                        for (idx = 0; idx < 2; idx++) {
//                            savedFile = pf.getStockAndSave(se.id);
//                            if (savedFile == null) {
//                                System.out.println("Get basic info page failed, try again");
//                                continue;
//                            }
//                            if(DEBUG_PARSE_PAGE_BASIC) {
//                                System.out.println("processing basic info page:" + savedFile);
//                                hWebPage.getWebPageFromFile(savedFile);
//                                if (hWebPage.mPageListener != null &&
//                                        hWebPage.mPageListener.infoListener != null &&
//                                        hWebPage.mPageListener.infoListener.stockInfo != null) {
//                                    System.out.println("processing basic info page success");
//                                    break;
//                                }
//                                System.out.println("processing basic info failed, try again");
//                            }
//                        }
//                        if (idx == 2) {
//                            System.out.println("processing basic info failed 2 times, leave");
//                            getPageSuccess = false;
//                        }
//                    }
//                    if(DEBUG_GET_PAGE_DAILY) {
//                        if (getPageSuccess) {
//                            for (idx = 0; idx < 2; idx++) {
//                                savedDailyFile = pf.getDailyPriseAndSave(se.id);
//                                if (savedDailyFile == null) {
//                                    System.out.println("Get daily info page failed, try again");
//                                    continue;
//                                }
////                                Runtime runtime = Runtime.getRuntime();
////                                runtime.gc();
////                                long memory = runtime.totalMemory() - runtime.freeMemory();
////                                System.out.println("Used memory is bytes: " + memory);
////                                System.out.println("Used memory is megabytes: "
////                                        + bytesToMegabytes(memory));
//                                if(DEBUG_PARSE_PAGE_DAILY) {
//                                    System.out.println("processing daily info page:" + savedDailyFile);
//                                    hWebPage.getDailyPrisePageFromFile(savedDailyFile);
//                                    if (hWebPage.mDailyPrisePageListener != null) {
//                                        System.out.println("processing daily info page success");
//                                        break;
//                                    }
//                                    System.out.println("processing daily info page failed, try again");
//                                }
//                            }
//                            if (idx == 2) {
//                                System.out.println("processing daily info page failed 2 times, leave");
//                                getPageSuccess = false;
//                            }
//                        }
//                    }
//                    if(DEBUG_GET_PAGE_DIVIDEND) {
//                        if (getPageSuccess) {
//                            for (idx = 0; idx < 2; idx++) {
//                                savedDividendFile = pf.getAnnualDividendAndSave(se.id);
//                                if (savedDividendFile == null) {
//                                    System.out.println("Get dividend page failed, try again");
//                                    continue;
//                                }
//                                if(DEBUG_PARSE_PAGE_DIVIDEND) {
//                                    System.out.println("processing dividend page:" + savedDividendFile);
//                                    hWebPage.getAnnualDividendPageFromFile(savedDividendFile);
//                                    if (hWebPage.mDividendListener != null && hWebPage.mDividendListener.divRecList != null) {
//                                        System.out.println("processing dividend page success");
//                                        break;
//                                    }
//                                    System.out.println("processing dividend page failed, try again");
//                                }
//                            }
//                            if (idx == 2) {
//                                System.out.println("processing dividend page failed 2 times, leave");
//                                getPageSuccess = false;
//                            }
//                        }
//                    }
//                    if(DEBUG_GET_PAGE_EARNING) {
//                        if (getPageSuccess) {
//                            for (idx = 0; idx < 2; idx++) {
//                                saveEarningFile = pf.getEarningPageAndSave(se.id);
//                                if (saveEarningFile == null) {
//                                    System.out.println("Get earning info page failed, try again");
//                                    continue;
//                                }
//                                if(DEBUG_PARSE_PAGE_EARNING) {
//                                    System.out.println("processing earning info page:" + saveEarningFile);
//                                    hWebPage.getEarningPageFromFile(saveEarningFile);
//                                    if (hWebPage.mEarningListener != null &&
//                                            hWebPage.mEarningListener.searnAfterTaxListener != null &&
//                                            hWebPage.mEarningListener.searnBeforeTaxListener != null &&
//                                            hWebPage.mEarningListener.monthListener != null) {
//                                        System.out.println("processing earning info page success");
//                                        break;
//                                    }
//                                    System.out.println("processing earning info page failed, try again");
//                                }
//                            }
//                            if (idx == 2) {
//                                System.out.println("processing earning info page failed 2 times, leave");
//                                getPageSuccess = false;
//                            }
//                        }
//                    }
////                    String savedFile = pf.getStockAndSave(se.id);
////                    String savedDailyFile = pf.getDailyPriseAndSave(se.id);
////                    String savedDividendFile = pf.getAnnualDividendAndSave(se.id);
////                    System.out.println("item i:" + i + " basic info page:" + savedFile +
////                            " daily info page:" + savedDailyFile +
////                            " dividend:" + savedDividendFile);
////                    String saveEarningFile = pf.getEarningPageAndSave(se.id);
////                    System.out.println("processing earning info page:" + saveEarningFile);
//                    try {
//                        Thread.sleep(1000);
//                        System.out.println("sleep");
//                    } catch(Exception e) {
//                        e.printStackTrace();
//                    }
//                    if(!getPageSuccess) {
//                        System.out.println("index i:" + i + " id:" + se.id + " get page failed, next");
//                        continue;
//                    }
//                    if (savedFile != null && savedDailyFile != null && savedDividendFile != null) {
////                        hWebPage.getWebPageFromFile(savedFile);
////                        hWebPage.getDailyPrisePageFromFile(savedDailyFile);
////                        hWebPage.getAnnualDividendPageFromFile(savedDividendFile);
////                        hWebPage.getEarningPageFromFile(saveEarningFile);
//                        if(DEBUG_DATABASE_STOCK_INSERT_DAILY) {
//                            StockSqlUtil stu = new StockSqlUtil();
//                            HTMLParserDailyPriseListener listener = hWebPage.mDailyPrisePageListener;
//                            stu.insertDailyTable(se.id, listener.mLatestRecordDate, listener.buyin, listener.sellout, listener.dealprise, listener.dailyShift, listener.amount);
//                        }
//                        if(DEBUG_DATABASE_STOCK_INSERT_BASIC) {
//                            StockSqlUtil stu = new StockSqlUtil();
//                            StackBasicInformation staInfo = hWebPage.mPageListener.infoListener.stockInfo;
//                            stu.insertAnnualTable(staInfo.StockNumber, staInfo.year, staInfo.ReturnOnEquity, staInfo.ReturnOnAssets,
//                                    staInfo.StockDividend, staInfo.CashDividend, staInfo.ExclusionDate, staInfo.EliminationDate,
//                                    staInfo.GrossProfitMargin, staInfo.OperatingProfitMargin, staInfo.BookValuePerShare, staInfo.EarningBeforeTaxMargin);
//                            stu.insertSeasonShareTable(se.id, staInfo.seasonRecordList);
//                            stu.insertAnnualShareTable(se.id, staInfo.yearRecordList);
//                        }
//                        if(DEBUG_DATABASE_STOCK_INSERT_DIVIDEND) {
//                            StockSqlUtil stu = new StockSqlUtil();
//                            stu.insertAnnualDividendTable(se.id, hWebPage.mDividendListener.divRecList);
//                        }
//                        if(DEBUG_DATABASE_STOCK_INSERT_EARNING) {
//                            StockSqlUtil stu = new StockSqlUtil();
//                            System.out.println("Adding earning info db with stock:" + se.id);
//                            HTMLParserEarningListener htmlListener = hWebPage.mEarningListener;
//                            stu.insertSeasonEarningTable(se.id,
//                                    htmlListener.searnAfterTaxListener.year1,
//                                    htmlListener.searnAfterTaxListener.alist1,
//                                    htmlListener.searnBeforeTaxListener.alist1);
//                            stu.insertSeasonEarningTable(se.id,
//                                    htmlListener.searnAfterTaxListener.year2,
//                                    htmlListener.searnAfterTaxListener.alist2,
//                                    htmlListener.searnBeforeTaxListener.alist2);
//                            stu.insertMonthlyEarningTable(se.id, htmlListener.monthListener.year1, htmlListener.monthListener.alist1);
//                            stu.insertMonthlyEarningTable(se.id, htmlListener.monthListener.year2, htmlListener.monthListener.alist2);
//
//                        }
//
//                    } else {
//                        System.out.println("Stock id:" + se.id + " basic info not exist");
////                    System.out.println("Stock id:" + "1103" + " basic info not exist");
//                    }
//                }
//                StockSqlUtil stu = new StockSqlUtil();
//                if(DEBUG_DATABASE_STOCK_ANALYSIS_RATIO) stu.analysisReturnRatio(se.id);
//                if(DEBUG_DATABASE_STOCK_APPLY_STRATEGY) stu.applyStrategy(se.id, 0);
//                if(DEBUG_DATABASE_STOCK_COUNT_FORMANCE) {
//                    perform = stu.getPerformance(se.id);
//                    total += perform;
//                    System.out.println("stockid:" + se.id + " performance:" + perform + " total:" + total);
//                }
            }
        }
        if(DEBUG_CURRENCY_UPDATE) {
            CurrencyUtil cu = new CurrencyUtil();
            cu.updateCurrency(new Date());
        }
    }
}
