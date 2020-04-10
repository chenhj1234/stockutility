package com.company.commutil;

import com.company.GetArgs;
import sqlutil.StockSqlUtil;

import java.util.Date;

public class SimulateStockBuying {
    public static void main(String[] args) {
        GetArgs argParser = new GetArgs();
        argParser.processArgs(args);
        String dateStr;
        StockSqlUtil sqlUtil = new StockSqlUtil();
        Date buyDate = null;
        Date sellDate = new Date();
        String stockid = null;
        if(argParser.isArgOn(GetArgs.OPT_BUY_DATE)) {
            dateStr = argParser.findParm(GetArgs.OPT_BUY_DATE);
            buyDate = sqlUtil.convertStrToJavaDate(dateStr);
        }
        if(argParser.isArgOn(GetArgs.OPT_SELL_DATE)) {
            dateStr = argParser.findParm(GetArgs.OPT_SELL_DATE);
            sellDate = sqlUtil.convertStrToJavaDate(dateStr);
        }
        if(argParser.isArgOn(GetArgs.OPT_SINGLE_STOCK)) {
            stockid = argParser.findParm(GetArgs.OPT_SINGLE_STOCK);
        }

        if(buyDate == null) {
            System.out.println("Specify --buydate 0000-00-00");
            return;
        }
        if(stockid == null) {
            System.out.println("Specify -s stock_id");
            return;
        }
        System.out.println("Simulate buy stock " + stockid + " at date " + buyDate + " and sell at " + sellDate);
    }
}
