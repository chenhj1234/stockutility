package com.company.commutil;

import com.company.GetArgs;
import com.company.StockListUtil;
import sqlutil.StockSqlUtil;

public class UpdateReturnRatioAndScore {
    private static final String kOptSingleStock = "-s";
    public static void main(String[] args) {
        GetArgs argParser = new GetArgs();
        argParser.addOption(kOptSingleStock, true);
        argParser.processArgs(args);
        String stockid;
        if(argParser.isArgOn(kOptSingleStock)) {
            StockSqlUtil stu = StockSqlUtil.getInstence();
            stockid = argParser.findParm(kOptSingleStock);
            stu.analysisReturnRatio(stockid, null, null);
            StockSqlUtil.performanceEntry pe = stu.getPerformance(stockid, false, true);
            float score = stu.getScore(pe, false);
            String stra = stu.getBuyinSelloutStrategy(stockid, pe);
            System.out.println("stock id:" + stockid + " score:" + score + " strategy:" + stra);
        } else {
            StockSqlUtil stu = StockSqlUtil.getInstence();
            StockListUtil su = new StockListUtil();
            su.getStockListFromUpdatedList();
            StockListUtil.StockIdEntry se;
            for (int i = 0; i < su.stockIdList.size(); i++) {
                se = su.stockIdList.get(i);
                System.out.println("processing item i:" + i + " id:" + se.id);
                stu.analysisReturnRatio(se.id, null, null);
            }
        }

    }
}
