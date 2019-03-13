package htmlparser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StackBasicInformation {
    // 代號
    public String StockNumber;
    // 名稱
    public String StockName;
    // 年度
    public int year;
    // 現金股利
    public float CashDividend;
    // 股票股利
    public float StockDividend;
    // 除權日期
    public Date ExclusionDate;
    // 除息日期
    public Date EliminationDate;
    // 營業毛利率
    public float GrossProfitMargin;
    // 營業利益率
    public float OperatingProfitMargin;
    // 每股淨值
    public float BookValuePerShare;
    // 稅前淨利率
    public float EarningBeforeTaxMargin;
    // 資產報酬率
    public float ReturnOnAssets;
    // 股東權益報酬率
    public float ReturnOnEquity;
    public class seasonRecord {
        public int year;
        public int season;
        public float share;
        public seasonRecord(int y, int s, float sh) {
            year = y;
            season = s;
            share = sh;
        }
    }
    // 每股盈餘 季度
    public ArrayList<seasonRecord> seasonRecordList = new ArrayList<>();
    // 每股盈餘 年度
    public ArrayList<seasonRecord> yearRecordList = new ArrayList<>();
    public void printStackBasicInformation() {
        System.out.println("Stock name:" + StockName + " stock number:" + StockNumber);
        System.out.println("Dividend cash:" + CashDividend + " date:" + ExclusionDate);
        System.out.println("Dividend cash:" + StockDividend + " date:" + EliminationDate);
        System.out.println("營業毛利率:" + GrossProfitMargin + " 營業利益率:" + OperatingProfitMargin +
                           " 股東權益報酬率:" + ReturnOnEquity + " 資產報酬率:" + ReturnOnAssets +
                           " 稅前淨利率:" + EarningBeforeTaxMargin);
    }
}
