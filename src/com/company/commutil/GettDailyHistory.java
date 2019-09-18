package com.company.commutil;

import com.company.CSVStackParser;
import com.company.GetArgs;
import com.company.PageAndFile;
import com.company.StockListUtil;
import sqlutil.StockSqlUtil;

import java.util.Calendar;
import java.util.Date;

public class GettDailyHistory {
    private static boolean checkDateEqual(String ds, Date dd) {
        StockSqlUtil sqlU = new StockSqlUtil();
        int dayOfWeek = sqlU.getWeekdatFromDate(dd);
        System.out.println("Week day " + dayOfWeek);
        int dayOfMonth = sqlU.getMonthdateFromDate(dd);
        String domStr;
        if(dayOfMonth < 10) {
            domStr = "0" + dayOfMonth;
        } else {
            domStr = "" + dayOfMonth;
        }
        System.out.println("Month day " + dayOfMonth);

        int month = sqlU.getMonthFromDate(dd) + 1;
        System.out.println("Month " + month);
        String mStr;
        if(month < 10) {
            mStr = "0" + month;
        } else {
            mStr = "" + month;
        }
        int year = sqlU.getYearFromDate(dd);
        System.out.println("Year " + year);
        String rs = year + mStr + domStr;
        System.out.println("Reconstruct string:" + rs);
        return rs.equals(ds);
    }
    private static boolean checkDateEqual(Date dd, int tYear, int tMonth, int tDay) {
        StockSqlUtil sqlU = new StockSqlUtil();
        int dayOfMonth = sqlU.getMonthdateFromDate(dd);
        System.out.println("Month day " + dayOfMonth);

        int month = sqlU.getMonthFromDate(dd) + 1;
        System.out.println("Month " + month);

        int year = sqlU.getYearFromDate(dd);
        System.out.println("Year " + year);
        return (dayOfMonth == tDay) && (month == tMonth) && (year == tYear);
    }
    static StockListUtil su = new StockListUtil();
    private static void buildDateRecord(Date date) {
        CSVStackParser csp = new CSVStackParser();
        StockSqlUtil sqlU = new StockSqlUtil();
        String saveFile;
        PageAndFile pf = new PageAndFile();
        saveFile = pf.getTPEXPageAndSave(date);
        System.out.println("Use TPEX file" + saveFile);
        csp.parseCSVFile(saveFile);
        csp.insertOTCStockDailyInfo(csp.mCsvListener.infoList, date, sqlU.dailyInfoHistoryTable);
        saveFile = pf.getTWSEPageAndSave(date);
        System.out.println("Use TWSE file" + saveFile);
        csp.resetInfoList();
        csp.parseCSVFile(saveFile);
        csp.insertStockDailyInfo(csp.mCsvListener.infoList, date, sqlU.dailyInfoHistoryTable);

//        StockListUtil.StockIdEntry se;
//        for (int i = 0; i < su.stockIdList.size(); i++) {
//            se = su.stockIdList.get(i);
//            System.out.println("processing item i:" + i + " id:" + se.id);
//            sqlU.analysisReturnRatio(se.id, date, sqlU.dailyInfoHistoryTable);
//        }

    }
    public static void main(String[] args) {
        GetArgs argParser = new GetArgs();
        argParser.addOption("-date", true);
        argParser.addOption("--startdate", true);
        argParser.addOption("--enddate", true);
        argParser.addOption("--daysbefore", true);
        argParser.processArgs(args);
        StockSqlUtil sqlU = new StockSqlUtil();
        CSVStackParser csp = new CSVStackParser();

        su.getStockListFromUpdatedList();
        if(argParser.isArgOn("-date")) {
            String dateStr = argParser.findParm("-date");

            Date date = sqlU.convertStrToJavaDate(dateStr);
            if(!checkDateEqual(dateStr, date)) {
                System.err.println("Date string:" + dateStr + " value invalid, convert for calender date failed.");
                return;
            }
            buildDateRecord(date);
//            String saveFile;
//            PageAndFile pf = new PageAndFile();
//            saveFile = pf.getTPEXPageAndSave(date);
//            System.out.println("Use TPEX file" + saveFile);
//            csp.parseCSVFile(saveFile);
//            csp.insertOTCStockDailyInfo(csp.mCsvListener.infoList, date, sqlU.dailyInfoHistoryTable);
//            saveFile = pf.getTWSEPageAndSave(date);
//            System.out.println("Use TWSE file" + saveFile);
//            csp.resetInfoList();
//            csp.parseCSVFile(saveFile);
//            csp.insertStockDailyInfo(csp.mCsvListener.infoList, date, sqlU.dailyInfoHistoryTable);
        } else {
            Date tdate;
            int yearStart = 2017;
            int monthStart = 1;
            int dateStart = 1;
            if(argParser.isArgOn("--startdate")) {
                String dateStr = argParser.findParm("--startdate");
                tdate = sqlU.convertStrToJavaDate(dateStr);
                yearStart = sqlU.getYearFromDate(tdate);
                monthStart = sqlU.getMonthFromDate(tdate) + 1;
                dateStart = sqlU.getMonthdateFromDate(tdate);
            }
            System.out.println("Start date : " + yearStart + " " + monthStart + " " + dateStart);

            tdate = new Date();
            int yearEnd = sqlU.getYearFromDate(tdate);
            int monthEnd = sqlU.getMonthFromDate(tdate) + 1;
            int dateEnd = sqlU.getMonthdateFromDate(tdate);
            if(argParser.isArgOn("--enddate")) {
                String dateStr = argParser.findParm("--enddate");
                tdate = sqlU.convertStrToJavaDate(dateStr);
                yearEnd = sqlU.getYearFromDate(tdate);
                monthEnd = sqlU.getMonthFromDate(tdate) + 1;
                dateEnd = sqlU.getMonthdateFromDate(tdate);
            }
            System.out.println("End date : " + yearEnd + " " + monthEnd + " " + dateEnd);
            if(argParser.isArgOn("--daysbefore")) {
                String dateStr = argParser.findParm("--daysbefore");
                int daysBefore = Integer.parseInt(dateStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(tdate);
                cal.add(Calendar.DATE, -daysBefore);
                tdate = cal.getTime();
                yearStart = sqlU.getYearFromDate(tdate);
                monthStart = sqlU.getMonthFromDate(tdate) + 1;
                dateStart = sqlU.getMonthdateFromDate(tdate);
                System.out.println("Use days before of " + daysBefore + " Start date : " + yearStart + " " + monthStart + " " + dateStart);
            }
            Calendar c = Calendar.getInstance();
            Date tDate;
            for(; yearStart <= yearEnd;yearStart++) {
                for (; (yearStart <yearEnd && monthStart < 13) || (yearStart == yearEnd && monthStart <= monthEnd); monthStart++) {
                    for( ; ((yearStart < yearEnd || monthStart < monthEnd) && dateStart < 32) || ((yearStart == yearEnd && monthStart == monthEnd) && dateStart <= dateEnd) ; dateStart ++) {
                        System.out.println("Process " + yearStart + " " + monthStart + " " + dateStart);
                        c.set(yearStart, monthStart - 1, dateStart);
                        tDate = c.getTime();
                        if(checkDateEqual(tDate,yearStart, monthStart, dateStart)) {
                            buildDateRecord(tDate);
                        }
                    }
                    dateStart = 1;
                }
                monthStart = 1;
            }
        }
    }
}
