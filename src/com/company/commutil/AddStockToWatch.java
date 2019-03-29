package com.company.commutil;

import com.company.StockListUtil;
import sqlutil.StockSqlUtil;

import java.util.ArrayList;

public class AddStockToWatch {
    static ArrayList<String> mNameList = new ArrayList<>();
    static ArrayList<String> mIdList = new ArrayList<>();
    static ArrayList<StockListUtil.StockIdEntry> mWatchList = new ArrayList<>();
    private static void processArgs(String[] args) {
        for(int i = 0;i < args.length;i++) {
            String arg = args[i];
            switch(arg) {
                case "-s":
                    i++;
                    mIdList.add(args[i]);
                    break;
                case "-n":
                    i++;
                    mNameList.add(args[i]);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        StockSqlUtil sqlUtil = new StockSqlUtil();
        StockListUtil listUtil = new StockListUtil();
        ArrayList<StockListUtil.StockIdEntry> idEnt;
        processArgs(args);
        listUtil.getStockList();
        idEnt = listUtil.getIdFromName(mNameList, mIdList);
        sqlUtil.addWatchTable(idEnt);
    }
}
