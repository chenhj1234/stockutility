package com.company.commutil;

import com.company.GetArgs;
import com.company.StockListUtil;
import sqlutil.StockSqlUtil;

import java.sql.ResultSet;

public class SelloutStock {
    public static void main(String[] args) {
        GetArgs argParser = new GetArgs();
        argParser.addOption("-s", true);
        argParser.addOption("-buyinlist", false);
        argParser.addOption("-buysell", false);
        argParser.addOption("-checkscore", false);
        argParser.processArgs(args);
        String stockid = argParser.findParm("-s");
        boolean optBuyin = argParser.isArgOn("-buyinlist");
        boolean optBuySell = argParser.isArgOn("-buysell");
        boolean optCheckScore = argParser.isArgOn("-checkscore");
        StockSqlUtil sSql = new StockSqlUtil();
        if(stockid != null) {
            sSql.applyBuyinSelloutStrategy(stockid, optBuySell);
//            StockSqlUtil.performanceEntry pe = sSql.getPerformanceNoBuyin(stockid, false);
//            sSql.initSelectTable();
//            sSql.addSelParmValue("stockid", stockid);
//            ResultSet rSet = sSql.performSelectTable(sSql.buyinTable);
//            try {
//                while (rSet.next()) {
//                    int sind = rSet.getInt("strategyindex");
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } else if(optCheckScore) {
            StockListUtil su = new StockListUtil();
            if(optBuyin) {
                System.out.println("Using table from buyin");
                su.getStockListFromBuyin(false);
            }
            else {
                System.out.println("Using table from buyin analysis");
                su.getStockListFromBuyinAnalysis();
            }
            StockListUtil.StockIdEntry se = null;
            for(int i = 0;i < su.stockIdList.size(); i++) {
                se = su.stockIdList.get(i);
                System.out.println("Process id:" + se.id + " name:" + se.name);
                StockSqlUtil.performanceEntry pe = sSql.getPerformance(se.id, false, true);
                sSql.updateBuyinAnalysis("buyin_table_analysis_score", se.id, pe, true);
            }
        } else {
            StockListUtil su = new StockListUtil();
            if(optBuyin) {
                System.out.println("Using table from buyin");
                su.getStockListFromBuyin(false);
            }
            else {
                System.out.println("Using table from buyin analysis");
                su.getStockListFromBuyinAnalysis();
            }
            StockListUtil.StockIdEntry se = null;
            for(int i = 0;i < su.stockIdList.size(); i++) {
                se = su.stockIdList.get(i);
                System.out.println("Process id:" + se.id + " name:" + se.name);
                sSql.applyBuyinSelloutStrategy(se.id, optBuySell);
            }
        }

    }
}
