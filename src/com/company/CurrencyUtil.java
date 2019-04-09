package com.company;

import sqlutil.StockSqlUtil;

import java.util.Date;

public class CurrencyUtil {
    public class CurrencyEntry {
        String currencyType;
        float buyinCash;
        float buyinSpot; /* 即期 */
        float buyin10;
        float buyin30;
        float buyin60;
        float buyin90;
        float buyin120;
        float buyin150;
        float buyin180;
        float selloutCash;
        float selloutSpot; /* 即期 */
        float sellout10;
        float sellout30;
        float sellout60;
        float sellout90;
        float sellout120;
        float sellout150;
        float sellout180;
    }
    final String DB_CURRENCY_TYPE = "currency";
    final String DB_CURRENCY_DATE = "date";
    final String DB_CURRENCY_BUYIN = "buyin";
    final String DB_CURRENCY_BUYIN_SPOT = "buyinSpot";
    final String DB_CURRENCY_BUYIN_10 = "buyin10";
    final String DB_CURRENCY_BUYIN_30 = "buyin30";
    final String DB_CURRENCY_BUYIN_60 = "buyin60";
    final String DB_CURRENCY_BUYIN_90 = "buyin90";
    final String DB_CURRENCY_BUYIN_120 = "buyin120";
    final String DB_CURRENCY_BUYIN_150 = "buyin150";
    final String DB_CURRENCY_BUYIN_180 = "buyin180";
    final String DB_CURRENCY_SELLOUT = "sellout";
    final String DB_CURRENCY_SELLOUT_SPOT = "selloutSpot";
    final String DB_CURRENCY_SELLOUT_10 = "sellout10";
    final String DB_CURRENCY_SELLOUT_30 = "sellout30";
    final String DB_CURRENCY_SELLOUT_60 = "sellout60";
    final String DB_CURRENCY_SELLOUT_90 = "sellout90";
    final String DB_CURRENCY_SELLOUT_120 = "sellout20";
    final String DB_CURRENCY_SELLOUT_150 = "sellout150";
    final String DB_CURRENCY_SELLOUT_180 = "sellout180";
    final int DB_CURRENCY_TYPE_INDEX = 0;
    final int DB_CURRENCY_BUYIN_INDEX = 2;
    final int DB_CURRENCY_BUYIN_SPOT_INDEX = 3;
    final int DB_CURRENCY_BUYIN_10_INDEX = 4;
    final int DB_CURRENCY_BUYIN_30_INDEX = 5;
    final int DB_CURRENCY_BUYIN_60_INDEX = 6;
    final int DB_CURRENCY_BUYIN_90_INDEX = 7;
    final int DB_CURRENCY_BUYIN_120_INDEX = 8;
    final int DB_CURRENCY_BUYIN_150_INDEX = 9;
    final int DB_CURRENCY_BUYIN_180_INDEX = 10;
    final int DB_CURRENCY_SELLOUT_INDEX = 12;
    final int DB_CURRENCY_SELLOUT_SPOT_INDEX = 13;
    final int DB_CURRENCY_SELLOUT_10_INDEX = 14;
    final int DB_CURRENCY_SELLOUT_30_INDEX = 15;
    final int DB_CURRENCY_SELLOUT_60_INDEX = 16;
    final int DB_CURRENCY_SELLOUT_90_INDEX = 17;
    final int DB_CURRENCY_SELLOUT_120_INDEX = 18;
    final int DB_CURRENCY_SELLOUT_150_INDEX = 19;
    final int DB_CURRENCY_SELLOUT_180_INDEX = 20;
    public void getCurrency(String currFile) {
        CSVStackParser csp = new CSVStackParser();
        csp.parseCSVFile(currFile);
        insertStockId(csp.mCsvListener.infoList);
    }
    public void insertStockId(StockInfoList sInfo) {
        String seString;
        StockSqlUtil sSqlU = new StockSqlUtil();
        String tableName = sSqlU.dailyExchangeRate;
        String dateStr = sSqlU.convertJavaDateToMySQLStr(new Date());
        for(int i = 0;i < sInfo.stockInfoList.size();i++) {
            StockInfoList.StockEntry se = sInfo.stockInfoList.get(i);
            seString = se.stockEntry.get(DB_CURRENCY_TYPE_INDEX);
            if(sSqlU.checkColumnExist(tableName, DB_CURRENCY_TYPE, seString, DB_CURRENCY_DATE , dateStr)) {
                System.out.println("Currency " + seString + " date " + dateStr + " exists, ignore");
            } else {
                sSqlU.initInsertTable();
                sSqlU.insertValue(DB_CURRENCY_TYPE, seString);
                sSqlU.insertValue(DB_CURRENCY_DATE, dateStr);
                sSqlU.insertValue(DB_CURRENCY_BUYIN, se.stockEntry.get(DB_CURRENCY_BUYIN_INDEX));
                sSqlU.insertValue(DB_CURRENCY_BUYIN_SPOT, se.stockEntry.get(DB_CURRENCY_BUYIN_SPOT_INDEX));
                sSqlU.insertValue(DB_CURRENCY_BUYIN_10, se.stockEntry.get(DB_CURRENCY_BUYIN_10_INDEX));
                sSqlU.insertValue(DB_CURRENCY_BUYIN_30, se.stockEntry.get(DB_CURRENCY_BUYIN_30_INDEX));
                sSqlU.insertValue(DB_CURRENCY_BUYIN_60, se.stockEntry.get(DB_CURRENCY_BUYIN_60_INDEX));
                sSqlU.insertValue(DB_CURRENCY_BUYIN_90, se.stockEntry.get(DB_CURRENCY_BUYIN_90_INDEX));
                sSqlU.insertValue(DB_CURRENCY_BUYIN_120, se.stockEntry.get(DB_CURRENCY_BUYIN_120_INDEX));
                sSqlU.insertValue(DB_CURRENCY_BUYIN_150, se.stockEntry.get(DB_CURRENCY_BUYIN_150_INDEX));
                sSqlU.insertValue(DB_CURRENCY_BUYIN_180, se.stockEntry.get(DB_CURRENCY_BUYIN_180_INDEX));
                sSqlU.insertValue(DB_CURRENCY_SELLOUT, se.stockEntry.get(DB_CURRENCY_SELLOUT_INDEX));
                sSqlU.insertValue(DB_CURRENCY_SELLOUT_SPOT, se.stockEntry.get(DB_CURRENCY_SELLOUT_SPOT_INDEX));
                sSqlU.insertValue(DB_CURRENCY_SELLOUT_10, se.stockEntry.get(DB_CURRENCY_SELLOUT_10_INDEX));
                sSqlU.insertValue(DB_CURRENCY_SELLOUT_30, se.stockEntry.get(DB_CURRENCY_SELLOUT_30_INDEX));
                sSqlU.insertValue(DB_CURRENCY_SELLOUT_60, se.stockEntry.get(DB_CURRENCY_SELLOUT_60_INDEX));
                sSqlU.insertValue(DB_CURRENCY_SELLOUT_90, se.stockEntry.get(DB_CURRENCY_SELLOUT_90_INDEX));
                sSqlU.insertValue(DB_CURRENCY_SELLOUT_120, se.stockEntry.get(DB_CURRENCY_SELLOUT_120_INDEX));
                sSqlU.insertValue(DB_CURRENCY_SELLOUT_150, se.stockEntry.get(DB_CURRENCY_SELLOUT_150_INDEX));
                sSqlU.insertValue(DB_CURRENCY_SELLOUT_180, se.stockEntry.get(DB_CURRENCY_SELLOUT_180_INDEX));
                sSqlU.insertIntoTable(tableName);
            }
        }
    }
    public void updateCurrency(Date d) {
        StockSqlUtil sqlu = new StockSqlUtil();
        CurrencyUtil currUtil = new CurrencyUtil();
        String storePath = "currency_" + sqlu.convertJavaDateToMySQLStr(d);
        PageAndFile webpage = new PageAndFile();
        String currFile = webpage.getPageAndSave(PageAndFile.currencyPage, storePath,"utf8");
        currUtil.getCurrency(currFile);
    }
}
