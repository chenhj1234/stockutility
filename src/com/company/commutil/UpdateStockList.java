package com.company.commutil;

import com.company.CSVStackParser;

public class UpdateStockList {
    public static void main(String[] args) {
        // We will to get stock list info page from here http://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=20190322&type=ALL
        CSVStackParser csp = new CSVStackParser();
        if(args.length > 0) {
            csp.parseCSVFile(args[0]);
            csp.insertStockId(csp.mCsvListener.infoList);
        }
    }
}
