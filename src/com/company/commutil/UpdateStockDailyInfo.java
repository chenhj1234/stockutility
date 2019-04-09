package com.company.commutil;

import com.company.CSVStackParser;
import com.company.PageAndFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateStockDailyInfo {
    private static boolean USE_STOCK_SOURCE_TWSE = false;
    private static boolean USE_STOCK_SOURCE_TPEX = false;
    private static String csvfile = null;
    private static int getPageYear = -1;
    private static int getPageMonth = -1;
    private static int getPageDay = -1;
    private static void processArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--twse":
                    USE_STOCK_SOURCE_TWSE = true;
                    break;
                case "--tpex":
                    USE_STOCK_SOURCE_TPEX = true;
                    break;
                case "--twse-file":
                    i++;
                    csvfile = args[i];
                    USE_STOCK_SOURCE_TWSE = true;
                    break;
                case "--tpex-file":
                    i++;
                    csvfile = args[i];
                    USE_STOCK_SOURCE_TPEX = true;
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

            }
        }
    }

    public static void main(String[] args) {
        processArgs(args);
        CSVStackParser csp = new CSVStackParser();
        if(USE_STOCK_SOURCE_TWSE) {
            String savefile;
            Date today;
            if(getPageDay != -1 && getPageMonth != -1 && getPageYear != -1) {
                Calendar cal = Calendar.getInstance();
                cal.set(getPageYear, getPageMonth - 1,getPageDay);
                today = cal.getTime();
            } else {
                today = new Date();
            }
            if(csvfile == null) {
                String twseIndexPage = "http://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=__DATE__&type=ALL";
                // We will to get stock list info page from here http://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=20190322&type=ALL
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                String todaystr = dateFormat.format(today);
                savefile = "MI_INDEX_" + todaystr + ".csv";
                twseIndexPage = twseIndexPage.replace("__DATE__", todaystr);
                PageAndFile pf = new PageAndFile();
                savefile = pf.getPageAndSave(twseIndexPage,savefile);
                System.out.println("MI Index save to " + savefile);
            }
            else {
                savefile = csvfile;
            }
            csp.parseCSVFile(savefile);
            csp.insertStockDailyInfo(csp.mCsvListener.infoList, today);
        }
        if(USE_STOCK_SOURCE_TPEX) {
            String savefile;
            Date today;
            if(getPageDay != -1 && getPageMonth != -1 && getPageYear != -1) {
                Calendar cal = Calendar.getInstance();
                cal.set(getPageYear, getPageMonth - 1,getPageDay);
                today = cal.getTime();
            } else {
                today = new Date();
            }
            if(csvfile == null) {
                String twseIndexPage = "http://www.tpex.org.tw/web/stock/aftertrading/daily_close_quotes/stk_quote_result.php?l=zh-tw&o=csv&d=__DATE__&s=0,asc,0";
                // We will to get stock list info page from here http://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=20190322&type=ALL
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                String todaystr = dateFormat.format(today);
                String tyear = String.valueOf(Integer.parseInt(todaystr.substring(0, 4)) - 1911);
                String tmm = todaystr.substring(4, 6);
                String tdd = todaystr.substring(6, 8);
                savefile = "RSTA_" + todaystr + ".csv";
                twseIndexPage = twseIndexPage.replace("__DATE__", tyear + "/" + tmm + "/" + tdd);
                PageAndFile pf = new PageAndFile();
                savefile = pf.getPageAndSave(twseIndexPage, savefile);
                System.out.println("TPEX index save to " + savefile);
            }
            else {
                savefile = csvfile;
            }
            csp.parseCSVFile(savefile);
            csp.insertOTCStockDailyInfo(csp.mCsvListener.infoList, today);
        }
    }
}
