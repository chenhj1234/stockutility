package com.company.commutil;

import com.company.PageAndFile;
import sqlutil.StockSqlUtil;

import java.util.Calendar;
import java.util.Date;

public class GetHistory {
    private static int startYear = -1, endYear = -1, startMonth = -1, endMonth = -1, startDay = -1, endDay = -1;
    private static boolean DEBUG_DOWNLOAD_TWSE = true;
    private static boolean DEBUG_DOWNLOAD_TPEX = false;
    private static void processArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-year":
                    i++;
                    startYear = Integer.parseInt(args[i]);
                    System.out.println("Applying year:" + args[i]);
                    break;
                case "-month":
                    i++;
                    startMonth = Integer.parseInt(args[i]);
                    System.out.println("Applying year:" + args[i]);
                    break;
                case "-day":
                    i++;
                    startDay = Integer.parseInt(args[i]);
                    System.out.println("Applying year:" + args[i]);
                    break;
                case "-end-year":
                    i++;
                    endYear = Integer.parseInt(args[i]);
                    System.out.println("Applying year:" + args[i]);
                    break;
                case "-end-month":
                    i++;
                    endMonth = Integer.parseInt(args[i]);
                    System.out.println("Applying year:" + args[i]);
                    break;
                case "-end-day":
                    i++;
                    endDay = Integer.parseInt(args[i]);
                    System.out.println("Applying year:" + args[i]);
                    break;
                case "-twse":
                    i++;
                    if ("no".equalsIgnoreCase(args[i])) {
                        DEBUG_DOWNLOAD_TWSE = false;
                    } else if ("yes".equalsIgnoreCase(args[i])) {
                        DEBUG_DOWNLOAD_TWSE = true;
                    }
                    break;
                case "-tpex":
                    i++;
                    if ("no".equalsIgnoreCase(args[i])) {
                        DEBUG_DOWNLOAD_TPEX = false;
                    } else if ("yes".equalsIgnoreCase(args[i])) {
                        DEBUG_DOWNLOAD_TPEX = true;
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        processArgs(args);
        PageAndFile pf = new PageAndFile();
        if(startDay == -1 || startMonth == -1 || startYear == -1) {
            System.err.println("Usage:cmd -year YYYY -month mm -day dd [-end-year yyyy] [-end-month mm] [-end-day dd]");
            System.exit(1);
        }
        if(endYear == -1 || endMonth == -1 || endDay == -1) {
            Calendar cal = Calendar.getInstance();
            endYear = cal.get(Calendar.YEAR);
            endMonth = cal.get(Calendar.MONTH);
            endYear = cal.get(Calendar.DAY_OF_MONTH);
        }
        if(DEBUG_DOWNLOAD_TWSE) {
            Calendar cal = Calendar.getInstance();
            cal.set(startYear, startMonth - 1, startDay);
            Date date = cal.getTime();
            pf.getTWSEPageAndSave(date);
        }
        if(DEBUG_DOWNLOAD_TPEX) {
            Calendar cal = Calendar.getInstance();
            cal.set(startYear, startMonth - 1, startDay);
            Date date = cal.getTime();
            pf.getTPEXPageAndSave(date);
        }

    }
}
