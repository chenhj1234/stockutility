package com.company.commutil;

import com.company.CSVStackParser;
import com.company.PageAndFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateStockList {
    public static void main(String[] args) {
        CSVStackParser csp = new CSVStackParser();
        if(args.length > 0) {
            csp.parseCSVFile(args[0]);
            csp.insertStockId(csp.mCsvListener.infoList);
        } else {
            String twseIndexPage = "http://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=__DATE__&type=ALL";
            // We will to get stock list info page from here http://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=20190322&type=ALL
            Date today = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String todaystr = dateFormat.format(today);
            String savefile = "MI_INDEX_" + todaystr +".csv";
            twseIndexPage = twseIndexPage.replace("__DATE__", todaystr);
            PageAndFile pf = new PageAndFile();
            savefile = pf.getPageAndSave(twseIndexPage,savefile);
            System.out.println("MI Index save to " + savefile);
            csp.parseCSVFile(savefile);
            csp.insertStockDailyInfo(csp.mCsvListener.infoList, today);
        }
    }
}
