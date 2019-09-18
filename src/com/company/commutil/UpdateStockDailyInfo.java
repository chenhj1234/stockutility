package com.company.commutil;

import com.company.CSVStackParser;
import com.company.PageAndFile;

//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateStockDailyInfo {
    private static boolean USE_STOCK_SOURCE_TWSE = false;
    private static boolean USE_STOCK_SOURCE_TPEX = false;
//    private static String csvfile = null;
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
//                case "--twse-file":
//                    i++;
//                    csvfile = args[i];
//                    USE_STOCK_SOURCE_TWSE = true;
//                    break;
//                case "--tpex-file":
//                    i++;
//                    csvfile = args[i];
//                    USE_STOCK_SOURCE_TPEX = true;
//                    break;
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
        if(USE_STOCK_SOURCE_TWSE) {
            CSVStackParser csp = new CSVStackParser();
            String savefile;
            Date today;
            if(getPageDay != -1 && getPageMonth != -1 && getPageYear != -1) {
                Calendar cal = Calendar.getInstance();
                cal.set(getPageYear, getPageMonth - 1,getPageDay);
                today = cal.getTime();
            } else {
                today = new Date();
            }
            PageAndFile pf = new PageAndFile();
            savefile = pf.getTWSEPageAndSave(today);
            System.out.println("Use TWSE file" + savefile);
            csp.resetInfoList();
            csp.parseCSVFile(savefile);
            csp.insertStockDailyInfo(csp.mCsvListener.infoList, today);
        }
        if(USE_STOCK_SOURCE_TPEX) {
            CSVStackParser csp = new CSVStackParser();
            String savefile;
            Date today;
            if(getPageDay != -1 && getPageMonth != -1 && getPageYear != -1) {
                Calendar cal = Calendar.getInstance();
                cal.set(getPageYear, getPageMonth - 1,getPageDay);
                today = cal.getTime();
            } else {
                today = new Date();
            }
            PageAndFile pf = new PageAndFile();
            savefile = pf.getTPEXPageAndSave(today);
            System.out.println("Use TPEX file" + savefile);
            csp.parseCSVFile(savefile);
            csp.insertOTCStockDailyInfo(csp.mCsvListener.infoList, today);
        }
    }
}
