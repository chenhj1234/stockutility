package com.company.commutil;

import com.company.GetArgs;
import com.company.StockListUtil;
import sqlutil.StockSqlUtil;

import java.sql.ResultSet;
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
    private static void createPriseHistTable(StockSqlUtil sqlU) {
        String tableName = sqlU.kPriseHistScoreTableName;
        sqlU.initCreateTableQuery();
        sqlU.addCreateTableColEntry("stockid", "varchar", "utf8", true, 20);
        sqlU.addCreateTableColEntry("date", "date", null, true, 0);
        sqlU.addCreateTableColEntry("duration", "int", null, true, 0);
        sqlU.addCreateTableColEntry("earn_10_pa", "boolean", null, true, 0);
        sqlU.addCreateTableColEntry("earn_20_pa", "boolean", null, true, 0);
        sqlU.addCreateTableColEntry("earn_30_pa", "boolean", null, true, 0);
        sqlU.addCreateTableColEntry("earn_40_pa", "boolean", null, true, 0);
        sqlU.addCreateTableColEntry("earn_50_pa", "boolean", null, true, 0);
        sqlU.addCreateTableColEntry("max_earning", "float", null, true, 0);
        sqlU.addCreateTableColEntry("total_earning", "float", null, true, 0);
        sqlU.addCreateTableColEntry("total_loss", "float", null, true, 0);
        sqlU.performCreateTable(tableName);
    }
    private static void getPriseHistScore(String stockid, StockSqlUtil sqlU, Date buyDate) {
        String tableName = sqlU.dailyInfoHistoryTable;
        float buyPrise = sqlU.getPrise(stockid, buyDate);
        if(buyPrise <= 0) return;
        float tracePrise;
        float maxPrise = 0;
        float totalPositiveEarning = 0;
        float totalNegativeEarning = 0;
        int recordCount = 0;
        int positiveCount = 0;
        int negativeCount = 0;
        boolean havePositiveReturn = false;
        boolean have10Pa = false;
        boolean have20Pa = false;
        boolean have30Pa = false;
        boolean have40Pa = false;
        boolean have50Pa = false;
        sqlU.initSelectTable();
        sqlU.addSelCol("dealprise");
        sqlU.addSelParmValue("stockid", stockid);
        sqlU.addSelOrder("date", false);
        ResultSet rSet = sqlU.performSelectTable(tableName);
        try {
            while (rSet.next()) {
                tracePrise = rSet.getFloat("dealprise");
                if(tracePrise > buyPrise) {
                    havePositiveReturn = true;
                    positiveCount ++;
                    if((tracePrise - buyPrise)/buyPrise > 0.1) {
                        have10Pa = true;
                    }
                    if((tracePrise - buyPrise)/buyPrise > 0.2) {
                        have20Pa = true;
                    }
                    if((tracePrise - buyPrise)/buyPrise > 0.3) {
                        have30Pa = true;
                    }
                    if((tracePrise - buyPrise)/buyPrise > 0.4) {
                        have40Pa = true;
                    }
                    if((tracePrise - buyPrise)/buyPrise > 0.5) {
                        have50Pa = true;
                    }
                    if(maxPrise < tracePrise) maxPrise = tracePrise;
                    totalPositiveEarning += (tracePrise - buyPrise);
                } else if(tracePrise < buyPrise) {
                    negativeCount ++;
                    totalNegativeEarning += (buyPrise - tracePrise);
                }
                recordCount ++;
                if(recordCount == 30 || recordCount == 90 || recordCount == 180 || recordCount == 270 || recordCount == 360) {
                    System.out.println("id:" + stockid + " " + recordCount + " days 10%:" + have10Pa + " 20%:" + have20Pa + " 30%:" + have30Pa + " 40%:" + have40Pa  + " 50%:" + have50Pa +
                    " max earning:" + (maxPrise - buyPrise)/buyPrise + " totEarn:" + totalPositiveEarning/positiveCount + " totLoss: -" + totalNegativeEarning/negativeCount);
                    sqlU.initInsertTable();
                    sqlU.insertValue("stockid", stockid);
                    sqlU.insertValue("date", buyDate);
                    sqlU.insertValue("duration", recordCount/30);
                    sqlU.insertValue("earn_10_pa", have10Pa);
                    sqlU.insertValue("earn_20_pa", have20Pa);
                    sqlU.insertValue("earn_30_pa", have30Pa);
                    sqlU.insertValue("earn_40_pa", have40Pa);
                    sqlU.insertValue("earn_50_pa", have50Pa);
                    sqlU.insertValue("max_earning", (maxPrise - buyPrise)/buyPrise);
                    if(positiveCount > 0) {
                        sqlU.insertValue("total_earning", totalPositiveEarning/positiveCount);
                    } else {
                        sqlU.insertValue("total_earning", 0);
                    }
                    if(negativeCount > 0) {
                        sqlU.insertValue("total_loss", totalNegativeEarning/negativeCount);
                    } else {
                        sqlU.insertValue("total_loss", 0);
                    }
                    tableName = sqlU.kPriseHistScoreTableName;
                    sqlU.insertIntoTable(tableName);
                }
                if(recordCount > 360) break;
            }
            sqlU.finishSelectQuery();
        } catch ( Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        garg.addOption("-s" , true);
        garg.addOption("--divhist" , false);
        garg.addOption("--prisehist" , false);
        garg.addOption("--date" , true);
        garg.processArgs(args);
        StockSqlUtil sqlU = StockSqlUtil.getInstence();
        Date monitorDate = new Date();
        if(garg.isArgOn("--date")) {
            String dateStr = garg.findParm("--date");
            monitorDate = sqlU.convertStrToJavaDate(dateStr);
        }
        if(garg.isArgOn("--divhist")) {
            sqlU.createHistDividendScoreTable(sqlU.kHistDividendScoreTableName);
        }
        if(garg.isArgOn("--prisehist")) {
            sqlU.createPriseHistTable();
        }
        if(!garg.isArgOn("-s")) {
            StockListUtil su = new StockListUtil();
            su.getStockListFromUpdatedList();
            StockListUtil.StockIdEntry se = null;
            for(int i = 0;i < su.stockIdList.size(); i++) {
                se = su.stockIdList.get(i);
                if(garg.isArgOn("--divhist")) {
                    sqlU.getOneHistDivScore(se.id, se.name, monitorDate);
                } else if( garg.isArgOn("--prisehist")){
                    sqlU.getPriseHistScore(se.id, monitorDate);
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
            } else if( garg.isArgOn("--prisehist")){
                sqlU.getPriseHistScore(stockid, monitorDate);
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
