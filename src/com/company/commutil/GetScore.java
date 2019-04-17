package com.company.commutil;

import com.company.GetArgs;
import sqlutil.StockSqlUtil;

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
    public static void main(String[] args) {
        float score = 0;
        float roeScore = 0;
        float rrScore = 0;
        float seasonEarnScore = 0;
        float yearEarnScore = 0;
        float announcedScore = 0;
        garg.addOption("-s" , true);
        garg.processArgs(args);
        if(!garg.isArgOn("-s")) {
            System.out.println("Usage : GetScore -s stockid");
            System.exit(-1);
        }
        String stockid = garg.findParm("-s");
        StockSqlUtil sqlU = new StockSqlUtil();
        StockSqlUtil.performanceEntry pe = sqlU.getPerformanceNoBuyin(stockid, false);
        roeScore = getRoeScore(pe.roe);
        rrScore = getRRScore(pe.returnratio);
        seasonEarnScore = getSeasonScore(pe.season);
        yearEarnScore = getAnnualScore(pe.year, pe.season);
        announcedScore = getAnnouncedScore(sqlU.checkThisYearShareAnnounced(stockid));
        score = roeScore + rrScore + seasonEarnScore + yearEarnScore + announcedScore;
        System.out.println("score: roe:" + roeScore + " rr:" + rrScore + " season:" + seasonEarnScore + " year:" + yearEarnScore + " ann:" + announcedScore + " tot:" + score);
    }
}