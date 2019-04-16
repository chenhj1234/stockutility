package com.company.commutil;

import com.company.GetArgs;
import com.company.StockListUtil;
import sqlutil.StockSqlUtil;

import java.sql.ResultSet;

public class SelloutStock {
    public static void main(String[] args) {
        GetArgs argParser = new GetArgs();
        argParser.addOption("-s", true);
        argParser.processArgs(args);
        String stockid = argParser.findParm("-s");
        StockSqlUtil sSql = new StockSqlUtil();
        if(stockid != null) {
            StockSqlUtil.performanceEntry pe = sSql.getPerformanceNoBuyin(stockid, false);
            sSql.initSelectTable();
            sSql.addSelParmValue("stockid", stockid);
            ResultSet rSet = sSql.performSelectTable(sSql.buyinTable);
            try {
                while (rSet.next()) {
                    int sind = rSet.getInt("strategyindex");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            StockListUtil su = new StockListUtil();
            su.getStockListFromBuyinAnalysis();
        }

    }
}
