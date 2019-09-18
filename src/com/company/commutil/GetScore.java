package com.company.commutil;

import com.company.GetArgs;
import com.company.StockListUtil;
import sqlutil.StockSqlUtil;

import java.util.Date;

public class GetScore {
    static GetArgs garg = new GetArgs();
    public static float getRoeScore(float roe) {
        if(roe > 2)
            return 2;
        else if(roe > 0)
            return 1;
        else if(roe > -3)
            return 0;
        return -1;
    }
    public static float getRRScore(float rr) {
        if(rr > 10)
            return 2;
        else if(rr > 8)
            return 1;
        else if(rr > 4)
            return 0;
        return -1;
    }
    public static float getSeasonScore(float[] se) {
        float c = 0;
        for(int i = 0;i < 4;i++) {
            c += (se[i] > 0 ? 1:0);
        }
        if(c > 3) return 2;
        else if(c > 2) return 1;
        else if(c > 1) return 0;
        return -1;
    }
    public static float getAnnualScore(float[] sa, float[] se) {
        if(se[0] + se[1] + se[2] + se[3] > sa[0]) {
            return 2;
        }
        return -1;
    }
    public static float getAnnouncedScore(boolean ann) {
        if(ann) return 1;
        return 0;
    }
    public static void getOneStockScore(String stockid) {
        float score = 0;
        float roeScore = 0;
        float rrScore = 0;
        float seasonEarnScore = 0;
        float yearEarnScore = 0;
        float announcedScore = 0;
        StockSqlUtil sqlU = StockSqlUtil.getInstence();
        StockSqlUtil.performanceEntry pe = sqlU.getPerformance(stockid, false, true);
        roeScore = getRoeScore(pe.roe);
        rrScore = getRRScore(pe.returnratio);
        seasonEarnScore = getSeasonScore(pe.season);
        yearEarnScore = getAnnualScore(pe.year, pe.season);
        announcedScore = getAnnouncedScore(sqlU.checkThisYearShareAnnounced(stockid));
        score = roeScore + rrScore + seasonEarnScore + yearEarnScore + announcedScore;
        System.out.println("stock id:" + stockid + " roe:" + pe.roe + " score:" + roeScore + " rr:" + pe.returnratio + " score:" + rrScore + " season:" + seasonEarnScore + " year:" + yearEarnScore + " ann:" + announcedScore + " tot:" + score);
        //sqlU.analysisShareHistory(stockid);

    }
//    String[] dbHistDividendScoreColName = new String[] {
//      "stockid",
//      "positive_dividend_score",
//      "return_ratio_score"
//    };
//    int kHistDividendScoreColIndex_StockId = 0;
//    int kHistDividendScoreColIndex_PositiveDividendScore = 0;
//    int kHistDividendScoreColIndex_ReturnRatioScore = 0;
//    String mCreateHistDividendTableQuery = "CREATE TABLE __HIST_DIVIDEND_SCORE_TABLE__ ("
//            + dbHistDividendScoreColName[kHistDividendScoreColIndex_StockId] + " VARCHAR(20) NOT NULL,"
//            + dbHistDividendScoreColName[kHistDividendScoreColIndex_StockId] + " FLOAT NOT NULL,"
//            + dbHistDividendScoreColName[kHistDividendScoreColIndex_StockId] + " FLOAT NOT NULL)";
//    ;
//    public void createHistDividendScoreTable(String tableName) {
//        StockSqlUtil sSqlU = StockSqlUtil.getInstence();
//        String dbTableName = sSqlU.getTabelNameWithDB(tableName);
//        String query = mCreateHistDividendTableQuery.replace("__CURRENCY_TABLE__", dbTableName);
//        if(!sSqlU.checkTableExist(tableName)) {
//            boolean retv = sSqlU.performStatement(query, true);
//            if(!retv) {
//                System.err.println("Create table:" + tableName + " failed.");
//            }
//        }
//    }

    public static void getOneHistDivScore(String stockid) {
        float rrScore = 0;
        float rrScoreAvg = 0;
        StockSqlUtil sqlU = StockSqlUtil.getInstence();
        StockSqlUtil.performanceEntry pe = sqlU.getPerformance(stockid, false, true);
        int positiveCnt = 0;
        float rr = 0;
        float diV;
        for(int i = 0;i < pe.div.size();i++) {
            diV = pe.div.get(i);
            if(diV > 0) {
                positiveCnt ++;
            }
            rr = diV / pe.lastprise;
            if(rr > 0.1) {
                rrScore += 1.5;
            }
            else if(rr > 0.08) {
                rrScore += 1.2;
            }
            else if(rr > 0.05) {
                rrScore += 1;
            }
            else if(rr > 0) {
                rrScore += 0.5;
            }
            rrScoreAvg += 1;
        }
//        roeScore = getRoeScore(pe.roe);
//        rrScore = getRRScore(pe.returnratio);
//        seasonEarnScore = getSeasonScore(pe.season);
//        yearEarnScore = getAnnualScore(pe.year, pe.season);
//        announcedScore = getAnnouncedScore(sqlU.checkThisYearShareAnnounced(stockid));
//        score = roeScore + rrScore + seasonEarnScore + yearEarnScore + announcedScore;
        System.out.println("stock id:" + stockid + " rr score:" + rrScore + " avg rr score" + rrScoreAvg + " positive score:" + (float)positiveCnt/(float)pe.div.size());
        //sqlU.analysisShareHistory(stockid);

    }

    private static void getPriseHistScore(String stockid, StockSqlUtil sqlU) {
        String tableName = sqlU.dailyInfoHistoryTable;
        sqlU.initSelectTable();
        sqlU.addSelCol("dealprise");
    }
    public static void main(String[] args) {
        garg.addOption("-s" , true);
        garg.addOption("--divhist" , false);
        garg.addOption("--date" , true);
        garg.processArgs(args);
        StockSqlUtil sqlU = StockSqlUtil.getInstence();
        Date monitorDate = null;
        if(garg.isArgOn("--date")) {
            String dateStr = garg.findParm("--date");
            monitorDate = sqlU.convertStrToJavaDate(dateStr);
        }
        if(garg.isArgOn("--divhist")) {
            sqlU.createHistDividendScoreTable(sqlU.kHistDividendScoreTableName);
        }
        if(!garg.isArgOn("-s")) {
            StockListUtil su = new StockListUtil();
            su.getStockListFromUpdatedList();
            StockListUtil.StockIdEntry se = null;
            for(int i = 0;i < su.stockIdList.size(); i++) {
                se = su.stockIdList.get(i);
                if(garg.isArgOn("--divhist")) {
                    sqlU.getOneHistDivScore(se.id, se.name, monitorDate);
                } else {
                    getOneStockScore(se.id);
                }
//                StockSqlUtil.performanceEntry pe = sqlU.getPerformance(se.id, false, true);
//                roeScore = getRoeScore(pe.roe);
//                rrScore = getRRScore(pe.returnratio);
//                seasonEarnScore = getSeasonScore(pe.season);
//                yearEarnScore = getAnnualScore(pe.year, pe.season);
//                announcedScore = getAnnouncedScore(sqlU.checkThisYearShareAnnounced(se.id));
//                score = roeScore + rrScore + seasonEarnScore + yearEarnScore + announcedScore;
//                System.out.println("stock id:" + se.id + " roe:" + pe.roe + " score:" + roeScore + " rr:" + pe.returnratio + " score:" + rrScore + " season:" + seasonEarnScore + " year:" + yearEarnScore + " ann:" + announcedScore + " tot:" + score);
            }
        } else {
            StockListUtil su = new StockListUtil();
            String stockid = garg.findParm("-s");
            String stockName = su.getStockNameFromId(stockid);

            if(garg.isArgOn("--divhist")) {
                sqlU.getOneHistDivScore(stockid, stockName,monitorDate);
            } else {
                getOneStockScore(stockid);
            }
//            StockSqlUtil sqlU = new StockSqlUtil();
//            StockSqlUtil.performanceEntry pe = sqlU.getPerformance(stockid, false, true);
//            roeScore = getRoeScore(pe.roe);
//            rrScore = getRRScore(pe.returnratio);
//            seasonEarnScore = getSeasonScore(pe.season);
//            yearEarnScore = getAnnualScore(pe.year, pe.season);
//            announcedScore = getAnnouncedScore(sqlU.checkThisYearShareAnnounced(stockid));
//            score = roeScore + rrScore + seasonEarnScore + yearEarnScore + announcedScore;
//            System.out.println("score: roe:" + roeScore + " rr:" + rrScore + " season:" + seasonEarnScore + " year:" + yearEarnScore + " ann:" + announcedScore + " tot:" + score);
        }
    }
}
