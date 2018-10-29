package com.company;

import java.util.ArrayList;

public class StockInfoList {
    public class StockEntry {
        public ArrayList<String> stockEntry = new ArrayList<>();
        public StockEntry(ArrayList<String> se) {
            stockEntry = se;
        }
    }
    public ArrayList<StockEntry> stockInfoList = new ArrayList<>();
    public int addEntry(ArrayList<String> se) {
        StockEntry sen = new StockEntry(se);
        stockInfoList.add(sen);
        return stockInfoList.size();
    }
    public void printOneEntry(int index) {
        if(index >= stockInfoList.size()) {
            index = stockInfoList.size() - 1;
        }
        StockEntry se = stockInfoList.get(index);
        String seString;
        for(int i =0;i< se.stockEntry.size();i++) {
            seString = se.stockEntry.get(i);
            System.out.print(seString + " ");
        }
        System.out.println("");
    }
    public void printOneValue(int index) {
        String seString;
        for(int i =0;i< stockInfoList.size();i++) {
            StockEntry se = stockInfoList.get(i);
            seString = se.stockEntry.get(index);
            System.out.println(seString);
        }
    }
    public void insertOneStock() {
        String seString;
        String senString;
        for(int i =0;i< stockInfoList.size();i++) {
            StockEntry se = stockInfoList.get(i);
            seString = se.stockEntry.get(0);
            senString = se.stockEntry.get(0);
        }
    }
}
