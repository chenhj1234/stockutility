package com.company.commutil;

import com.company.CSVStackParser;
import com.company.GetArgs;
import com.company.PageAndFile;

import java.util.Calendar;
import java.util.Date;

public class UpdateStockList {
    private static final String kOptTWSE = "-twse";
    private static final String kOptTPEX = "-tpex";
    private static final String kOptAlpha = "-alpha";
    public static void main(String[] args) {
        PageAndFile pf = new PageAndFile();
        GetArgs argParser = new GetArgs();
        argParser.addOption(kOptTWSE, false);
        argParser.addOption(kOptTPEX, false);
        argParser.addOption(kOptAlpha, false);
        argParser.processArgs(args);
        //Date today = Calendar.getInstance().getTime();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();
        String savefile;
        if(argParser.isArgOn(kOptTWSE)) {
            CSVStackParser csp = new CSVStackParser();
            System.out.println("Option TWSE enabled, process stockid from twse table");
            savefile = pf.getTWSEPageAndSave(yesterday);
            System.out.println("Use TWSE file" + savefile);
            csp.resetInfoList();
            csp.parseCSVFile(savefile);
            csp.insertStockId(csp.mCsvListener.infoList);

        }
        if(argParser.isArgOn(kOptTPEX)) {
            CSVStackParser csp = new CSVStackParser();
            System.out.println("Option TPEX enabled, process stockid from tpex table");
            savefile = pf.getTPEXPageAndSave(yesterday);
            System.out.println("Use TPEX file" + savefile);
            csp.resetInfoList();
            csp.parseCSVFile(savefile);
            csp.insertStockId(csp.mCsvListener.infoList);
        }
//        } else {
//            String twseIndexPage = "http://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=__DATE__&type=ALL";
//            // We will to get stock list info page from here http://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=20190322&type=ALL
//            Date today = new Date();
//            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//            String todaystr = dateFormat.format(today);
//            String savefile = "MI_INDEX_" + todaystr +".csv";
//            twseIndexPage = twseIndexPage.replace("__DATE__", todaystr);
//            PageAndFile pf = new PageAndFile();
//            savefile = pf.getPageAndSave(twseIndexPage,savefile);
//            System.out.println("MI Index save to " + savefile);
//            csp.parseCSVFile(savefile);
//            csp.insertStockDailyInfo(csp.mCsvListener.infoList, today);
//        }
    }
}
