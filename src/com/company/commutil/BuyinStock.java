package com.company.commutil;

import sqlutil.StockSqlUtil;

public class BuyinStock {
    private final static int FUNC_BUYIN_WATCHLIST = 0;
    private final static int FUNC_BUYIN_STOCK = 1;
    private static int func = FUNC_BUYIN_WATCHLIST;
    private static String buyinStockId;
    private static void processArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--buyin-watch-list":
                    func = FUNC_BUYIN_WATCHLIST;
                    break;
                case "--buyin-stock":
                    func = FUNC_BUYIN_STOCK;
                    i++;
                    buyinStockId = args[i];
                    break;
            }
        }
    }
    public static void main(String[] args) {
        StockSqlUtil sqlUtil = new StockSqlUtil();
        switch(func) {
            case FUNC_BUYIN_WATCHLIST:
                sqlUtil.buyinWatchList();
                break;
            case FUNC_BUYIN_STOCK:
                sqlUtil.buyinSingleStock(buyinStockId);
                break;
        }
    }
}
