package sqlutil;

import com.company.StockListUtil;
import htmlparser.DividendGrammarParser;
import htmlparser.EarningGrammarDataListener;
import htmlparser.HTMLParserDividendListener;
import htmlparser.StackBasicInformation;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class StockSqlUtil {
    private boolean DEBUG_SQL_CMD = false;
    private boolean DEBUG_VERBOSE = false;
    private Connection mConnection = null;
    private Statement mStatement = null;
    private ResultSet mResultSet = null;
    private PreparedStatement mPreparedStatement = null;
    private String serverName = "10.20.71.108";
//    private String serverName = "192.168.1.90";
    private String mydatabase = "stock_identifier_alpha";
    // Table for stock id and name matching list
    public final String tblStockId = "stockid";
    public final String tblStockIdUpdate = "stockid_for_update";
    public final String tblStockIdUpdateAlpha = "stockid_for_update_alpha";

    public final String dailyInfoTable = "daily_info_table";
    public final String dailyInfoHistoryTable = "daily_info_table_hist";
    public final String monthlyEarnTable = "monthly_earn_table";
    public final String annuallyInfoTable = "annual_info_table";
    public final String annualDividendTable = "annual_share_table";
    public final String seasonEarningInfoTable = "season_info_table";
    public final String annualEarningShareTable = "annural_earning_share_table";
    public final String seasonEarningShareTable = "season_earning_share_table";
    public final String buyinTableAnalysis = "buyin_table_analysis";
    public final String buyinTableAnalysisScore = "buyin_table_analysis_score";
    public final String buyinTable = "buyin_table";
    public final String watchTable = "watch_table";
    public final String watchBuyinTable = "watch_buyin_table";
    public final String watchBuyinHistTable = "watch_buyin_hist_table";
    public final String watchBuyinPerformanceTable = "watch_buyin_performance";
    public final String watchBuyinAnalysisTable = "watch_buyin_table_analysis";
    public final String dailyExchangeRate = "daily_exchange_rate";
    public final String buyinPerformance = "buyin_performance";
    public final String buyinPerformanceAccu = "buyin_performance_accu";
    public final String strategyTable = "buyin_strategy";
    public final String kHistDividendScoreTableName = "hist_div_score";
    public final String kPriseHistScoreTableName = "prise_hist_score";
    public final String kSimulationTableName = "simulation_table";
    public final String kSimulationYoyTableName = "simulation_score_yoy_table";

    // With regular daily buyin, the stockid and date will be checked, if matched, not buyin
    public static final int BUYIN_REGULAR_DAILY = 0;
    // With regular daily buyin, the stockid will be checked, if matched, buyin, add amount and count avarage prise
    public static final int BUYIN_ADD_AVG = 1;

    private String url = "jdbc:mysql://" + serverName + "/" + mydatabase + "?serverTimezone=UTC&useUnicode=yes&characterEncoding=UTF-8";

    private String username = "chenhj";
    private String password = "holmas0228";
    private String table_stockid = mydatabase + "." + tblStockId;
    private String driverName = "com.mysql.cj.jdbc.Driver";

    public String getTblStockIdUpdate() {
        return tblStockIdUpdate;
    }

    public String getTblStockIdUpdate(boolean isAlpha) {
        if (isAlpha) {
            return tblStockIdUpdateAlpha;
        }
        return tblStockIdUpdate;
    }

    public String getTabelNameWithDB(String tableName) {
        return mydatabase + "." + tableName;
    }
    private boolean connectToServer() {
        try {
            Class.forName(driverName); // here is the ClassNotFoundException
            if(mConnection == null) {
                mConnection = DriverManager.getConnection(url, username, password);
                mStatement = mConnection.createStatement();
            }
            if(mConnection == null) {
                return false;
            }
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    private static StockSqlUtil instance = null;
    static public StockSqlUtil getInstence() {
        if(instance == null) {
            instance = new StockSqlUtil();
            instance.connectToServer();
        }
        return instance;
    }

    public boolean isConnected() {
        return (mConnection != null);
    }

    public Statement getConnectionStatement() {
        if(mConnection == null) return null;
        try {
            if (mStatement == null) mStatement = mConnection.createStatement();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return mStatement;
    }

    public void closeConnectionStatement() {
        if(mConnection != null && mStatement != null) {
            try {
                mStatement.close();
                mStatement = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startConnection() {
        closeConnection();
        connectToServer();
    }

    public void closeConnection() {
        closeConnectionStatement();
        if(mConnection != null) {
            try {
                mConnection.close();
                mConnection = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public boolean insertDailyTable(String stockid, Date date, float buyin, float sellout, float dealprise, float shift, int amount, boolean updaterec) {
        return insertDailyTable(stockid, date, buyin,sellout,dealprise,shift,amount,updaterec, dailyInfoTable);
    }
    public boolean insertDailyTable(String stockid, Date date, float buyin, float sellout, float dealprise, float shift, int amount, boolean updaterec, String dailyTargetTableName) {
        String tableName = dailyTargetTableName;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        //java.sql.Date date = new java.sql.Date();
        String dateString = "null";
        String timeString = "null";
        if(date == null || dealprise == -1) return false;
//        dateString = "'" + dateFormat.format(date) + "'";
//        timeString = "'" + timeFormat.format(date) + "'";
        dateString = dateFormat.format(date);
        timeString = timeFormat.format(date);
        if(stockid.equals("5601")) {
            System.out.println("Go on");
        }
        if(checkIdExistInTable(tableName, stockid, dateFormat.format(date))) {
//            PreparedStatement stmt = mConnection.prepareStatement("update`user` set `exp` = '666'  where `username` = '"+loggedusername+"'");
            if(updaterec) {
                if (DEBUG_VERBOSE)
                    System.out.println("id:" + stockid + " date:" + dateFormat.format(date) + " exist, try update");
                initUpdateTable();
                addUpdateCol("buyin", buyin);
                addUpdateCol("sellout", sellout);
                addUpdateCol("dealprise", dealprise);
                addUpdateCol("amount", amount);
                addUpdateCol("time", timeFormat.format(date));
                addUpdateCol("returnratio", 0);
                addUpdateCol("score", 0);
                addUpdateCol("suggestion", "nosuggestion");
                addUpdateParam("stockid", stockid);
                addUpdateParam("date", dateFormat.format(date));
                performUpdateTable(tableName);
                if (DEBUG_VERBOSE)
                    System.out.println("update success");
//                try {
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    System.out.println("update fail");
//                }
            }
        } else {
            if(DEBUG_VERBOSE) System.out.println("id:" + stockid + " date:" + dateFormat.format(date) + " not exist, try insert");
            initInsertTable();
            insertValue("stock", stockid);
            insertValue("buyin", buyin);
            insertValue("sellout", sellout);
            insertValue("dealprise", dealprise);
            insertValue("amount", amount);
            insertValue("date", dateString);
            insertValue("time", timeString);
            insertValue("returnratio", 0);
            insertValue("score", 0);
            insertValue("suggestion", "nosuggestion");
            insertIntoTable(tableName);

//            String query = "insert into " + tableName + " values ( '" + stockid + "', " +
//                    buyin + ", " + sellout + ", " + dealprise + ", " + amount + ", " + dateString + ", " + timeString + ", 0" + ")";
//
//            try {
//                System.out.println(query);
//                connectToServer();
//                mStatement.executeUpdate(query);
//                if (mConnection != null) {
//                    mConnection.close();
//                    mConnection = null;
//                }
//                if(DEBUG_VERBOSE) System.out.println("insert success");
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("insert fail");
//                System.exit(1);
//            }
        }
        return true;
    }

    public boolean insertAnnualTable(String stockid, int year, float roe, float roa, float stock_dividend, float cash_dividend,
                                     Date exdate, Date eldate,
                                     float gross_profit_margin, float operating_profit_margin,
                                     float book_value_per_share, float earning_before_tax_margin) {
        String tableName = annuallyInfoTable;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        //java.sql.Date date = new java.sql.Date();
        String exDateString = "null";
        String elDateString = "null";
        if(exdate != null) exDateString = "'" + dateFormat.format(exdate) + "'";
        if(eldate != null) elDateString = "'" + dateFormat.format(eldate) + "'";
        if(year == 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            year = cal.get(Calendar.YEAR);
        }
        if(checkIdExistInTable(tableName, stockid, year)) {
            if(DEBUG_VERBOSE) System.out.println("id:" + stockid + " year:" + year + " exist, try update");
            initUpdateTable();
            addUpdateCol("roe", roe);
            addUpdateCol("roa", roa);
            addUpdateCol("stock_dividend", stock_dividend);
            addUpdateCol("cash_dividend", cash_dividend);
            addUpdateCol("exclusion_date", exdate);
            addUpdateCol("elimination_date", eldate);
            addUpdateCol("gross_profit_margin", gross_profit_margin);
            addUpdateCol("operating_profit_margin", operating_profit_margin);
            addUpdateCol("book_value_per_share", book_value_per_share);
            addUpdateCol("earning_before_tax_margin", earning_before_tax_margin);
            addUpdateParam("stockid", stockid);
            addUpdateParam("year", year);
            performUpdateTable(tableName);

            if(DEBUG_VERBOSE) System.out.println("update " + tableName + " success");
//            try {
//                connectToServer();
//
//                PreparedStatement stmt = mConnection.prepareStatement("UPDATE " + tableName +
//                        " SET roe = ?, roa = ? , stock_dividend = ? , cash_dividend = ? , " +
//                        "exclusion_date = ? , elimination_date = ? , " +
//                        "gross_profit_margin = ? , operating_profit_margin = ? ," +
//                        "book_value_per_share = ? , earning_before_tax_margin = ?  " +
//                        "WHERE stockid = ? AND year = ?");
//                stmt.setFloat(1, roe);
//                stmt.setFloat(2, roa);
//                stmt.setFloat(3, stock_dividend);
//                stmt.setFloat(4, cash_dividend);
//                java.sql.Date sqlDate = null;
//                if(exdate == null) {
//                    stmt.setDate(5, null);
//                } else {
//                    sqlDate = java.sql.Date.valueOf(dateFormat.format(exdate));
//                    stmt.setDate(5, sqlDate);
//                }
//                if(eldate == null) {
//                    stmt.setDate(6, null);
//                } else {
//                    sqlDate = java.sql.Date.valueOf(dateFormat.format(eldate));
//                    stmt.setDate(6, sqlDate);
//                }
//                stmt.setFloat(7, gross_profit_margin);
//                stmt.setFloat(8, operating_profit_margin);
//                stmt.setFloat(9, book_value_per_share);
//                stmt.setFloat(10, earning_before_tax_margin);
//                stmt.setString(11, stockid);
//                stmt.setInt(12, year);
//                stmt.executeUpdate();
//                stmt.close();
//                if(mConnection != null) {
//                    mConnection.close();
//                    mConnection = null;
//                }
//            } catch(Exception e) {
//                e.printStackTrace();
//                System.out.println("update " + tableName + " fail");
//            }
            return true;
        } else {
            if (DEBUG_VERBOSE) System.out.println("id:" + stockid + " year:" + year + " not exist, try insert");
            initInsertTable();
            insertValue("stockid", stockid);
            insertValue("year", year);
            insertValue("roe", roe);
            insertValue("roa", roa);
            insertValue("stock_dividend", stock_dividend);
            insertValue("cash_dividend", cash_dividend);
            insertValue("exclusion_date", exdate);
            insertValue("elimination_date", eldate);
            insertValue("gross_profit_margin", gross_profit_margin);
            insertValue("operating_profit_margin", operating_profit_margin);
            insertValue("book_value_per_share", book_value_per_share);
            insertValue("earning_before_tax_margin", earning_before_tax_margin);
            insertIntoTable(tableName);
            if (DEBUG_VERBOSE) System.out.println("insert " + tableName + " success");
//        String query = "insert into " + tableName + " values ( '" + stockid + "', " +
//                year + ", " + roe + ", " + roa + ", " + stock_dividend + ", " + cash_dividend +
//                ", " + exDateString + ", " + elDateString + ", " +
//                gross_profit_margin + ", " + operating_profit_margin + ", " +
//                book_value_per_share + ", " + earning_before_tax_margin + ")";
//        try {
//            if(DEBUG_SQL_CMD) System.out.println(query);
//            connectToServer();
//            mStatement.executeUpdate(query);
//            mStatement.close();
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
//            if(DEBUG_VERBOSE) System.out.println("insert " + tableName + " success");
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("insert " + tableName + " failed");
//            System.exit(1);
//        }
            return true;
        }
    }

    public boolean insertAnnualDividendTable(String stockid, ArrayList<HTMLParserDividendListener.dividendRecord> aList) {
        String tableName = annualDividendTable;
        for(int i = 0;i < aList.size();i++) {
            HTMLParserDividendListener.dividendRecord rec = aList.get(i);
            if(checkIdExistInTable(tableName, stockid, rec.year)) {
                if(DEBUG_VERBOSE) System.out.println("id:" + stockid + " year:" + rec.year + " exist, leave");
                initUpdateTable();
                addUpdateCol("cash_dividend", rec.dividend[DividendGrammarParser.TYPE_INDEX_CASH]);
                addUpdateCol("earn_dividend", rec.dividend[DividendGrammarParser.TYPE_INDEX_EARN]);
                addUpdateCol("stock_dividend_capital", rec.dividend[DividendGrammarParser.TYPE_INDEX_STOCK_CAP]);
                addUpdateCol("stock_dividend", rec.dividend[DividendGrammarParser.TYPE_INDEX_STOCK]);
                addUpdateCol("total_dividend", rec.dividend[DividendGrammarParser.TYPE_INDEX_TOTAL]);
                addUpdateParam("stockid", stockid);
                addUpdateParam("year", rec.year);
                performUpdateTable(tableName);
            } else {
                if (DEBUG_VERBOSE) System.out.println("id:" + stockid + " year:" + rec.year + " not exist, try insert");
                initInsertTable();
                insertValue("stockid", stockid);
                insertValue("year", rec.year);
                insertValue("cash_dividend", rec.dividend[DividendGrammarParser.TYPE_INDEX_CASH]);
                insertValue("earn_dividend", rec.dividend[DividendGrammarParser.TYPE_INDEX_EARN]);
                insertValue("stock_dividend_capital", rec.dividend[DividendGrammarParser.TYPE_INDEX_STOCK_CAP]);
                insertValue("stock_dividend", rec.dividend[DividendGrammarParser.TYPE_INDEX_STOCK]);
                insertValue("total_dividend", rec.dividend[DividendGrammarParser.TYPE_INDEX_TOTAL]);
                insertIntoTable(tableName);
            }
        }
        return true;
    }

    public boolean checkIdExistInTable(String tableName, String stockid) {
//        String checkQuery = "";
//            checkQuery = "select * from " + tableName +
//                    " where stockid = '" + stockid + "'";
        boolean retval = false;
        try {
//            connectToServer();
            initSelectTable();
            addSelParmValue("stockid", stockid);
            mResultSet = performSelectTable(tableName);
            if(mResultSet.next()) {
                retval = true;
            }
            finishSelectQuery();
//            mResultSet.close();
//            mResultSet = null;
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public boolean checkIdExistInTable(String tableName, String stockid, int year) {
//        String checkQuery = "";
//        checkQuery = "select * from " + tableName + " where stockid = '" + stockid + "' and year = " + year;
        boolean retval = false;
        try {
//            connectToServer();
            initSelectTable();
            addSelParmValue("stockid", stockid);
            addSelParmValue("year", year);
            mResultSet = performSelectTable(tableName);
//            mResultSet = mStatement.executeQuery(checkQuery);
            if(mResultSet.next()) {
                retval = true;
            }
            finishSelectQuery();
//            mResultSet.close();
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public boolean checkIdYearMonthExistInTable(String tableName, String stockid, int year, int month) {
//        String checkQuery = "";
//        checkQuery = "select * from " + tableName + " where stockid = '" + stockid + "' and year = " + year + " and month = " + month;
        boolean retval = false;
        try {
//            connectToServer();
//            mResultSet = mStatement.executeQuery(checkQuery);
            initSelectTable();
            addSelParmValue("stockid", stockid);
            addSelParmValue("year", year);
            addSelParmValue("month", month);
            mResultSet = performSelectTable(tableName);
            if(mResultSet.next()) {
                retval = true;
            }
            finishSelectQuery();
//            mResultSet.close();
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public boolean checkIdYearSeasonExistInTable(String tableName, String stockid, int year, int season) {
//        String checkQuery = "";
//        checkQuery = "select * from " + tableName + " where stockid = '" + stockid + "' and year = " + year + " and season = " + season;
        boolean retval = false;
        try {
//            connectToServer();
//            mResultSet = mStatement.executeQuery(checkQuery);
            initSelectTable();
            addSelParmValue("stockid", stockid);
            addSelParmValue("year", year);
            addSelParmValue("season", season);
            performSelectTable(tableName);
            if(mResultSet.next()) {
                retval = true;
            }
            finishSelectQuery();
//            mResultSet.close();
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public boolean checkIdExistInTable(String tableName, String stockid, String dateStr) {
        String checkQuery = "";
        checkQuery = "select * from " + tableName +
                " where stockid = '" + stockid + "' and date = '" + dateStr + "'";
        boolean retval = false;
        if(DEBUG_SQL_CMD) System.out.println("query:" + checkQuery);
        try {
//            connectToServer();
//            mResultSet = mStatement.executeQuery(checkQuery);
            initSelectTable();
            addSelParmValue("stockid", stockid);
            addSelParmValue("date", dateStr);
            performSelectTable(tableName);
            if(mResultSet.next()) {
                retval = true;
            }
            finishSelectQuery();
//            mResultSet.close();
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public boolean checkColumnExist(String tableName, String colname1, String colval1 , String colname2, String colval2) {
//        String checkQuery = "";
//        checkQuery = "select * from " + tableName + " where " +colname1 + " = '" + colval1 + "' and "+ colname2+" = '" + colval2 + "'";
        boolean retval = false;
//        if(DEBUG_SQL_CMD) System.out.println("query:" + checkQuery);
        try {
//            connectToServer();
//            mResultSet = mStatement.executeQuery(checkQuery);
            initSelectTable();
            addSelParmValue(colname1, colval1);
            addSelParmValue(colname2, colval2);
            performSelectTable(tableName);
            if(mResultSet.next()) {
                retval = true;
            }
            finishSelectQuery();
//            mResultSet.close();
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public boolean checkColumnExist(String tableName, String[] colname, String[] colval) {
        boolean retval = false;
        int ind;
        try {
            initSelectTable();
            for(ind = 0; ind < colname.length; ind++) {
                addSelParmValue(colname[ind], colval[ind]);
            }
            performSelectTable(tableName);
            if(mResultSet.next()) {
                retval = true;
            }
            finishSelectQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public boolean checkIdExistInTable(String tableName, String stockid, String colName, String colStr) {
        String checkQuery;
        boolean retval = false;
        try {
            initSelectTable();
            addSelParmValue("stockid", stockid);
            addSelParmValue(colName, colStr);
            performSelectTable(tableName);
            if(mResultSet.next()) {
                retval = true;
            }
            finishSelectQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public boolean checkIdExistInTable(String tableName, String stockid, String colName, int colval) {
        String checkQuery;
        boolean retval = false;
        try {
            initSelectTable();
            addSelParmValue("stockid", stockid);
            addSelParmValue(colName, colval);
            performSelectTable(tableName);
            if(mResultSet.next()) {
                retval = true;
            }
            finishSelectQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public boolean checkIdExistInTable(String tableName, String stockid, String colName, Date colval) {
        String checkQuery;
        boolean retval = false;
        try {
            initSelectTable();
            addSelParmValue("stockid", stockid);
            addSelParmValue(colName, colval);
            performSelectTable(tableName);
            if(mResultSet.next()) {
                retval = true;
            }
            finishSelectQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public void analysisReturnRatio(String stockid, Date analysisDate, String tab) {
//        String checkQuery;
        String tableName = annualDividendTable;
//        checkQuery = "select * from " + tableName + " where stockid = '" + stockid + "' order by year desc";
        float tdiv = 0, sdiv = 0, prz = 0, cdiv = 0;
        try {
            getLastReturnRatio(stockid, true);
            prz = mBuyinPrise;
            tableName = annuallyInfoTable;
            initSelectTable();
            addSelParmValue("stockid", stockid);
            addSelOrder("year", false);
            mResultSet = performSelectTable(tableName);
            if(mResultSet.next()) {
                cdiv = mResultSet.getFloat("cash_dividend");
                sdiv = mResultSet.getFloat("stock_dividend");
                tdiv = sdiv + cdiv;
                if(DEBUG_VERBOSE) System.out.println("tdiv:" + tdiv + " sdiv:" + sdiv);
            }
            finishSelectQuery();
            if(analysisDate == null) {
                analysisDate = mLastBuyDate;
            }
            if(tab == null) {
                tableName = dailyInfoTable;
            } else {
                tableName = tab;
            }
            float ratio = 0;
            if(prz != 0) ratio = (tdiv * 100 )/prz;
            StockSqlUtil.performanceEntry pe = getPerformance(stockid, false, true);
            pe.returnratio = ratio;
            float score = getScore(pe, false);
            String stra = getBuyinSelloutStrategy(stockid, pe);
            System.out.println("tdiv:" + tdiv + " prz:" + prz + " rr:" + ratio + " score:" + score);
            initUpdateTable();
            addUpdateCol("returnratio", ratio);
            addUpdateCol("score", score);
            addUpdateCol("suggestion", stra);
            addUpdateParam("stockid", stockid);
            addUpdateParam("date", analysisDate);
            performUpdateTable(tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public java.sql.Date convertJavaDateToMySQL(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return java.sql.Date.valueOf(dateFormat.format(date));
    }

    public String convertJavaDateToMySQLStr(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(date != null) {
            return dateFormat.format(date);
        }
        return "null";
    }

    public int getWeekdatFromDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }
    public int getMonthdateFromDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dd = c.get(Calendar.DAY_OF_MONTH);
        return dd;
    }
    public int getMonthFromDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int mm = c.get(Calendar.MONTH);
        return mm;
    }
    public int getYearFromDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int yy = c.get(Calendar.YEAR);
        return yy;
    }

    public Date getDateOfYesterday(Date todayDate) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public Date getDateOfYesterday() {
        return getDateOfYesterday(new Date());
    }

    public Date getDateOfTomorrow(Date todayDate) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    public Date convertStrToJavaDate(String dateStr) {
        DateFormat dateFormat;
        if(dateStr.indexOf("-") >= 0) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            dateFormat = new SimpleDateFormat("yyyyMMdd");
        }
        try {
            return dateFormat.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    ArrayList<String> insertData = null;
    String insertTableName;
    public ArrayList<String> initInsertTable() {
        insertData = new ArrayList<>();
        return insertData;
    }
    public void insertNullValue(String name) {
        String insertStr = "null";
        insertData.add(insertStr);
    }
    public void insertValue(String name, String value) {
        String insertStr = "'" + value + "'";
        insertData.add(insertStr);
    }
    public void insertValue(String name, int value) {
        String insertStr = "" + value;
        insertData.add(insertStr);
    }
    public void insertValue(String name, float value) {
        String insertStr = "" + value;
        insertData.add(insertStr);
    }
    public void insertValue(String name, Date value) {
        String insertStr;
        if(value != null) {
            insertStr = "'" + convertJavaDateToMySQLStr(value) + "'";
        } else {
            insertStr = " null ";
        }
        insertData.add(insertStr);
    }
    public void insertValue(String name, boolean value) {
        String insertStr = "'" + (value?1:0) + "'";
        insertData.add(insertStr);
    }
    public void insertNullValue(String name, ArrayList<String> aList) {
        String insertStr = "null";
        aList.add(insertStr);
    }
    public void insertValue(String name, String value, ArrayList<String> aList) {
        String insertStr = "'" + value + "'";
        aList.add(insertStr);
    }
    public void insertValue(String name, int value, ArrayList<String> aList) {
        String insertStr = "" + value;
        aList.add(insertStr);
    }
    public void insertValue(String name, float value, ArrayList<String> aList) {
        String insertStr = "" + value;
        aList.add(insertStr);
    }
    public void insertValue(String name, Date value, ArrayList<String> aList) {
        String insertStr = "'" + convertJavaDateToMySQLStr(value) + "'";
        aList.add(insertStr);
    }
    public void insertValue(String name, boolean value, ArrayList<String> aList) {
        String insertStr = "'" + (value?1:0) + "'";
        aList.add(insertStr);
    }
    public void insertIntoTable(String tab, ArrayList<String> aList) {
        insertTableName = tab;
        String insertValueStr = aList.get(0);

        for(int i = 1; i < aList.size(); i++) {
            insertValueStr += " , " + aList.get(i);
        }

        String query = "insert into " + insertTableName + " values ( " + insertValueStr + " )";
        if(DEBUG_SQL_CMD) System.out.println(query);
        try {
//            if(!isConnected()) connectToServer();
            getConnectionStatement();
            mStatement.executeUpdate(query);
            closeConnectionStatement();
//            mStatement.close();
//            mStatement = null;
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
            if(DEBUG_VERBOSE) System.out.println("insert success");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insert failed");
            System.exit(1);
        }
    }
    public void insertIntoTable(String tab) {
        insertTableName = tab;

        String insertValueStr = insertData.get(0);

        for(int i = 1; i < insertData.size(); i++) {
            insertValueStr += " , " + insertData.get(i);
        }

        String query = "insert into " + insertTableName + " values ( " + insertValueStr + " )";
        if(DEBUG_SQL_CMD) System.out.println(query);
        try {
//            connectToServer();
            getConnectionStatement();
            mStatement.executeUpdate(query);
            closeConnectionStatement();
//            mStatement.close();
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
            if(DEBUG_VERBOSE) System.out.println("insert success");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insert failed");
            System.exit(1);
        }
    }
    public Date convertMySQLDateToJava(java.sql.Date sqlDate) {
        String dateStr = sqlDate.toString();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateStr);
        } catch(Exception e) {
            return null;
        }
    }
    float mBuyinReturnRatio = -1;
    float mBuyinPrise = -1;
    java.sql.Date mLastBuyDate = null;
    float getReturnRatio(String stockid, Date date) {
        String tableName = dailyInfoTable;
//        String dateStr = convertJavaDateToMySQLStr(date);
//        String checkQuery = "select * from " + tableName + " where stockid = " + stockid + " and date = '" + dateStr + "';";
        float retr = 0;
        try {
//            connectToServer();
            initSelectTable();
            addSelParmValue("stockid", stockid);
            addSelParmValue("date", date);
            performSelectTable(tableName);
//            mResultSet = mStatement.executeQuery(checkQuery);
            if (mResultSet.next()) {
                mBuyinReturnRatio = retr = mResultSet.getFloat("returnratio");
                mBuyinPrise = mResultSet.getFloat("dealprise");
                mLastBuyDate = mResultSet.getDate("date");
            }
            finishSelectQuery();
//            mResultSet.close();
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
        } catch(Exception e) {
            e.printStackTrace();
            retr = -1;
        }
        return retr;
    }
    public float getLastReturnRatio(String stockid) {
        return getLastReturnRatio(stockid, false);
    }
    public float getLastReturnRatio(String stockid, boolean allowZeroRatio) {
        String tableName = dailyInfoTable;
//        String checkQuery = "select * from " + tableName + " where stockid = " + stockid + " order by date desc;";
        float retr = 0;
        int i = 0;
        try {
//            connectToServer();
//            mResultSet = mStatement.executeQuery(checkQuery);
            initSelectTable();
            addSelParmValue("stockid", stockid);
            addSelOrder("date", false);
            performSelectTable(tableName);
            while(i < 3 && mResultSet.next()) {
                mBuyinReturnRatio = retr = mResultSet.getFloat("returnratio");
                mBuyinPrise = mResultSet.getFloat("dealprise");
                mLastBuyDate = mResultSet.getDate("date");
                if(retr != 0 || allowZeroRatio) {
                    break;
                }
                i++;
            }
            finishSelectQuery();
//            mResultSet.close();
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
        } catch(Exception e) {
            e.printStackTrace();
            retr = -1;
        }
        return retr;
    }
    public float getRoe(String stockid) {
        String tableName = annuallyInfoTable;
        initSelectTable();
        addSelCol("roe");
        addSelParmValue("stockid", stockid);
        addSelParmValue("year", String.valueOf(getThisYear()));
        ResultSet rSet = performSelectTable(tableName);
        float roe = -100;
        try {
            if(rSet.next()) {
                roe = rSet.getFloat("roe");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roe;
    }
    public boolean buyin(String stockid, int amount, String dateStr, float prise, int sind, String sdec, float rr) {
        if(!checkIdExistInTable("buyin_table", stockid, "buyday", dateStr)) {
            ArrayList<String> aList = initInsertTable();
            insertValue("stockid", stockid, aList);
            insertValue("buyday", dateStr, aList);
            insertNullValue("sellday", aList);
            insertValue("amount", amount, aList);
            insertValue("prise", prise, aList);
            insertValue("strategyindex", sind, aList);
            insertValue("strategydescription", sdec, aList);
            insertValue("returnratio", rr, aList);
            insertValue("sellprize", 0, aList);
            insertIntoTable("buyin_table", aList);
        } else {
            if(DEBUG_VERBOSE) System.out.println("stockid:" + stockid + " date:" + dateStr + " already bought");
        }
        return true;
    }

    ArrayList<String> delParmData = null;

    void initDeleteTable() {
        delParmData = new ArrayList<>();
    }
    void addDeleteParam(String col, String val) {
        delParmData.add(col + " = '" + val + "'");
    }
    void addDeleteParam(String col, int val) {
        delParmData.add(col + " = " + val);
    }
    void addDeleteParam(String col, boolean val) {
        delParmData.add(col + " = '" + (val?1:0) + "'");
    }
    void addDeleteParam(String col, Date val) {
        delParmData.add(col + " = '" + val + "'");
    }
    void addDeleteParam(String col, float val) {
        delParmData.add(" abs ( " + col + " - " + val + ") < 0.001");
    }
    void addDeleteParam(String col) {
        delParmData.add(col + " is null");
    }
    public void performDeleteTable(String tab) {
        if(delParmData == null || delParmData.size() <= 0) return;
        String deleteStr = "DELETE from " + tab + " where ";
        deleteStr += " " + delParmData.get(0);
        for (int i = 1; i < delParmData.size(); i++) {
            deleteStr += " and " + delParmData.get(i);
        }
        if(DEBUG_SQL_CMD)
            System.out.println(deleteStr);
//        connectToServer();
        try {
            PreparedStatement stmt = mConnection.prepareStatement(deleteStr);
            stmt.executeUpdate();
            stmt.close();
            if(DEBUG_VERBOSE) System.out.println("update " + tab + " success");
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final static int SELLOUT_AMOUNT_ALL = 100000;
    public void sellout(String stockid, int amount, String datestr, float sellprize) {
        String tableName = buyinTable;
        if(checkIdExistInTable(tableName, stockid, "sellday", (String)null)) {
            initUpdateTable();
            addUpdateCol("sellday", datestr);
            addUpdateCol("sellprize", sellprize);
            addUpdateParam("stockid", stockid);
            performUpdateTable(tableName);
        }
        tableName = buyinTableAnalysis;
        if(checkIdExistInTable(tableName, stockid)) {
            initDeleteTable();
            addDeleteParam("stockid", stockid);
            performDeleteTable(tableName);
        }
    }

    public boolean buyin(String stockid, String tableName, int amount, String dateStr, float prise, int sind, String sdec, float rr, int buyin_method) {
        boolean idExist = checkIdExistInTable(tableName, stockid);
        boolean idDateExist = checkIdExistInTable(tableName, stockid, "buyday", dateStr);
        //If we do daily regular buyin with no date&stockid record, or we get no such stockid bought, bought it
        // We should apply BUYIN_REGULAR_DAILY on history database
        if(((buyin_method == BUYIN_REGULAR_DAILY) && (!idDateExist)) ||
                (!idExist)) {
            ArrayList<String> aList = initInsertTable();
            insertValue("stockid", stockid, aList);
            insertValue("buyday", dateStr, aList);
            insertNullValue("sellday", aList);
            insertValue("amount", amount, aList);
            insertValue("prise", prise, aList);
            insertValue("strategyindex", sind, aList);
            insertValue("strategydescription", sdec, aList);
            insertValue("returnratio", rr, aList);
            insertValue("sellprize", 0, aList);
            insertIntoTable(tableName, aList);
        } else if(buyin_method == BUYIN_ADD_AVG && idExist){
            // We should apply BUYIN_ADD_AVG on buyin for brief database
            if(DEBUG_VERBOSE) System.out.println("stockid:" + stockid + " date:" + dateStr + " already bought, buyin add and count average");
            int bought_amount = 0;
            float bought_prise = 0;
            initSelectTable();
            addSelCol("amount");
            addSelCol("prise");
            addSelParmValue("stockid", stockid);
            addSelOrder("buyday", false);
            ResultSet mResSet = performSelectTable(tableName);
            try {
                if (mResSet.next()) {
                    bought_prise = mResSet.getFloat("prise");
                    bought_amount = mResSet.getInt("amount");
                }
                mResSet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finishSelectQuery();
            prise = (prise * amount + bought_amount * bought_prise) / (amount + bought_amount);
            amount = amount + bought_amount;
            initUpdateTable();
            addUpdateCol("buyday", dateStr);
            addUpdateCol("sellday");
            addUpdateCol("amount", amount);
            addUpdateCol("prise", prise);
            addUpdateCol("strategyindex", sind);
            addUpdateCol("strategydescription", sdec);
            addUpdateCol("returnratio", rr);
            addUpdateParam("stockid", stockid);
            performUpdateTable(tableName);
        } else {
            if(DEBUG_VERBOSE) System.out.println("stockid:" + stockid + " date:" + dateStr + " already bought, ignore");
        }
        return true;
    }
    /*
    * strategy 1: returnratio > 10 & roe > 0
    * stratego 2: returnratio > 8 & roe > 1
    * */
    float ratio_threshold = 10;
    class StrategyParameter {
        public float ratio_threshold;
        public float roe;
        public boolean noNeedAnnounced = false;
        public int sid;
        public String strategyDesc = null;
        public StrategyParameter(int asid, float rr, float re, boolean an) {
            ratio_threshold = rr;
            roe = re;
            noNeedAnnounced = an;
            sid = asid;
        }
    }
    /* Score formula
    * roe > 2 : 2
    * roe > 0 : 1
    * roe > -3 : 0
    * otherwise : -1
    *
    * return ratio :
    * 10 : 2
    * 8 : 1
    * 4 : 0
    * otherwise : -1
    *
    * announced : 1
    * otherwise : 0
    *
    * season :
    * all positive : 2
    * 3 seasons : 1
    * 2 seasons : 0
    * otherwise : -1
    *
    * seasons total > last year : 2
    * otherwise : -1
    * */
    StrategyParameter param1 = new StrategyParameter(1,10,-3, false);
    StrategyParameter param2 = new StrategyParameter(2,8,1, true);
    StrategyParameter sellparam1 = new StrategyParameter(1,6,-3, false);
    StrategyParameter sellparam2 = new StrategyParameter(2,5,1, true);
    ArrayList<StrategyParameter> mSelloutStrategy = new ArrayList<>();
    ArrayList<StrategyParameter> mBuyinStrategy = new ArrayList<>();
    ArrayList<StrategyParameter> mExtraBuyinStrategy = new ArrayList<>();
    public void applyStrategy(String stockid, int strategyIndex) {
        String dateStr = convertJavaDateToMySQLStr(new Date());
        float retr = getLastReturnRatio(stockid);
        float roe = getRoe(stockid);
        boolean announced = checkThisYearShareAnnounced(stockid);
        if(retr > param1.ratio_threshold && roe > param1.roe && (announced || param1.noNeedAnnounced)) {
            if(DEBUG_VERBOSE) System.out.println("stockid:" + stockid + " rr:" + retr + " buyin, threshold:" + ratio_threshold);
            buyin(stockid, 1,dateStr,mBuyinPrise,1,"ratio > " + param1.ratio_threshold + " roe > " + param1.roe + " share:" + param1.noNeedAnnounced, mBuyinReturnRatio);
        } else if(retr > param2.ratio_threshold && roe >  param2.roe && (announced || param2.noNeedAnnounced)) {
            if(DEBUG_VERBOSE) System.out.println("stockid:" + stockid + " rr:" + retr + " buyin, threshold:" + ratio_threshold);
            buyin(stockid, 1,dateStr,mBuyinPrise,2,"ratio > " + param2.ratio_threshold + " roe > " + param2.roe + " share:" + param2.noNeedAnnounced, mBuyinReturnRatio);
        } else {
            if(DEBUG_VERBOSE) System.out.println("stockid:" + stockid + " not buyin");
        }
        //System.out.println("stock id:" + stockid + " rr:" + retr);
    }

    public void getStrategy(ArrayList<StrategyParameter> buyStrategy,
                            ArrayList<StrategyParameter> sellStrategy,
                            ArrayList<StrategyParameter> extraByuinStrategy) {
        String tableName = strategyTable;
        float rr, prz, roe;
        int idx;
        String stype;
        String sDesc;
        boolean needAnn = false;
        initSelectTable();
        addSelOrder("strategyid", true);
        ResultSet rSet = performSelectTable(tableName);
        try {
            while (rSet.next()) {
                rr = rSet.getFloat("return_ratio");
                prz = rSet.getFloat("prize");
                stype = rSet.getString("strategytype");
                needAnn = rSet.getBoolean("needAnnounced");
                idx = rSet.getInt("strategyid");
                sDesc = rSet.getString("strategy_description");
                roe = rSet.getFloat("roe");
                if(stype.equals("buy") && buyStrategy != null) {
                    StrategyParameter sp = new StrategyParameter(idx, rr, prz, needAnn);
                    sp.strategyDesc = sDesc;
                    sp.roe = roe;
                    buyStrategy.add(sp);
                }
                else if(stype.equals("sell") && sellStrategy != null) {
                    StrategyParameter sp = new StrategyParameter(idx, rr, prz, needAnn);
                    sp.strategyDesc = sDesc;
                    sp.roe = roe;
                    sellStrategy.add(sp);
                }
                else if(stype.equals("cbuy") && extraByuinStrategy != null) {
                    StrategyParameter sp = new StrategyParameter(idx, rr, prz, needAnn);
                    sp.strategyDesc = sDesc;
                    sp.roe = roe;
                    extraByuinStrategy.add(sp);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public int matchStrategy(String stockid, performanceEntry pe, boolean ann, ArrayList<StrategyParameter> strategy, boolean isBuy) {
        return matchStrategy(stockid,pe,ann,strategy,isBuy,-1);
    }

    public int matchStrategy(String stockid, performanceEntry pe, boolean ann, ArrayList<StrategyParameter> strategy, boolean isBuy, int idx) {
        StrategyParameter sp;
        if(DEBUG_VERBOSE)
            System.out.println("Check for performance rr:" + pe.returnratio + " roe:" + pe.roe);
        for(int i = 0;i < strategy.size();i++) {
            sp = strategy.get(i);
            if(idx >= 0 && idx != sp.sid)
                continue;
            if(isBuy) {
                if((pe.returnratio > sp.ratio_threshold) && (pe.roe > sp.roe) && (ann || sp.noNeedAnnounced)) {
                    return sp.sid;
                }
            } else {
                if((pe.returnratio < sp.ratio_threshold) || (pe.roe < sp.roe)) {
                    return sp.sid;
                }
            }
        }
        return -1;
    }

    public void applyBuyinSelloutStrategy(String stockid, boolean applyForUpdate) {
        if(mBuyinStrategy.size() == 0 || mSelloutStrategy.size() == 0 || mExtraBuyinStrategy.size() == 0)
            getStrategy(mBuyinStrategy, mSelloutStrategy, mExtraBuyinStrategy);
        performanceEntry pe = getPerformance(stockid, false, false);
        boolean ann = checkThisYearShareAnnounced(stockid);
        int buycount = 0;
        int buyinStrInd;
        int i = matchStrategy(stockid, pe, ann, mSelloutStrategy, false, pe.lastStrategyId);
        if(i >= 0) {
            System.out.println("Sellout");

            if(applyForUpdate) sellout(stockid,1000000, convertJavaDateToMySQLStr(new Date()),mBuyinPrise);
        }
        buyinStrInd = matchStrategy(stockid, pe, ann, mBuyinStrategy,true);
        if(buyinStrInd >= 0) {
            System.out.println("Buyin, strategy:" + buyinStrInd);
            buycount ++;
        }
        i = matchStrategy(stockid, pe, ann, mExtraBuyinStrategy,true, i);
        if(i >= 0) {
            System.out.println("Extra Buyin, strategy:" + i);
            buycount ++;
        }
        if(buycount > 0)
            if(applyForUpdate) buyin(stockid, buycount,convertJavaDateToMySQLStr(new Date()),pe.lastprise, buyinStrInd,mBuyinStrategy.get(buyinStrInd - 1).strategyDesc,pe.returnratio);
    }

    public String getBuyinSelloutStrategy(String stockid, performanceEntry pe) {
        if(mBuyinStrategy.size() == 0 || mSelloutStrategy.size() == 0 || mExtraBuyinStrategy.size() == 0)
            getStrategy(mBuyinStrategy, mSelloutStrategy, mExtraBuyinStrategy);
        if(pe == null) {
            pe = getPerformance(stockid, false, false);
        }
        boolean ann = checkThisYearShareAnnounced(stockid);
        int buycount = 0;
        int i = matchStrategy(stockid, pe, ann, mSelloutStrategy, false, pe.lastStrategyId);
        if(i >= 0) {
            System.out.println("Sellout");
            buycount = -1;
        }
        i = matchStrategy(stockid, pe, ann, mBuyinStrategy,true);
        if(i >= 0) {
            System.out.println("Buyin, strategy:" + i);
            buycount ++;
        }
        i = matchStrategy(stockid, pe, ann, mExtraBuyinStrategy,true, i);
        if(i >= 0) {
            System.out.println("Extra Buyin, strategy:" + i);
            buycount ++;
        }
        if(buycount < 0) {
            return "sellout";
        } else if(buycount > 0) {
            return "buyin";
        }
        return "hold";
    }

    public boolean insertSeasonEarningTable(String stockid, int annualYear,
                                            ArrayList<EarningGrammarDataListener.seasonInfo> seasonPostTaxEarning,
                                            ArrayList<EarningGrammarDataListener.seasonInfo> seasonPreTaxEarning) {
        if(seasonPostTaxEarning.size() != seasonPreTaxEarning.size()) {
            if(DEBUG_VERBOSE) System.out.println("Season post tax record count:" + seasonPostTaxEarning.size() +
            " is not equal to season pre tax record count:" + seasonPreTaxEarning.size());
            return false;
        }
        String tableName = seasonEarningInfoTable;
        for(int i = 0;i < seasonPostTaxEarning.size(); i++) {
            EarningGrammarDataListener.seasonInfo preTaxInfo = seasonPreTaxEarning.get(i);
            EarningGrammarDataListener.seasonInfo postTaxInfo = seasonPostTaxEarning.get(i);
            if((preTaxInfo.earning == 0) && (preTaxInfo.yearIncRatio == 0)) {
                System.out.println("Year:" + annualYear + " season:" + preTaxInfo.season + " record invalid");
                continue;
            }
            if(checkIdYearSeasonExistInTable(tableName, stockid, annualYear, preTaxInfo.season)) {
                if(DEBUG_VERBOSE) System.out.println("Id:" + stockid +
                        " year:" + annualYear +
                        " season:" + preTaxInfo.season +
                        " exist in table:" + tableName + " , try update");
                initUpdateTable();
                addUpdateCol("post_tax_surplus", postTaxInfo.earning);
                addUpdateCol("post_tax_increase_ratio", postTaxInfo.yearIncRatio);
                addUpdateCol("pre_tax_surplus", preTaxInfo.earning);
                addUpdateCol("pre_tax_increase_ratio", preTaxInfo.yearIncRatio);
                addUpdateParam("stockid", stockid);
                addUpdateParam("year", annualYear);
                addUpdateParam("season", preTaxInfo.season);
                performUpdateTable(tableName);
//                try {
//                    connectToServer();
//                    PreparedStatement stmt = mConnection.prepareStatement("UPDATE " + tableName +
//                            " SET post_tax_surplus = ?, post_tax_increase_ratio = ? , pre_tax_surplus = ? , pre_tax_increase_ratio = ? " +
//                            "WHERE stockid = ? AND year = ? AND season = ?");
//                    stmt.setInt(1, postTaxInfo.earning);
//                    stmt.setFloat(2, postTaxInfo.yearIncRatio);
//                    stmt.setInt(3, preTaxInfo.earning);
//                    stmt.setFloat(4, preTaxInfo.yearIncRatio);
//                    stmt.setString(5, stockid);
//                    stmt.setInt(6, annualYear);
//                    stmt.setInt(7, preTaxInfo.season);
//                    stmt.executeUpdate();
//                    stmt.close();
//                    if(DEBUG_VERBOSE) System.out.println("update " + tableName + " success");
//                    if(mConnection != null) {
//                        mConnection.close();
//                        mConnection = null;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            } else {
                ArrayList<String> aList = initInsertTable();
                insertValue("stockid", stockid, aList);
                insertValue("year", annualYear, aList);
                insertValue("season", preTaxInfo.season, aList);
                insertValue("post_tax_surplus", postTaxInfo.earning, aList);
                insertValue("post_tax_increase_ratio", postTaxInfo.yearIncRatio, aList);
                insertValue("pre_tax_surplus", preTaxInfo.earning, aList);
                insertValue("pre_tax_increase_ratio", preTaxInfo.yearIncRatio, aList);
                insertIntoTable(tableName, aList);
            }
        }

        return true;
    }

    public boolean insertSeasonShareTable(String stockid,
                                          ArrayList<StackBasicInformation.seasonRecord> recList) {
        String tableName = seasonEarningShareTable;

        for(int i = 0;i < recList.size(); i++) {
            StackBasicInformation.seasonRecord rec = recList.get(i);
            if(checkIdYearSeasonExistInTable(tableName, stockid, rec.year, rec.season)) {
                if(DEBUG_VERBOSE) System.out.println("Id:" + stockid + " year:" + rec.year + " season:" + rec.season + " exist in table:" + tableName + ", try update");
                initUpdateTable();
                addUpdateCol("earn_per_share", rec.share);
                addUpdateParam("stockid", stockid);
                addUpdateParam("year", rec.year);
                addUpdateParam("season", rec.season);
                performUpdateTable(tableName);
//                connectToServer();
//                try {
//                    PreparedStatement stmt = mConnection.prepareStatement("UPDATE " + tableName +
//                            " SET earn_per_share = ? " +
//                            "WHERE stockid = ? AND year = ? AND season = ?");
//                    stmt.setFloat(1, rec.share);
//                    stmt.setString(2, stockid);
//                    stmt.setInt(3, rec.year);
//                    stmt.setInt(4, rec.season);
//                    stmt.executeUpdate();
//                    stmt.close();
//                    if(DEBUG_VERBOSE) System.out.println("update " + tableName + " success");
//                    if(mConnection != null) {
//                        mConnection.close();
//                        mConnection = null;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            } else {
                ArrayList<String> aList = initInsertTable();
                insertValue("stockid", stockid, aList);
                insertValue("year", rec.year, aList);
                insertValue("season", rec.season, aList);
                insertValue("earn_per_share", rec.share, aList);
                insertIntoTable(tableName, aList);
            }
        }

        return true;
    }
    public boolean insertAnnualShareTable(String stockid,
                                          ArrayList<StackBasicInformation.seasonRecord> recList) {
        String tableName = annualEarningShareTable;
        for(int i = 0;i < recList.size(); i++) {
            StackBasicInformation.seasonRecord rec = recList.get(i);
            if(checkIdExistInTable(tableName, stockid, rec.year)) {
                if(DEBUG_VERBOSE) System.out.println("Id:" + stockid + " year:" + rec.year + " exist in table:" + tableName + ", try update");
                initUpdateTable();
                addUpdateCol("earn_per_share", rec.share);
                addUpdateParam("stockid", stockid);
                addUpdateParam("year", rec.year);
                performUpdateTable(tableName);
//                connectToServer();
//                try {
//                    PreparedStatement stmt = mConnection.prepareStatement("UPDATE " + tableName +
//                            " SET earn_per_share = ? " +
//                            "WHERE stockid = ? AND year = ?");
//                    stmt.setFloat(1, rec.share);
//                    stmt.setString(2, stockid);
//                    stmt.setInt(3, rec.year);
//                    stmt.executeUpdate();
//                    stmt.close();
//                    if(DEBUG_VERBOSE) System.out.println("update " + tableName + " success");
//                    if(mConnection != null) {
//                        mConnection.close();
//                        mConnection = null;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            } else {
                ArrayList<String> aList = initInsertTable();
                insertValue("stockid", stockid, aList);
                insertValue("year", rec.year, aList);
                insertValue("earn_per_share", rec.share, aList);
                insertIntoTable(tableName, aList);
            }
        }

        return true;
    }

    public boolean insertMonthlyEarningTable(String stockid, int annualYear,
                                            ArrayList<EarningGrammarDataListener.seasonInfo> monthlyEarning) {
        if(monthlyEarning.size() == 0) {
            if(DEBUG_VERBOSE) System.out.println("Monthly earn list is empty:" + monthlyEarning.size());
            return false;
        }
        String tableName = monthlyEarnTable;
        for(int i = 0;i < monthlyEarning.size(); i++) {
            EarningGrammarDataListener.seasonInfo monInfo = monthlyEarning.get(i);
            if((monInfo.earning == 0) && (monInfo.yearIncRatio == 0)) {
                System.out.println("Year:" + annualYear + " season:" + monInfo.season + " record invalid");
                continue;
            }
            if(checkIdYearMonthExistInTable(tableName, stockid, annualYear, monInfo.season)) {
                if(DEBUG_VERBOSE) System.out.println("Id:" + stockid +
                        " year:" + annualYear +
                        " season:" + monInfo.season +
                        " exist in table:" + tableName + " , try update");
                initUpdateTable();
                addUpdateCol("revenue",monInfo.season);
                addUpdateCol("inc_ratio",monInfo.earning);
                addUpdateCol("accumulate_revenue",monInfo.yearIncRatio);
                addUpdateCol("accumulate_inc_ratio",monInfo.accuYearIncRatio);
                addUpdateParam("stockid", stockid);
                addUpdateParam("year", annualYear);
                addUpdateParam("month",monInfo.season);
                performUpdateTable(tableName);
            } else {
                ArrayList<String> aList = initInsertTable();
                insertValue("stockid", stockid, aList);
                insertValue("year", annualYear, aList);
                insertValue("month", monInfo.season, aList);
                insertValue("revenue", monInfo.earning, aList);
                insertValue("inc_ratio", monInfo.yearIncRatio, aList);
                insertValue("accumulate_revenue", monInfo.accuEarning, aList);
                insertValue("accumulate_inc_ratio", monInfo.accuYearIncRatio, aList);
                insertIntoTable(tableName, aList);
            }
        }

        return true;
    }

    public float mRetrievePrise = 0;
    public Date mRetrieveDate = null;
    public float getPrise(String stockid, Date date) {
        String tableName = dailyInfoTable;
//        String checkQuery = "select dealprise,date from " +
//                tableName +
//                " where stockid = '" + stockid + "'";
//        if(date == null) {
//            checkQuery = checkQuery + " order by date desc";
//        } else {
//            checkQuery = checkQuery + " and date = '" + convertJavaDateToMySQLStr(date) + "'";
//        }
        mRetrievePrise = 0;
        mRetrieveDate = null;
        initSelectTable();
        addSelCol("dealprise");
        addSelCol("date");
        addSelParmValue("stockid", stockid);
        if(date == null) {
            addSelOrder("date", false);
        } else {
            addSelParmValue("date", date, "<=");
            addSelOrder("date", false);
        }
        performSelectTable(tableName);
        try {
            if (mResultSet.next()) {
                mRetrievePrise = mResultSet.getFloat("dealprise");
                java.sql.Date sqlDate = mResultSet.getDate("date");
                mRetrieveDate = convertMySQLDateToJava(sqlDate);
                if (DEBUG_VERBOSE)
                    System.out.println("mRetrievePrise:" + mRetrievePrise + " mRetrieveDate:" + mRetrieveDate);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        finishSelectQuery();
//        try {
//            connectToServer();
//            mResultSet = mStatement.executeQuery(checkQuery);
//            if(mResultSet.next()) {
//                mRetrievePrise = mResultSet.getFloat("dealprise");
//                java.sql.Date sqlDate = mResultSet.getDate("date");
//                mRetrieveDate = convertMySQLDateToJava(sqlDate);
//                if(DEBUG_VERBOSE) System.out.println("mRetrievePrise:" + mRetrievePrise + " mRetrieveDate:" + mRetrieveDate);
//            }
//            mResultSet.close();
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return mRetrievePrise;
    }

    public class divEntry {
        float div;
        int year;
    }
    public class performanceEntry {
        public float avgprise = 0;
        public float lastprise = 0;
        public float perform = 0;
        public int count = 0;
        public float returnratio = 0;
        /* Last buy date in buyin table */
        public java.sql.Date buyinDate = null;
        /* Today */
        public java.sql.Date checkDate = null;
        /* Last Sell day in buyin table */
        public java.sql.Date lastSellDate = null;
        public float min = 0;
        public float max = 0;
        public float roe = 0;
        public float roa = 0;
        public float stockdiv = 0;
        public float nv = 0;
        public float season[] = new float[4];
        public float year[] = new float[4];
        public ArrayList<divEntry> annualDiv = new ArrayList<>();
        public ArrayList<Float> div = new ArrayList<>();
        public int lastStrategyId = -1;
        /* If we have unsold item, we need do "buyin strategy estimation analysis" */
        public boolean hasUnsold = false;
        /* If we have sold item, we need do "buyin performance analysis" */
        public boolean hasSold = false;
    }

    ArrayList<String> colData = null;
    ArrayList<String> parmData = null;
    ArrayList<String> orderData = null;
    ArrayList<String> selGroupData = null;

    public void initSelectTable() {
        colData = new ArrayList<>();
        parmData = new ArrayList<>();
        orderData = new ArrayList<>();
        selGroupData = new ArrayList<>();
    }
    public void addSelCol(String name) {
        colData.add(name);
    }
    public void addSelGroup(String name) {
        selGroupData.add(name);
    }
    public void addSelOrder(String name, boolean inc) {
        if(inc) {
            orderData.add(name + " asc");
        } else {
            orderData.add(name + " desc");
        }
    }
    public void addSelParmValue(String name) {
        String insertStr = name + " is null ";
        parmData.add(insertStr);
    }
    public void addSelParmValue(String name, String value) {
        String insertStr = name + " = '" + value + "'";
        parmData.add(insertStr);
    }
    public void addSelParmValue(String name, int value) {
        String insertStr = name + " = " + value;
        parmData.add(insertStr);
    }
    public void addSelParmValue(String name, float value) {
        String insertStr = name + " = " + value;
        parmData.add(insertStr);
    }
    public void addSelParmValue(String name, Date value) {
        String insertStr = name + " = '" + convertJavaDateToMySQLStr(value) + "'";
        parmData.add(insertStr);
    }
    public void addSelParmValue(String name, Date value, String op) {
        String insertStr;
        if(op != null && (op.equals(">") || op.equals(">=") || op.equals("<") || op.equals("<="))) {
            insertStr = name + " " + op + " '" + convertJavaDateToMySQLStr(value) + "'";
        } else {
            insertStr = name + " = '" + convertJavaDateToMySQLStr(value) + "'";
        }
        parmData.add(insertStr);
    }
    public void addSelParmValue(String name, boolean value) {
        String insertStr = name + " = '" + (value?1:0) + "'";
        parmData.add(insertStr);
    }
    public ResultSet performSelectTable(String tab) {
        return performSelectTable(tab, true);
    }
    public ResultSet performSelectTable(String tab, boolean printTraceAndExit) {
        String selColStr = "select ";
        if(colData.size() == 0) {
            selColStr += " * ";
        } else {
            selColStr += " " + colData.get(0);
            for (int i = 1; i < colData.size(); i++) {
                selColStr += " , " + colData.get(i);
            }
        }
        selColStr += " from " + tab;
        if(parmData.size() != 0) {
            selColStr += " where " + parmData.get(0);
            for (int i = 1; i < parmData.size(); i++) {
                selColStr += " AND " + parmData.get(i);
            }
        }
        if(orderData.size() != 0) {
            selColStr += " order by " + orderData.get(0);
            for (int i = 1; i < orderData.size(); i++) {
                selColStr += " , " + orderData.get(i);
            }
        }
        if(selGroupData.size() > 0) {
            selColStr += " group by " + selGroupData.get(0);
            for (int i = 1; i < selGroupData.size(); i++) {
                selColStr += " , " + selGroupData.get(i);
            }
        }
        if(DEBUG_SQL_CMD) System.out.println(selColStr);
        try {
//            connectToServer();
            getConnectionStatement();
            mResultSet = mStatement.executeQuery(selColStr);
            return mResultSet;
        } catch (Exception e) {
            if(printTraceAndExit) {
                e.printStackTrace();
                System.out.println("insert failed");
                System.exit(1);
            }
        }
        return null;
    }

    public void finishSelectQuery() {
        try {
            if(mResultSet != null) {
                mResultSet.close();
                mResultSet = null;
            }
            if(mStatement != null) {
                mStatement.close();
                mStatement = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getInfoForAnalysis(String stockid, performanceEntry pe) {
        int year;
        String tableName = annuallyInfoTable;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        year = cal.get(Calendar.YEAR);
        initSelectTable();
        addSelCol("roe");
        addSelCol("roa");
        addSelCol("book_value_per_share");
        addSelCol("stock_dividend");
        addSelParmValue("stockid",stockid);
        addSelParmValue("year", year);
        ResultSet resSet = performSelectTable(tableName);
        try {
            while (resSet.next()) {
                pe.roe = resSet.getFloat("roe");
                pe.roa = resSet.getFloat("roa");
                pe.nv = resSet.getFloat("book_value_per_share");
                pe.stockdiv = resSet.getFloat("stock_dividend");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        finishSelectQuery();
    }

    void getSeasonEarnForAnalysis(String stockid, performanceEntry pe) {
        String tableName = seasonEarningShareTable;
        initSelectTable();
        addSelCol("earn_per_share");
        addSelParmValue("stockid",stockid);
        addSelOrder("year", false);
        addSelOrder("season", false);
        ResultSet resSet = performSelectTable(tableName);
        try {
            int i = 0;
            while (resSet.next()) {
                pe.season[i] = resSet.getFloat("earn_per_share");
                i ++;
                if(i == 4) {
                    break;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        finishSelectQuery();

    }
    void getAnnualEarnForAnalysis(String stockid, performanceEntry pe) {
        String tableName = annualEarningShareTable;
        initSelectTable();
        addSelCol("earn_per_share");
        addSelParmValue("stockid",stockid);
        addSelOrder("year", false);
        ResultSet resSet = performSelectTable(tableName);
        try {
            int i = 0;
            while (resSet.next()) {
                pe.year[i] = resSet.getFloat("earn_per_share");
                i ++;
                if(i == 4) {
                    break;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        finishSelectQuery();

    }
    void getAnnualDividendForAnalysis(String stockid, performanceEntry pe) {
        String tableName = annualDividendTable;
        initSelectTable();
        addSelCol("total_dividend");
        addSelCol("year");
        addSelParmValue("stockid",stockid);
        addSelOrder("year", false);

        ResultSet resSet = performSelectTable(tableName);

        try {
            int i = 0;
            while (resSet.next()) {
                divEntry dEnt = new divEntry();
                dEnt.div = resSet.getFloat("total_dividend");
                dEnt.year = resSet.getInt("year");
                pe.div.add(resSet.getFloat("total_dividend"));
                pe.annualDiv.add(dEnt);
                i ++;
//                if(i == 4) {
//                    break;
//                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        finishSelectQuery();

    }

    public performanceEntry getPerformance(String stockid, boolean updateDB, boolean checkOverAll) {
        String tableName = buyinTable;
//        String checkQuery = "select * from " + tableName + " where stockid = '" + stockid + "' order by buyday desc";
        float prz = getPrise(stockid, null);
        float min = 1000000, max = 0;
        float dealprz, totalPrise = 0, rr = 0;
        int count = 0;
        int sid = -1;
        float total = 0;
        java.sql.Date buyDate = null, lastBuyDay = null, sellDay, lastSellDay = null;
        performanceEntry performEnt = new performanceEntry();
        rr = getLastReturnRatio(stockid);/*mResultSet.getFloat("returnratio");*/
        try {
//            connectToServer();
//            mResultSet = mStatement.executeQuery(checkQuery);
            initSelectTable();
            addSelParmValue("stockid", stockid);
            addSelOrder("buyday", false);
            performSelectTable(tableName);
            while(mResultSet.next()) {
                dealprz = mResultSet.getFloat("prise");
                if(sid < 0) sid = mResultSet.getInt("strategyindex");
                buyDate = mResultSet.getDate("buyday");
                if((lastBuyDay == null) || lastBuyDay.before(buyDate)) {
                    lastBuyDay = buyDate;
                }
                sellDay = mResultSet.getDate("sellday");
                if(sellDay != null) {
                    performEnt.hasSold = true;
                }
                else {
                    performEnt.hasUnsold = true;
                }
                if((lastSellDay == null) || lastBuyDay.before(sellDay)) {
                    lastSellDay = sellDay;
                }
                if(dealprz > max) max = dealprz;
                if(dealprz < min) min = dealprz;
                total += (prz - dealprz);
                count ++;
                totalPrise += dealprz;
            }
            finishSelectQuery();
//            mResultSet.close();
            if(count > 0) {
                performEnt.count = count;
                performEnt.perform = total / totalPrise;
                performEnt.avgprise = totalPrise / count;
                performEnt.buyinDate = lastBuyDay;
                performEnt.lastSellDate = lastSellDay;
                performEnt.checkDate = convertJavaDateToMySQL(new Date());
                performEnt.returnratio = rr;
                performEnt.min = min;
                performEnt.max = max;
                performEnt.lastprise = prz;
                performEnt.lastStrategyId = sid;
            } else if(checkOverAll){
                /* Since we didn't buy before, we only check other annual info */
                performEnt.count = 0;
                performEnt.perform = 0;
                performEnt.avgprise = mBuyinPrise;
                performEnt.buyinDate = null;
                performEnt.lastSellDate = null;
                performEnt.checkDate = convertJavaDateToMySQL(new Date());
                performEnt.returnratio = rr;
                performEnt.min = mBuyinPrise;
                performEnt.max = mBuyinPrise;
                performEnt.lastprise = mBuyinPrise;
                performEnt.lastStrategyId = sid;
            }
            if(DEBUG_VERBOSE) System.out.println("performance, total:" + total);
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(checkOverAll || (performEnt.count > 0)) {
            getInfoForAnalysis(stockid, performEnt);
            getSeasonEarnForAnalysis(stockid, performEnt);
            getAnnualEarnForAnalysis(stockid, performEnt);
            getAnnualDividendForAnalysis(stockid, performEnt);
            if (updateDB) {
                updatePerformance(stockid, performEnt);
                if (performEnt.hasUnsold) {
                    System.out.println("Handle unsold with buyin analysis, id:" + stockid);
                    updateBuyinAnalysis(stockid, performEnt);
                    updateBuyinAnalysis(buyinTableAnalysisScore, stockid, performEnt, true);
                } else {
                    System.out.println("ToDo : Handle sold with performance analysis, id:" + stockid);
                }
            }
        }
        return performEnt;
    }

//    public performanceEntry getPerformance(String stockid, boolean updateDB) {
//        String tableName = buyinTable;
//        String checkQuery = "select * from " + tableName +
//                " where stockid = '" + stockid + "' order by buyday desc";
//        float prz = getPrise(stockid, null);
//        float min = 1000000, max = 0;
//        float dealprz, totalPrise = 0, rr = 0;
//        int count = 0;
//        int sid = -1;
//        float total = 0;
//        java.sql.Date buyDate = null, lastBuyDay = null, sellDay, lastSellDay = null;
//        performanceEntry performEnt = new performanceEntry();
//        rr = getLastReturnRatio(stockid);/*mResultSet.getFloat("returnratio");*/
//        try {
//            connectToServer();
//            mResultSet = mStatement.executeQuery(checkQuery);
//            while(mResultSet.next()) {
//                dealprz = mResultSet.getFloat("prise");
//                if(sid < 0) sid = mResultSet.getInt("strategyindex");
//                buyDate = mResultSet.getDate("buyday");
//                if((lastBuyDay == null) || lastBuyDay.before(buyDate)) {
//                    lastBuyDay = buyDate;
//                }
//                /* Consider some maybe sold and some are not, we have to set 2 flags : have unsold item and last sold item */
//                sellDay = mResultSet.getDate("sellday");
//                if((lastSellDay == null) || lastBuyDay.before(sellDay)) {
//                    lastSellDay = sellDay;
//                }
//                if(dealprz > max) max = dealprz;
//                if(dealprz < min) min = dealprz;
//                total += (prz - dealprz);
//                count ++;
//                totalPrise += dealprz;
//            }
//            mResultSet.close();
//            if(count > 0) {
//                performEnt.count = count;
//                performEnt.perform = total / totalPrise;
//                performEnt.avgprise = totalPrise / count;
//                performEnt.buyinDate = lastBuyDay;
//                performEnt.lastSellDate = lastSellDay;
//                performEnt.checkDate = convertJavaDateToMySQL(new Date());
//                performEnt.returnratio = rr;
//                performEnt.min = min;
//                performEnt.max = max;
//                performEnt.lastprise = prz;
//                performEnt.lastStrategyId = sid;
//            }
//            if(DEBUG_VERBOSE) System.out.println("performance, total:" + total);
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if(performEnt.count != 0) {
//            getInfoForAnalysis(stockid, performEnt);
//            getSeasonEarnForAnalysis(stockid, performEnt);
//            getAnnualEarnForAnalysis(stockid, performEnt);
//            getAnnualDividendForAnalysis(stockid, performEnt);
//            if(updateDB) {
//                updatePerformance(stockid, performEnt);
//                if(lastSellDay == null) {
//                    updateBuyinAnalysis(stockid, performEnt);
//                }
//            }
//        }
//        return performEnt;
//    }
//
    public float getPerformance(String stockid) {
        performanceEntry pe = getPerformance(stockid, true, false);
        return pe.count * pe.avgprise;
    }

    public void updatePerformance(String stockid, performanceEntry pe) {
        Date todayDate = new Date();
        String tableName = buyinPerformance;

        if(!checkIdExistInTable(tableName, stockid,"checkdate",  pe.checkDate.toString())) {
            ArrayList<String> aList = initInsertTable();
            insertValue("stockid", stockid, aList);
            insertValue("averageprise", pe.avgprise, aList);
            insertValue("lastprise", pe.lastprise, aList);
            insertValue("returnratio", pe.returnratio, aList);
            insertValue("performance", pe.perform, aList);
            insertValue("dealdate", /*convertJavaDateToMySQLStr(todayDate)*/ pe.buyinDate, aList);
            insertValue("checkdate", /*convertJavaDateToMySQLStr(todayDate)*/ pe.checkDate, aList);
            insertValue("sold", false, aList);
            insertValue("buyincount", pe.count, aList);
            insertIntoTable(tableName, aList);
        } else {
            if(DEBUG_VERBOSE) System.out.println("updatePerformance Date exist : stockid:" + stockid + " date:" + todayDate + ", try update");
            initUpdateTable();
            addUpdateCol("averageprise", pe.avgprise);
            addUpdateCol("lastprise", pe.lastprise);
            addUpdateCol("returnratio", pe.returnratio);
            addUpdateCol("performance", pe.perform);
            addUpdateCol("dealdate", /*convertJavaDateToMySQLStr(todayDate)*/ pe.buyinDate);
            addUpdateCol("sold", false);
            addUpdateCol("buyincount", pe.count);
            addUpdateParam("stockid", stockid);
            addUpdateParam("checkdate", pe.checkDate);
            performUpdateTable(tableName);
        }

        tableName = buyinPerformanceAccu;
        if(!checkIdExistInTable(tableName, stockid)) {
            initInsertTable();
            insertValue("stockid", stockid);
            insertValue("averageprise", pe.avgprise);
            insertValue("lastprise", pe.lastprise);
            insertValue("returnratio", pe.returnratio);
            insertValue("performance", pe.perform);
            insertValue("dealdate", /*convertJavaDateToMySQLStr(todayDate)*/ pe.buyinDate);
            insertValue("checkdate", /*convertJavaDateToMySQLStr(todayDate)*/ pe.checkDate);
            insertValue("sold", false);
            insertValue("buyincount", pe.count);
            insertIntoTable(tableName);
        } else {
            if(DEBUG_VERBOSE) System.out.println("update performance accumulate exist : stockid:" + stockid + ", try update");
            initUpdateTable();
            addUpdateCol("averageprise", pe.avgprise);
            addUpdateCol("lastprise", pe.lastprise);
            addUpdateCol("returnratio", pe.returnratio);
            addUpdateCol("performance", pe.perform);
            addUpdateCol("dealdate", /*convertJavaDateToMySQLStr(todayDate)*/ pe.buyinDate);
            addUpdateCol("checkdate", /*convertJavaDateToMySQLStr(todayDate)*/ pe.checkDate);
            addUpdateCol("sold", false);
            addUpdateCol("buyincount", pe.count);
            addUpdateParam("stockid", stockid);
            performUpdateTable(tableName);
        }
    }

    ArrayList<String> updateColData = null;
    ArrayList<String> updateParmData = null;

    public void initUpdateTable() {
        updateColData = new ArrayList<>();
        updateParmData = new ArrayList<>();
    }
    public void addUpdateCol(String name) {
        updateColData.add(name + " = null");
    }
    public void addUpdateCol(String name, String value) {
        String insertStr = name + " = '" + value + "'";
        updateColData.add(insertStr);
    }
    public void addUpdateCol(String name, int value) {
        String insertStr = name + " = " + value;
        updateColData.add(insertStr);
    }
    public void addUpdateCol(String name, float value) {
        String insertStr = name + " = " + value;
        updateColData.add(insertStr);
    }
    public void addUpdateCol(String name, Date value) {
        String insertStr;
        if(value != null) {
            insertStr = name + " = '" + convertJavaDateToMySQLStr(value) + "'";
        } else {
            insertStr = name + " = null ";
        }
        updateColData.add(insertStr);
    }
    public void addUpdateCol(String name, boolean value) {
        String insertStr = name + " = '" + (value?1:0) + "'";
        updateColData.add(insertStr);
    }
    public void addUpdateParam(String name) {
        updateParmData.add(name + " = null");
    }
    public void addUpdateParam(String name, String value) {
        String insertStr = name + " = '" + value + "'";
        updateParmData.add(insertStr);
    }
    public void addUpdateParam(String name, int value) {
        String insertStr = name + " = " + value;
        updateParmData.add(insertStr);
    }
    public void addUpdateParam(String name, float value) {
        String insertStr = name + " = " + value;
        updateParmData.add(insertStr);
    }
    public void addUpdateParam(String name, Date value) {
        String insertStr = name + " = '" + convertJavaDateToMySQLStr(value) + "'";
        updateParmData.add(insertStr);
    }
    public void addUpdateParam(String name, boolean value) {
        String insertStr = name + " = '" + (value?1:0) + "'";
        updateParmData.add(insertStr);
    }
    public ResultSet performUpdateTable(String tab) {
        if(updateColData.size() <= 0) return null;
        String updateColStr = "Update " + tab + " set ";
        updateColStr += " " + updateColData.get(0);
        for (int i = 1; i < updateColData.size(); i++) {
            updateColStr += " , " + updateColData.get(i);
        }
        if(updateParmData.size() != 0) {
            updateColStr += " where " + updateParmData.get(0);
            for (int i = 1; i < updateParmData.size(); i++) {
                updateColStr += " AND " + updateParmData.get(i);
            }
        }
        if(DEBUG_SQL_CMD)
            System.out.println(updateColStr);
//        connectToServer();
        try {
            PreparedStatement stmt = mConnection.prepareStatement(updateColStr);
            stmt.executeUpdate();
            stmt.close();
            if(DEBUG_VERBOSE) System.out.println("update " + tab + " success");
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getThisYear() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return year;
    }
    public boolean checkThisYearShareAnnounced(String stockid) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int annualyear = year - 1911 - 1;
        String yearStr = String.valueOf(annualyear);
        String tableName = annualDividendTable;
        boolean yearDivAnnounced = false;
        initSelectTable();
        addSelParmValue("stockid" , stockid);
        addSelParmValue("year", yearStr);
        ResultSet resset = performSelectTable(tableName);
        try {
            if (resset.next()) {
                yearDivAnnounced = true;
            }
            resset.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finishSelectQuery();
        return yearDivAnnounced;
    }
    public void updateBuyinAnalysis(String stockid, performanceEntry pe) {
        String tableName= buyinTableAnalysis;
        updateBuyinAnalysis(tableName, stockid, pe, false);
    }
    public float getRoeScore(float roe) {
        if(roe > 2)
            return 2;
        else if(roe > 0)
            return 1;
        else if(roe > -3)
            return 0;
        return -1;
    }
    public float getRRScore(float rr) {
        if(rr > 10)
            return 2;
        else if(rr > 8)
            return 1;
        else if(rr > 4)
            return 0;
        return -1;
    }
    public float getSeasonScore(float[] se) {
        float c = 0;
        for(int i = 0;i < 4;i++) {
            c += (se[i] > 0 ? 1:0);
        }
        if(c > 3) return 2;
        else if(c > 2) return 1;
        else if(c > 1) return 0;
        return -1;
    }
    public float getAnnualScore(float[] sa, float[] se) {
        float yearPredDiff = se[0] + se[1] + se[2] + se[3] - sa[0];
        if((yearPredDiff/sa[0]) > 0.2) {
            /* Earn per share increase more than 20% */
            return 2;
        } else if((yearPredDiff/sa[0]) > 0) {
            /* Earn per share increase */
            return 1;
        } else if((yearPredDiff/sa[0]) > -0.10) {
            /* Earn per share drop less than 10% */
            return 0;
        }
        /* Earn per share drop more than 10% */
        return -1;
    }
    public float getAnnouncedScore(boolean ann) {
        if(ann) return 1;
        return 0;
    }

    private float getAnnualShareScore(ArrayList<Float> div, float prz) {
        float totalScore = 0;
        float rr;
        for(int i = 0;i < div.size();i++) {
            rr = div.get(i) / prz;
            if(rr > 0.1) {
                totalScore += 1.4;
            } else if(rr > 0.05) {
                totalScore += 1.2;
            } else if(rr > 0) {
                totalScore += 1;
            } else {
                totalScore --;
            }
        }
        /*
        * Total score : 0 : means half time the share is positive
        * max : div.length * 1.4
        * assume : 2/3 is positive , 1/3 is above average 0.05, the score will be length * 1/3 * 1.2 = 0.4 * length
        * assume : 3/4 is positive , 1/2 is above average 0.05, the score will be length * 1/2 * 1.2 = 0.6 * length
         * */
        if(totalScore > (0.6 * div.size())) {
            return 2;
        }
        else if(totalScore > (0.4 * div.size())) {
            return 1;
        }
        else if(totalScore < 0) {
            return -1;
        }
        return 0;
    }
    public float getScore(performanceEntry pe, boolean divAnnounced) {
        float score;
        float roeScore;
        float rrScore;
        float seasonEarnScore;
        float yearEarnScore;
        float announcedScore;
        float histShareScore;
        roeScore = getRoeScore(pe.roe);
        rrScore = getRRScore(pe.returnratio);
        seasonEarnScore = getSeasonScore(pe.season);
        yearEarnScore = getAnnualScore(pe.year, pe.season);
        announcedScore = getAnnouncedScore(divAnnounced);
        histShareScore = getAnnualShareScore(pe.div, pe.lastprise);
        score = roeScore + rrScore + seasonEarnScore + yearEarnScore + announcedScore + histShareScore;
        return score;
    }

    public void updateBuyinAnalysis(String tableName, String stockid, performanceEntry pe, boolean withScore) {
        Date todayDate = new Date();
        boolean divAnnounced = checkThisYearShareAnnounced(stockid);
        String stra;
        if(!checkIdExistInTable(tableName, stockid)) {
            ArrayList<String> aList = initInsertTable();
            insertValue("stockid", stockid, aList);
            insertValue("lastbuydate", pe.buyinDate, aList);
            insertValue("checkdate", pe.checkDate, aList);
            insertValue("amount", pe.count, aList);
            insertValue("avg_prise", pe.avgprise, aList);
            insertValue("returnratio", pe.returnratio, aList);
            if(withScore) {
                insertValue("score", getScore(pe, divAnnounced));
                stra = getBuyinSelloutStrategy(stockid, pe);
                insertValue("action", stra);
            }
            insertValue("lastprise", pe.lastprise, aList);
            insertValue("min_prise", pe.min, aList);
            insertValue("max_prise", pe.max, aList);
            insertValue("performance", pe.perform, aList);
            insertValue("roe", pe.roe, aList);
            insertValue("roa", pe.roa, aList);
            insertValue("book_value", pe.nv, aList);
            insertValue("thisyear", divAnnounced, aList);
            insertValue("stock_div", pe.stockdiv, aList);
            for(int i = 0;i < 4;i++) {
                insertValue("season" + (i+1) + "_earn", pe.season[i], aList);
            }
            for(int i = 0;i < 4;i++) {
                insertValue("year" + (i+1) + "_earn", pe.year[i], aList);
            }
            for(int i = 0;i < 4;i++) {
                insertValue("year" + (i+1) + "_div", pe.div.get(i), aList);
            }
            insertIntoTable(tableName, aList);
        } else {
            if(DEBUG_VERBOSE) System.out.println("updateBuyinAnalysis Date exist : stockid:" + stockid + " table:" + tableName + " date:" + todayDate);
            initUpdateTable();
            addUpdateCol("lastbuydate", pe.buyinDate);
            addUpdateCol("checkdate", pe.checkDate);
            addUpdateCol("amount", pe.count);
            addUpdateCol("avg_prise", pe.avgprise);
            addUpdateCol("returnratio", pe.returnratio);
            if(withScore) {
                addUpdateCol("score", getScore(pe, divAnnounced));
                stra = getBuyinSelloutStrategy(stockid, pe);
                addUpdateCol("action", stra);
            }
            addUpdateCol("lastprise", pe.lastprise);
            addUpdateCol("min_prise", pe.min);
            addUpdateCol("max_prise", pe.max);
            addUpdateCol("performance", pe.perform);
            addUpdateCol("roe", pe.roe);
            addUpdateCol("roa", pe.roa);
            addUpdateCol("book_value", pe.nv);
            addUpdateCol("thisyear", divAnnounced);
            addUpdateCol("stock_div", pe.stockdiv);
            for(int i = 0;i < 4;i++) {
                addUpdateCol("season" + (i+1) + "_earn", pe.season[i]);
            }
            for(int i = 0;i < 4;i++) {
                addUpdateCol("year" + (i+1) + "_earn", pe.year[i]);
            }
            for(int i = 0;i < 4;i++) {
                addUpdateCol("year" + (i+1) + "_div", pe.div.get(i));
            }
            addUpdateParam("stockid", stockid);
            performUpdateTable(tableName);
        }
    }

    public void addWatchTable(ArrayList<StockListUtil.StockIdEntry> idEnt) {
        for(int i = 0;i < idEnt.size() ; i++) {
            StockListUtil.StockIdEntry e = idEnt.get(i);
            // Check id exist
            initSelectTable();
            addSelCol("stockname");
            addSelParmValue("stockid", e.id);
            ResultSet rSet = performSelectTable("watch_table");
            try {
                if (rSet.next()) {
                    rSet.close();
                    finishSelectQuery();
                    System.out.println("Record id:" + e.id + " name:" + e.name + " duplicated in database");
                    continue;
                }
                rSet.close();
            } catch(Exception err) {
                err.printStackTrace();
                break;
            }
            finishSelectQuery();
            // Add into table
            System.out.println("Adding record id:" + e.id + " name:" + e.name);
            initInsertTable();
            insertValue("stockid", e.id);
            insertValue("stockname", e.name);
            insertIntoTable("watch_table");
        }
    }

    public void buyinWatchList() {
        String tableName = watchTable;
        String sid, sname;
        ArrayList<String> idEnt = new ArrayList<>();
        initSelectTable();
        ResultSet rSet = performSelectTable(tableName);
        try {
            while (rSet.next()) {
                sid = rSet.getString("stockid");
                idEnt.add(sid);
            }
            rSet.close();
        } catch(Exception err) {
            err.printStackTrace();
        }
        finishSelectQuery();

        String dateStr = convertJavaDateToMySQLStr(new Date());
        for(int i = 0;i < idEnt.size() ; i++) {
            // stockid	buyday	sellday	amount	prise	strategyindex	strategydescription	returnratio
            getLastReturnRatio(idEnt.get(i));
            tableName = watchBuyinTable;
            buyin(idEnt.get(i), tableName,1,dateStr,mBuyinPrise,1,"Buyin From Command",mBuyinReturnRatio, BUYIN_REGULAR_DAILY);
            tableName = watchBuyinHistTable;
            buyin(idEnt.get(i), tableName,1,dateStr,mBuyinPrise,1,"Buyin From Command",mBuyinReturnRatio, BUYIN_REGULAR_DAILY);
        }
    }
    public void buyinSingleStock(String stockid) {
        String dateStr = convertJavaDateToMySQLStr(new Date());
        getLastReturnRatio(stockid);
        String tableName = watchBuyinTable;
        buyin(stockid, tableName,1,dateStr,mBuyinPrise,1,"Buyin From Command",mBuyinReturnRatio, BUYIN_ADD_AVG);
        tableName = watchBuyinHistTable;
        buyin(stockid, tableName,1,dateStr,mBuyinPrise,1,"Buyin From Command",mBuyinReturnRatio, BUYIN_ADD_AVG);
    }
    public void analysisBuyinWatchList() {
        String tableName = watchTable;
        String sid, sname;
        ArrayList<String> idEnt = new ArrayList<>();
        initSelectTable();
        ResultSet rSet = performSelectTable(tableName);
        try {
            while (rSet.next()) {
                sid = rSet.getString("stockid");
                idEnt.add(sid);
            }
            rSet.close();
        } catch(Exception err) {
            err.printStackTrace();
        }
        finishSelectQuery();

        String dateStr = convertJavaDateToMySQLStr(new Date());
        for(int i = 0;i < idEnt.size() ; i++) {
            // stockid	buyday	sellday	amount	prise	strategyindex	strategydescription	returnratio
            getLastReturnRatio(idEnt.get(i));
            tableName = watchBuyinTable;
            buyin(idEnt.get(i), tableName,1,dateStr,mBuyinPrise,1,"Buyin From Command",mBuyinReturnRatio, BUYIN_ADD_AVG);
            tableName = watchBuyinHistTable;
            buyin(idEnt.get(i), tableName,1,dateStr,mBuyinPrise,1,"Buyin From Command",mBuyinReturnRatio, BUYIN_REGULAR_DAILY);
        }
    }

    public void c(String stockid) {
        String tableName = annualDividendTable;
        float totalSahre = 0, averageShare = 0, lastShare = 0;
        int count = 0;


        initSelectTable();
        addSelParmValue("stockid", stockid);
        addSelOrder("year", false);
        ResultSet shareRes = performSelectTable(tableName);
        try {
            while (shareRes.next()) {
                totalSahre += shareRes.getFloat("total_dividend");
                if(count == 0) lastShare = totalSahre;
                count ++;
            }
            shareRes.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        averageShare = totalSahre / count;

        System.out.println("last share " + lastShare + " total share:" + totalSahre + " avg share:" + averageShare);
        finishSelectQuery();
    }

    public boolean checkTableExist(String tableName) {
        boolean retv = false;
        initSelectTable();
        ResultSet mSet = performSelectTable(tableName, false);
        if(mSet != null) {
            retv = true;
        }
        finishSelectQuery();
        return retv;
    }

    public boolean performStatement(String stmt, boolean verbosePrint) {
        getConnectionStatement();
        try {
            mStatement.executeUpdate(stmt);
        } catch (Exception e) {
            if(verbosePrint) {
                e.printStackTrace();
            }
            return false;
        }
        return true;

    }
    class CreateTableColEntry {
        public String colName;
        public String colType;
        public boolean notNull;
        public String charSet;
        public int strLength;
        public CreateTableColEntry(String cn, String ct, String cs, boolean nn, int sl) {
            colName = cn;
            colType = ct;
            charSet = cs;
            notNull = nn;
            strLength = sl;
        }
    }
    private String createTableQuery = "create table ___TABLE_NAME___ ( ___TABLE_CONTENT___)";
    ArrayList<CreateTableColEntry> colList = null;
    public void initCreateTableQuery() {
        colList = new ArrayList<>();
    }
    public void addCreateTableColEntry(String cn, String ct, String cs, boolean nn, int sl) {
        CreateTableColEntry tCol = new CreateTableColEntry(cn, ct, cs, nn, sl);
        colList.add(tCol);
    }
    private String getCreateTableQuery(String tableName) {
        String colStr, contentStr = "", queryStr = createTableQuery;

        if(colList.size() == 0) {
            return null;
        }
        for(int i = 0;i < colList.size();i++) {
            CreateTableColEntry colEnt = colList.get(i);
            if(colEnt.colType.equalsIgnoreCase("varchar")) {
                colStr = colEnt.colName + " " + colEnt.colType + "(" + colEnt.strLength + ")";
                if(colEnt.charSet != null && colEnt.charSet.equalsIgnoreCase("utf8"))
                    colStr = colStr + " character set utf8 collate utf8_unicode_ci ";
            } else {
                colStr = colEnt.colName + " " + colEnt.colType;
            }
            if(colEnt.notNull)
                colStr = colStr + " not null";
            contentStr = contentStr + colStr;
            if(i < (colList.size() - 1)) {
                contentStr = contentStr + ",";
            }
        }
        queryStr = queryStr.replace("___TABLE_NAME___",tableName);
        queryStr = queryStr.replace("___TABLE_CONTENT___",contentStr);
        return queryStr;
    }
    public void performCreateTable(String tableName) {
        String query = getCreateTableQuery(tableName);
        if(!checkTableExist(tableName)) {
            boolean retv = performStatement(query, true);
            if(!retv) {
                System.err.println("Create table:" + tableName + " failed.");
            }
        }
    }

    private final int kHistDividendScoreColIndex_StockId = 0;
    private final int kHistDividendScoreColIndex_StockName = kHistDividendScoreColIndex_StockId + 1;
    private final int kHistDividendScoreColIndex_Date = kHistDividendScoreColIndex_StockName + 1;
    private final int kHistDividendScoreColIndex_PositiveDividendScore = kHistDividendScoreColIndex_Date + 1;
    private final int kHistDividendScoreColIndex_ReturnRatioScore = kHistDividendScoreColIndex_PositiveDividendScore + 1;
    private final int kHistDividendScoreColIndex_ThreeYearTrendScore = kHistDividendScoreColIndex_ReturnRatioScore + 1;
    private final int kHistDividendScoreColIndex_SimulationScore = kHistDividendScoreColIndex_ThreeYearTrendScore + 1;
    private final int kHistDividendScoreColIndex_BasicInfoScore = kHistDividendScoreColIndex_SimulationScore + 1;
    private final int kHistDividendScoreColIndex_WeightedScore = kHistDividendScoreColIndex_BasicInfoScore + 1;
    private final int kHistDividendScoreColIndex_NonLockupScore = kHistDividendScoreColIndex_WeightedScore + 1;
    private final int kHistDividendScoreColIndex_WeightedNonLockupScore = kHistDividendScoreColIndex_NonLockupScore + 1;
    private final int kHistDividendScoreColIndex_Prize = kHistDividendScoreColIndex_WeightedNonLockupScore + 1;
    private final String[] dbHistDividendScoreColName = new String[] {
            "stockid",
            "stockname",
            "date",
            "positive_dividend_score",
            "return_ratio_score",
            "three_year_trend_score",
            "simulation_score",
            "basic_info_score",
            "weighted_score",
            "non_lockup_score",
            "weighted_score_non_lockup",
            "prize"
    };

    private String mCreateHistDividendTableQuery = "CREATE TABLE __HIST_DIVIDEND_SCORE_TABLE__ ("
            + dbHistDividendScoreColName[kHistDividendScoreColIndex_StockId] + " VARCHAR(20) NOT NULL,"
            + dbHistDividendScoreColName[kHistDividendScoreColIndex_StockName] + " VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,"
            + dbHistDividendScoreColName[kHistDividendScoreColIndex_Date] + " Date NOT NULL,"
            + dbHistDividendScoreColName[kHistDividendScoreColIndex_PositiveDividendScore] + " FLOAT NOT NULL,"
            + dbHistDividendScoreColName[kHistDividendScoreColIndex_ReturnRatioScore] + " FLOAT NOT NULL,"
            + dbHistDividendScoreColName[kHistDividendScoreColIndex_ThreeYearTrendScore] + " FLOAT NOT NULL,"
            + dbHistDividendScoreColName[kHistDividendScoreColIndex_SimulationScore] + " FLOAT NOT NULL,"
            + dbHistDividendScoreColName[kHistDividendScoreColIndex_BasicInfoScore] + " FLOAT NOT NULL,"
            + dbHistDividendScoreColName[kHistDividendScoreColIndex_WeightedScore] + " FLOAT NOT NULL,"
            + dbHistDividendScoreColName[kHistDividendScoreColIndex_NonLockupScore] + " FLOAT NOT NULL,"
            + dbHistDividendScoreColName[kHistDividendScoreColIndex_WeightedNonLockupScore] + " FLOAT NOT NULL,"
            + dbHistDividendScoreColName[kHistDividendScoreColIndex_Prize] + " FLOAT NOT NULL)";

    public void createHistDividendScoreTable(String tableName) {
//        String dbTableName = getTabelNameWithDB(tableName);
//        String query = mCreateHistDividendTableQuery.replace("__HIST_DIVIDEND_SCORE_TABLE__", dbTableName);
//        if(!checkTableExist(tableName)) {
//            boolean retv = performStatement(query, true);
//            if(!retv) {
//                System.err.println("Create table:" + tableName + " failed.");
//            }
//        }
//        String tableName = kPriseHistScoreTableName;
        initCreateTableQuery();
        addCreateTableColEntry(dbHistDividendScoreColName[kHistDividendScoreColIndex_StockId], "varchar", "utf8", true, 20);
        addCreateTableColEntry(dbHistDividendScoreColName[kHistDividendScoreColIndex_StockName], "varchar", "utf8", true, 20);
        addCreateTableColEntry(dbHistDividendScoreColName[kHistDividendScoreColIndex_Date], "date", null, true, 0);
        addCreateTableColEntry(dbHistDividendScoreColName[kHistDividendScoreColIndex_PositiveDividendScore], "float", null, true, 0);
        addCreateTableColEntry(dbHistDividendScoreColName[kHistDividendScoreColIndex_ReturnRatioScore], "float", null, true, 0);
        addCreateTableColEntry(dbHistDividendScoreColName[kHistDividendScoreColIndex_ThreeYearTrendScore], "float", null, true, 0);
        addCreateTableColEntry(dbHistDividendScoreColName[kHistDividendScoreColIndex_SimulationScore], "float", null, true, 0);
        addCreateTableColEntry(dbHistDividendScoreColName[kHistDividendScoreColIndex_BasicInfoScore], "float", null, true, 0);
        addCreateTableColEntry(dbHistDividendScoreColName[kHistDividendScoreColIndex_WeightedScore], "float", null, true, 0);
        addCreateTableColEntry(dbHistDividendScoreColName[kHistDividendScoreColIndex_NonLockupScore], "float", null, true, 0);
        addCreateTableColEntry(dbHistDividendScoreColName[kHistDividendScoreColIndex_WeightedNonLockupScore], "float", null, true, 0);
        addCreateTableColEntry(dbHistDividendScoreColName[kHistDividendScoreColIndex_Prize], "float", null, true, 0);
        performCreateTable(tableName);
    }

    private static class HistDivScore {
        public float rrScore;
        public float positiveScore;
        public float threeYearTrendScore;
        public float simulationScore;
        public float basicInfoScore;
        public float weightedScore;
        public float nonLockupScore;
        public float weightedNonLockupScore;
        public float prizeScore;
    };
    public void insertHistDividendScoreRecord(String tableName, String stockid, String stockName, Date date, HistDivScore score, boolean doUpdate) {
//        if(checkIdExistInTable(tableName, stockid, convertJavaDateToMySQLStr(date))) {
        if(checkIdExistInTable(tableName, stockid)) {
            System.out.println("Record id:" + stockid + " date:" + convertJavaDateToMySQLStr(date) + " exist, do update:" + doUpdate);
            if(doUpdate) {
                System.out.println("Update record stockid:" + stockid +
                        " date:" + convertJavaDateToMySQLStr(date) +
                        " positive score:" + score.positiveScore +
                        " return ratio score:" + score.rrScore +
                        " into table " + tableName);
                initUpdateTable();
                addUpdateCol(dbHistDividendScoreColName[kHistDividendScoreColIndex_StockName], stockName);
                addUpdateCol(dbHistDividendScoreColName[kHistDividendScoreColIndex_PositiveDividendScore], score.positiveScore);
                addUpdateCol(dbHistDividendScoreColName[kHistDividendScoreColIndex_ReturnRatioScore], score.rrScore);
                addUpdateCol(dbHistDividendScoreColName[kHistDividendScoreColIndex_ThreeYearTrendScore], score.threeYearTrendScore);
                addUpdateCol(dbHistDividendScoreColName[kHistDividendScoreColIndex_SimulationScore], score.simulationScore);
                addUpdateCol(dbHistDividendScoreColName[kHistDividendScoreColIndex_BasicInfoScore], score.basicInfoScore);
                addUpdateCol(dbHistDividendScoreColName[kHistDividendScoreColIndex_WeightedScore], score.weightedScore);
                addUpdateCol(dbHistDividendScoreColName[kHistDividendScoreColIndex_NonLockupScore], score.nonLockupScore);
                addUpdateCol(dbHistDividendScoreColName[kHistDividendScoreColIndex_WeightedNonLockupScore], score.weightedNonLockupScore);
                addUpdateCol(dbHistDividendScoreColName[kHistDividendScoreColIndex_Date], date);
                addUpdateCol(dbHistDividendScoreColName[kHistDividendScoreColIndex_Prize], score.prizeScore);
                addUpdateParam(dbHistDividendScoreColName[kHistDividendScoreColIndex_StockId], stockid);
                performUpdateTable(tableName);
            }
        } else {
            System.out.println("Insert record stockid:" + stockid +
                    " date:" + convertJavaDateToMySQLStr(date) +
                    " positive score:" + score.positiveScore +
                    " return ratio score:" + score.rrScore +
                    " into table " + tableName);
            initInsertTable();
            insertValue(dbHistDividendScoreColName[kHistDividendScoreColIndex_StockId],stockid);
            insertValue(dbHistDividendScoreColName[kHistDividendScoreColIndex_StockName],stockName);
            insertValue(dbHistDividendScoreColName[kHistDividendScoreColIndex_Date],date);
            insertValue(dbHistDividendScoreColName[kHistDividendScoreColIndex_PositiveDividendScore],score.positiveScore);
            insertValue(dbHistDividendScoreColName[kHistDividendScoreColIndex_ReturnRatioScore],score.rrScore);
            insertValue(dbHistDividendScoreColName[kHistDividendScoreColIndex_ThreeYearTrendScore],score.threeYearTrendScore);
            insertValue(dbHistDividendScoreColName[kHistDividendScoreColIndex_SimulationScore],score.simulationScore);
            insertValue(dbHistDividendScoreColName[kHistDividendScoreColIndex_BasicInfoScore],score.basicInfoScore);
            insertValue(dbHistDividendScoreColName[kHistDividendScoreColIndex_WeightedScore],score.weightedScore);
            insertValue(dbHistDividendScoreColName[kHistDividendScoreColIndex_NonLockupScore], score.nonLockupScore);
            insertValue(dbHistDividendScoreColName[kHistDividendScoreColIndex_WeightedNonLockupScore], score.weightedNonLockupScore);
            insertValue(dbHistDividendScoreColName[kHistDividendScoreColIndex_Prize], score.prizeScore);
            insertIntoTable(tableName);
        }
    }



    private boolean oShowDebugMessage = false;
    private float basicInfoOverheadFunc(float basicScore) {
        /*
        Basic score distribution :
        10, 9, 8, 7 : rr , annual, roe, season all good --> no overhead
        6, 5, 4 : rr , annual, roe, season,  some of then is bad --> overhead -1
        3, 2, 1 : rr , annual, roe, season,  mush of then is bad --> overhead -3
        less the l : rr , annual, roe, season,  mush of then is bad --> overhead -5
        */
        if(basicScore > 6) return 0;
        if(basicScore > 3) return -0.1f;
        if(basicScore > 0) return -0.5f;
        return -1f;
    }

    private void assignNewDiv(float div, float[] threeYearDivs) {
        threeYearDivs[0] = div;
    }
    private void shiftThreeYearDiv(float[] threeYearDivs) {
        threeYearDivs[2] = threeYearDivs[1];
        threeYearDivs[1] = threeYearDivs[0];
    }

    private float countThreeYearDivTotal(float[] threeYearDivs) {
        return threeYearDivs[0] + threeYearDivs[1] + threeYearDivs[2];
    }

    private float getYearDiv(int year, float[] threeYearDivs) {
        return threeYearDivs[year];
    }
    public void getOneHistDivScore(String stockid, String stockName, Date date) {
        float rrScore = 0;
        float rrScoreAvg = 0;
        performanceEntry pe = getPerformance(stockid, false, true);
        int positiveCnt = 0;
        float rr = 0;
        //float diV;
        float prz;
        //float div3 = -1, div2 = -1;
        float[] threeYearDiv = new float[]{-1.0f, -1.0f, -1.0f};
        float div3avg = -1, div3avgCheck = -1, div3avgScore = 0;
        float totalDiv = 0;
        if(date != null) {
            prz = getPrise(stockid,date);
            if(prz == 0) {
                prz = pe.lastprise;
            }
        } else {
            prz = pe.lastprise;
            date = new Date();
        }
        System.out.println("Prize:" + prz);
        for(int i = 0;i < pe.div.size();i++) {
            //diV = pe.div.get(i);
            assignNewDiv(pe.div.get(i), threeYearDiv);
            if(i < 5) totalDiv += getYearDiv(0, threeYearDiv);
            if(getYearDiv(2, threeYearDiv) >= 0) {
                if(div3avg >= 0) {
                    div3avgCheck = (div3avg - countThreeYearDivTotal(threeYearDiv)) / div3avg;
                    if(div3avgCheck > 0.05) {
                        div3avgScore += 0.05;
                    }
                }
                if(oShowDebugMessage) System.out.println("div3avg:" + div3avg + " div1:" + getYearDiv(0, threeYearDiv) + " div2:" + getYearDiv(1, threeYearDiv) + " div3:" + getYearDiv(2, threeYearDiv) + " div3avgCheck:" + div3avgCheck + " div3avgScore:" + div3avgScore);
                div3avg = countThreeYearDivTotal(threeYearDiv);

            }
            if(getYearDiv(0, threeYearDiv) > 0) {
                positiveCnt ++;
            }
            rr = getYearDiv(0, threeYearDiv) / prz;
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
//            div3 = div2;
//            div2 = diV;
            shiftThreeYearDiv(threeYearDiv);
        }
        float rrScoreNorm = rrScore / rrScoreAvg;
        /* Check for NaN */
        if(rrScoreNorm != rrScoreNorm) {
            rrScoreNorm = -10;
        }
        float posiScoreNorm = (float)positiveCnt / (float)pe.div.size();
        if(posiScoreNorm != posiScoreNorm) {
            posiScoreNorm = -10;
        }
        float stabilityRatio = 1.0f;
        if(pe.div.size() > 0) {
            stabilityRatio = (float)pe.div.size() / 7.0f;
        }
        HistDivScore score = new HistDivScore();
        score.rrScore = rrScoreNorm * stabilityRatio;
        score.positiveScore = posiScoreNorm * stabilityRatio;
        score.threeYearTrendScore = div3avgScore;
        if(pe.div.size() > 0 && prz != 0) {
            if(pe.div.size() < 5) {
                score.simulationScore = (totalDiv / pe.div.size()) / prz;
            } else {
                score.simulationScore = (totalDiv / 5.0f) / prz;
            }
        } else {
            score.simulationScore = 0;
        }
        score.basicInfoScore = getScore(pe, true);
        // For prize less than 7 NTD, we consider it as dangerous, and it's return ratio may be greatly error
        score.prizeScore = prz;
        score.weightedScore = score.rrScore + score.simulationScore + score.threeYearTrendScore + score.positiveScore + (score.basicInfoScore / 10) + basicInfoOverheadFunc(score.basicInfoScore);
        score.nonLockupScore = getPriseHistScore(stockid, date);
        score.weightedNonLockupScore = score.weightedScore + score.nonLockupScore;
        insertHistDividendScoreRecord(kHistDividendScoreTableName, stockid, stockName, date, score, true);
    }
    class PriseHistInfo {
        public String stockid;
        public Date date;
        public int duration;
        public boolean earn10pa;
        public boolean earn20pa;
        public boolean earn30pa;
        public boolean earn40pa;
        public boolean earn50pa;
        public float max_earning;
        public float total_earning;
        public float total_loss;
        public float score;
    }
    public void createPriseHistTable() {
        String tableName = kPriseHistScoreTableName;
        initCreateTableQuery();
        addCreateTableColEntry("stockid", "varchar", "utf8", true, 20);
        addCreateTableColEntry("date", "date", null, true, 0);
        addCreateTableColEntry("duration", "int", null, true, 0);
        addCreateTableColEntry("earn_10_pa", "boolean", null, true, 0);
        addCreateTableColEntry("earn_20_pa", "boolean", null, true, 0);
        addCreateTableColEntry("earn_30_pa", "boolean", null, true, 0);
        addCreateTableColEntry("earn_40_pa", "boolean", null, true, 0);
        addCreateTableColEntry("earn_50_pa", "boolean", null, true, 0);
        addCreateTableColEntry("max_earning", "float", null, true, 0);
        addCreateTableColEntry("total_earning", "float", null, true, 0);
        addCreateTableColEntry("total_loss", "float", null, true, 0);
        addCreateTableColEntry("score", "float", null, true, 0);
        performCreateTable(tableName);
    }

    ArrayList<PriseHistInfo> priseHistInfoList = new ArrayList<>();

    class AvoidLockUpScore {
        public int total_count = 0;
        final int kLockupBoundary2Pa = 0;
        final int kLockupBoundary10Pa = 1;
        final int kLockupBoundary30Pa = 2;
        final int kLockupBoundaryCount = 3;
        public int[] max_duration = {0, 0, 0};
        public int[] current_duration = {0, 0, 0};
        public int[] avg_dur_aquare_count = {0, 0, 0};
        public int[] total_dur_count = {0, 0, 0};
        public int[] latest_duration = {0, 0, 0};
        public float[] target_boundary = {0.02f, 0.1f, 0.3f};
        public float[] maxDurScore = {0.0f, 0.0f, 0.0f};
        public float[] avgDurScore = {0.0f, 0.0f, 0.0f};
        public float[] latDurScore = {0.0f, 0.0f, 0.0f};
        boolean[] lastRecordAdded = {false, false, false};
        public float buyPrise = 0;
        float getRR(float monitorPrise) {
            return (monitorPrise - buyPrise)/buyPrise;
        }
        public void addMonitorMoundryTarget(int target, float mPrz) {
            current_duration[target] ++;
            if(max_duration[target] < current_duration[target]) 
              max_duration[target] = current_duration[target];
            else {
                if(latest_duration[target] == 0) {
                    latest_duration[target] = max_duration[target];
                }
            }
            // Assume : previous rr is less than target_boundary, now we get another rr. In case
            // 1. rr still less than target_boundary, we just increase the duration (in days)
            // 2. rr greater than target_boundary, we add "one duration period" and reset the current duration

            // Assume : previous rr is greater than target_boundary, now we get another rr. In such case, current_duration was reset
            // 1. rr still less than target_boundary, we just increase the duration (in days)
            // 2. rr greater than target_boundary, since duration was reset, it will not add to duration period
            if(getRR(mPrz) > target_boundary[target]) {
//                avg_dur_aquare_count[target] += current_duration[target] * current_duration[target];
                // To avoid prise climbing up
                if(current_duration[target] > 2) {
                    total_dur_count[target] ++;
                    avg_dur_aquare_count[target] += current_duration[target];
                }
                current_duration[target] = 0;
//                lastRecordAdded[target] = true;
            } else {
//                lastRecordAdded[target] = false;
            }
        }
        public float getLockUpBoundaryTargetScore(int target) {
            if(latest_duration[target] == 0) latest_duration[target] = max_duration[target];
//            if(!lastRecordAdded[target]) {
            if(current_duration[target] != 0) {
                if(current_duration[target] > 2) {
                    total_dur_count[target] ++;
                    avg_dur_aquare_count[target] += current_duration[target];
                }
                current_duration[target] = 0;
            }
            maxDurScore[target] = (10.0f/(float)max_duration[target]);
            avgDurScore[target] = avg_dur_aquare_count[target] == 0 ? 0 : ((float)total_dur_count[target]/(float)avg_dur_aquare_count[target]);
            latDurScore[target] = (10.0f/(float)latest_duration[target]);
            System.out.println("Target " + target +
                    " Max duration:" + max_duration[target] +
                    " Latest duration:" + latest_duration[target] +
                    " average duration:" + (total_dur_count[target] == 0 ? 0:(avg_dur_aquare_count[target]/total_dur_count[target])) +
                    " Total dur count:" + total_dur_count[target]);
            System.out.println("maxDurScore:" + maxDurScore[target] + " latestDurScore:" + latDurScore[target] + " avgDurScore:" + avgDurScore[target]);
            float score = maxDurScore[target]*0.2f + latDurScore[target]*0.0f + avgDurScore[target]*0.8f;
            return maxDurScore[target]*0.2f + latDurScore[target]*0.0f + avgDurScore[target]*0.8f;
        }
        public void addMonitorRecord(float monitorPrise) {
//            total_count ++;
            addMonitorMoundryTarget(kLockupBoundary2Pa, monitorPrise);
            addMonitorMoundryTarget(kLockupBoundary10Pa, monitorPrise);
            addMonitorMoundryTarget(kLockupBoundary30Pa, monitorPrise);
//            current_30pa_duration ++;
//            current_10pa_duration ++;
//            if(max_10pa_duration < current_10pa_duration) max_10pa_duration = current_10pa_duration;
//            if(getRR(monitorPrise) > 0.1) {
//                total_10pa_dur_count ++;
//                current_10pa_duration = 0;
//            }
//            if(max_30pa_duration < current_30pa_duration) max_30pa_duration = current_30pa_duration;
//            if(getRR(monitorPrise) > 0.3) {
//                total_30pa_dur_count ++;
//                current_30pa_duration = 0;
//            }
        }
        public float getLockUpScore() {
            float totalScore = 0;
            totalScore += getLockUpBoundaryTargetScore(kLockupBoundary2Pa) * 0.35f;
            totalScore += getLockUpBoundaryTargetScore(kLockupBoundary10Pa) * 0.55f;
            totalScore += getLockUpBoundaryTargetScore(kLockupBoundary30Pa) * 0.1f;
            System.out.println("Total score:" + totalScore);
//            float maxDur10Score = (10.0f/(float)max_10pa_duration);
//            float avgDur10Score = ((float)total_10pa_dur_count/(float)total_count);
//            float maxDur30Score = (10.0f/(float)max_30pa_duration);
//            float avgDur30Score = ((float)total_30pa_dur_count/(float)total_count);
//            System.out.println("Max 10% duration:" + max_10pa_duration + " average 10% duration:" + (total_10pa_dur_count == 0 ? 0:(total_count/total_10pa_dur_count)));
//            System.out.println("Max 30% duration:" + max_30pa_duration + " average 30% duration:" + (total_30pa_dur_count == 0 ? 0:(total_count/total_30pa_dur_count)));
//            System.out.println("maxDur10Score:" + maxDur10Score + " avgDur10Score:" + avgDur10Score);
//            System.out.println("maxDur30Score:" + maxDur30Score + " avgDur30Score:" + avgDur30Score);
            return totalScore;
        }
    }
    public float getSimulationScore(String stockid, Date buyDate) {
        return 0.0f;
    }
    public float getHistPrise(String stockid, Date buyDate) {
        String tableName = dailyInfoHistoryTable;
        initSelectTable();
        addSelParmValue("stockid", stockid);
        addSelParmValue("date", buyDate, "=");
        addSelOrder("date", false); // order by date desc
        ResultSet rSet = performSelectTable(tableName);
        float prz = 0.0f;
        try {
            while (rSet.next()) {
                prz = rSet.getFloat("dealprise");
                if(prz == 0) prz = rSet.getFloat("sellout");
                if(prz == 0) prz = rSet.getFloat("buyin");
                if(prz != 0) break;
            }
            rSet.close();
        }catch (Exception e) {

        }
        finishSelectQuery();
        return prz;
    }

    class HistPrizeRecord {
        public float prize;
        public Date date;
        public HistPrizeRecord(float f, Date d) {
            prize = f;
            date = d;
        }
    }
    public ArrayList<HistPrizeRecord> getHistPriseRecList(String stockid, Date startDate, Date endDate) {
        String tableName = dailyInfoHistoryTable;
        initSelectTable();
        addSelParmValue("stockid", stockid);
        if(startDate != null)addSelParmValue("date", startDate, ">=");
        if(endDate != null) addSelParmValue("date", endDate, "<=");
        ResultSet rSet = performSelectTable(tableName);
        ArrayList<HistPrizeRecord> przList = new ArrayList();
        float prz = 0.0f;
        java.sql.Date sqlDate;
        Date d;
        try {
            while (rSet.next()) {
                try {
                    prz = rSet.getFloat("dealprise");
                    sqlDate = rSet.getDate("date");
                    d = convertMySQLDateToJava(sqlDate);
                    if (prz == 0) prz = rSet.getFloat("sellout");
                    if (prz == 0) prz = rSet.getFloat("buyin");
                    if (prz != 0) {
                        przList.add(new HistPrizeRecord(prz,d));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e) {

        }
        finishSelectQuery();
        return przList;
    }
    public ArrayList<Float> getHistPriseList(String stockid, Date startDate, Date endDate) {
        String tableName = dailyInfoHistoryTable;
        initSelectTable();
        addSelParmValue("stockid", stockid);
        if(startDate != null)addSelParmValue("date", startDate, ">=");
        if(endDate != null) addSelParmValue("date", endDate, "<=");
        ResultSet rSet = performSelectTable(tableName);
        ArrayList<Float> przList = new ArrayList();
        float prz = 0.0f;
        try {
            while (rSet.next()) {
                try {
                    prz = rSet.getFloat("dealprise");
                    if (prz == 0) prz = rSet.getFloat("sellout");
                    if (prz == 0) prz = rSet.getFloat("buyin");
                    if (prz != 0) {
                        przList.add(prz);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e) {

        }
        finishSelectQuery();
        return przList;
    }

    public float getAnnualDividend(String stockid, int lunar_year) {
        String tableName = annualDividendTable;
        initSelectTable();
        addSelCol("total_dividend");
        addSelParmValue("stockid",stockid);
        addSelParmValue("year", lunar_year);
        ResultSet resSet = performSelectTable(tableName);
        float prz = 0;
        try {
            while (resSet.next()) {
                prz = resSet.getFloat("total_dividend");
                break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        finishSelectQuery();
        return prz;
    }

    public class ProfitRatio {
        public float locking_day_ratio;
        public float expected_value_ratio;
        public float average_value_ratio;
        public float score_weight;
        public ProfitRatio(float lk, float exp, float avg, float sw) {
            locking_day_ratio = lk;
            expected_value_ratio = exp;
            average_value_ratio = avg;
            score_weight = sw;
        }
    }

    public class SimulationResult {
        String stockid;
        Date simulation_date;
        public float share_ratio;
        public int interval;
        public ProfitRatio pr0;
        public ProfitRatio pr10;
        public ProfitRatio pr20;
        public SimulationResult(String sid, Date sd, float rr, int intv, ProfitRatio p0, ProfitRatio p10, ProfitRatio p20) {
            stockid = sid;
            simulation_date = sd;
            share_ratio = rr;
            interval = intv;
            pr0 = p0;
            pr10 = p10;
            pr20 = p20;;
        }
    }

    public ProfitRatio getHistExpectedPriseEarning(String stockid, Date startDate, Date endDate, float cutting_ratio, float sw) {
        ArrayList<Float> przList = getHistPriseList(stockid, startDate, endDate);
        float firstPrz = 0, prz, total_count = 0, profit = 0, profit_day_count = 0;
        Iterator iter = przList.iterator();
        if(iter.hasNext()) {
            firstPrz = (Float)iter.next();
        }
        while(iter.hasNext()) {
            prz = (Float)iter.next();
            if((prz/firstPrz) > (1 + cutting_ratio)) {
                profit += (prz - firstPrz);
                profit_day_count ++;
            }
            total_count ++;
        }
        ProfitRatio profitRatio;
        if(profit_day_count != 0) {
            profitRatio = new ProfitRatio(profit_day_count / total_count, profit / (total_count * firstPrz), profit / (profit_day_count * firstPrz), sw);
        }
        else {
            profitRatio = new ProfitRatio(profit_day_count / total_count, profit / (total_count * firstPrz), 0, sw);
        }
        return profitRatio;

    }
    // Assume I bought 10 at the simulation date
    // If stock drop down 20% , I buy 5, prise change to average prise , reset
    // If stock goes up 20%, I sell 5, prise change to new prise , reset
    public void getPriseDistribution(String stockid, Date startDate, Date endDate) {
        ArrayList<HistPrizeRecord> przList = getHistPriseRecList(stockid, startDate, endDate);
        HistPrizeRecord przRec;
        float refPrz = 0, prz = 0, stock_count = 10, cost = 0, earning = 0, sell_buy_amount = 0, buy_amount = 5, netval_cost = 0, buy_prize = 0, max_cost = 0, total_earning = 0;
        Iterator iter = przList.iterator();
        if(iter.hasNext()) {
            przRec = (HistPrizeRecord)iter.next();
            buy_prize = refPrz = przRec.prize;
        }
        netval_cost = cost = stock_count * refPrz;
        System.out.println("Init cost:" + cost + " earning:" + earning + " left amount:" + stock_count + " value:" + stock_count * prz + " prize:" + buy_prize + " orig cost:" + netval_cost);
        while(iter.hasNext()) {
            przRec = (HistPrizeRecord)iter.next();
            prz = przRec.prize;
            if(prz/refPrz > 1.2) {
                if(stock_count > 0) {
                    if (stock_count > 2) {
                        sell_buy_amount = stock_count / 2;
                    } else {
                        sell_buy_amount = stock_count;
                    }
                    earning = sell_buy_amount * prz;
                    total_earning += earning;
                    stock_count -= sell_buy_amount;
                    cost -= buy_prize * sell_buy_amount;
                    System.out.println("Found earning 20% profit, sell:" + sell_buy_amount + " cost:" + cost + " earning:" + earning + " left amount:" + stock_count + " current prize:" + prz + " ref prize:" + refPrz + " date:" + przRec.date);
                } else {
                    System.out.println("Found earning 20% profit, no stock to sell");
                }
                refPrz = prz;
            } else if(prz/refPrz < 0.8){
                sell_buy_amount = buy_amount;
                cost += sell_buy_amount * prz;
                stock_count += sell_buy_amount;
                buy_prize = cost/stock_count;
                refPrz = prz;
               System.out.println("Found droping 20%, buy:" + sell_buy_amount + " cost:" + cost + " earning:" + earning + " left amount:" + stock_count + " current prize:" + prz + " ref prize:" + refPrz + " date:" + przRec.date);
            }
            if(max_cost < cost) max_cost = cost;
        }
        System.out.println("Final cost:" + cost + " earning:" + total_earning + " left amount:" + stock_count + " value:" + stock_count * prz + " total:" + (total_earning + stock_count * prz) + " prize:" + prz);
        System.out.println("Max cost:" + max_cost + " profit:" + (total_earning + stock_count * prz - cost) + " ratio:" + ((total_earning + stock_count * prz - cost)/max_cost));
    }

    public SimulationResult simulate_yoy_performance(String stockid, Date simulate_date, int year_offset, boolean do_update_db) {
        Calendar prevYear = Calendar.getInstance();
        Date today = new Date();
        float prz;
        if(simulate_date == null && (year_offset > 5 || year_offset <= 0)) {
            System.out.println("Specify date or year offset");
            return null;
        }
        if(simulate_date == null) {
            prevYear.add(Calendar.YEAR, - year_offset);
            int year = prevYear.get(Calendar.YEAR);
            // 1/1 is somehow a holiday
            prevYear.set(year,0,1);
            simulate_date = prevYear.getTime();
        } else {
            int this_year = prevYear.get(Calendar.YEAR);
            prevYear.setTime(simulate_date);
            int simu_year = prevYear.get(Calendar.YEAR);
            year_offset = this_year - simu_year;
        }
        // 1. get the prise of the day
        int limit = 0;
        while((prz = getHistPrise(stockid, simulate_date)) == 0) {
            prevYear.add(Calendar.DATE, 1);
            simulate_date = prevYear.getTime();
            if(limit > 10) {
                return null;
            }
            limit ++;
        }
        System.out.println("Stockid:" + stockid + " Date:" + simulate_date + " prise:" + prz);
        // 2. get annual share
        float share_amount = 0;
        int year_of_lunar = prevYear.get(Calendar.YEAR) - 1911;
        for(int i = 0;i < year_offset; i++) {
            share_amount += getAnnualDividend(stockid, year_of_lunar + i);
        }
        System.out.println("Year:" + year_of_lunar + " share:" + share_amount + " ratio:" + (share_amount/prz));
        // 3. Simulate "expected earning value"
        StockSqlUtil.ProfitRatio pr = getHistExpectedPriseEarning(stockid, simulate_date, today, 0, 0.08333f);
        System.out.println("Cutting ratio 0 Profit count ratio:" + pr.locking_day_ratio + " exp value ratio:" + pr.expected_value_ratio + " avg value ratio:" + pr.average_value_ratio);
        StockSqlUtil.ProfitRatio pr10 = getHistExpectedPriseEarning(stockid, simulate_date, today, 0.1f, 0.16667f);
        System.out.println("Cutting ratio 10% Profit count ratio:" + pr10.locking_day_ratio + " exp value ratio:" + pr10.expected_value_ratio + " avg value ratio:" + pr10.average_value_ratio);
        StockSqlUtil.ProfitRatio pr20 = getHistExpectedPriseEarning(stockid, simulate_date, today, 0.2f, 0.25f);
        System.out.println("Cutting ratio 20% Profit count ratio:" + pr20.locking_day_ratio + " exp value ratio:" + pr20.expected_value_ratio + " avg value ratio:" + pr20.average_value_ratio);
        SimulationResult sRes = new SimulationResult(stockid, today, share_amount/(prz * year_offset), year_offset, pr, pr10, pr20);
        if(do_update_db)
            addSimulationResult(sRes);
        return sRes;
    }

    public void simulate_yoy_distribution(String stockid, Date simulate_date, int year_offset, boolean do_update_db) {
        Calendar prevYear = Calendar.getInstance();
        Date today = new Date();
        float prz;
        if(simulate_date == null && (year_offset > 5 || year_offset <= 0)) {
            System.out.println("Specify date or year offset");
            return;
        }
        if(simulate_date == null) {
            prevYear.add(Calendar.YEAR, - year_offset);
            int year = prevYear.get(Calendar.YEAR);
            // 1/1 is somehow a holiday
            prevYear.set(year,0,1);
            simulate_date = prevYear.getTime();
        } else {
            int this_year = prevYear.get(Calendar.YEAR);
            prevYear.setTime(simulate_date);
            int simu_year = prevYear.get(Calendar.YEAR);
            year_offset = this_year - simu_year;
        }
        // 1. get the prise of the day
        int limit = 0;
        while((prz = getHistPrise(stockid, simulate_date)) == 0) {
            prevYear.add(Calendar.DATE, 1);
            simulate_date = prevYear.getTime();
            if(limit > 10) {
                System.out.println("Stockid:" + stockid + " Date:" + simulate_date + " has no prise record on this date, exit");
                return;
            }
            limit ++;
        }
        System.out.println("Stockid:" + stockid + " Date:" + simulate_date + " prise:" + prz);
        // 2. get annual share
        getPriseDistribution(stockid, simulate_date, today);
    }
    public final String kStockIdField = "stockid";
    public final String kSimulationDateField = "simulation_date";
    public final String kSimulationIntervalField = "simulate_interval_year";
    public final String kSimulationReturnRatioField = "return_ratio";
    public final String kSimulationProfitLockRatio0Field = "profit_lock_ratio_0";
    public final String kSimulationProfitExpectedRatio0Field = "profit_expected_value_ratio_0";
    public final String kSimulationProfitAverageRatio0Field = "profit_average_value_ratio_0";
    public final String kSimulationProfitLockRatio10Field = "profit_lock_ratio_10";
    public final String kSimulationProfitExpectedRatio10Field = "profit_expected_value_ratio_10";
    public final String kSimulationProfitAverageRatio10Field = "profit_average_value_ratio_10";
    public final String kSimulationProfitLockRatio20Field = "profit_lock_ratio_20";
    public final String kSimulationProfitExpectedRatio20Field = "profit_expected_value_ratio_20";
    public final String kSimulationProfitAverageRatio20Field = "profit_average_value_ratio_20";
    public final String kSimulationScoreField = "simulation_score";

    public float getSimulationResScore(SimulationResult sRes) {
        // 0%  1/6
        // 10% 2/6
        // 20% 3/6
        return sRes.share_ratio
                + (sRes.pr0.locking_day_ratio + sRes.pr0.expected_value_ratio + sRes.pr0.average_value_ratio) * sRes.pr0.score_weight
                + (sRes.pr10.locking_day_ratio + sRes.pr10.expected_value_ratio + sRes.pr10.average_value_ratio) * sRes.pr10.score_weight
                + (sRes.pr20.locking_day_ratio + sRes.pr20.expected_value_ratio + sRes.pr20.average_value_ratio) * sRes.pr20.score_weight;
    }
    public void addSimulationResult(SimulationResult sRes) {
        String tableName = kSimulationTableName;
        if(checkIdExistInTable(tableName, sRes.stockid, kSimulationIntervalField, sRes.interval)) {
            initUpdateTable();
            addUpdateCol(kSimulationDateField, sRes.simulation_date);
            addUpdateCol(kSimulationReturnRatioField, sRes.share_ratio);
            addUpdateCol(kSimulationProfitLockRatio0Field, sRes.pr0.locking_day_ratio);
            addUpdateCol(kSimulationProfitExpectedRatio0Field, sRes.pr0.expected_value_ratio);
            addUpdateCol(kSimulationProfitAverageRatio0Field, sRes.pr0.average_value_ratio);
            addUpdateCol(kSimulationProfitLockRatio10Field, sRes.pr10.locking_day_ratio);
            addUpdateCol(kSimulationProfitExpectedRatio10Field, sRes.pr10.expected_value_ratio);
            addUpdateCol(kSimulationProfitAverageRatio10Field, sRes.pr10.average_value_ratio);
            addUpdateCol(kSimulationProfitLockRatio20Field, sRes.pr20.locking_day_ratio);
            addUpdateCol(kSimulationProfitExpectedRatio20Field, sRes.pr20.expected_value_ratio);
            addUpdateCol(kSimulationProfitAverageRatio20Field, sRes.pr20.average_value_ratio);
            addUpdateCol(kSimulationScoreField, getSimulationResScore(sRes));
            addUpdateParam(kStockIdField, sRes.stockid);
            addUpdateParam(kSimulationIntervalField, sRes.interval);
            performUpdateTable(tableName);
        } else {
            initInsertTable();
            insertValue(kStockIdField, sRes.stockid);
            insertValue(kSimulationDateField, sRes.simulation_date);
            insertValue(kSimulationIntervalField, sRes.interval);
            insertValue(kSimulationReturnRatioField, sRes.share_ratio);
            insertValue(kSimulationProfitLockRatio0Field, sRes.pr0.locking_day_ratio);
            insertValue(kSimulationProfitExpectedRatio0Field, sRes.pr0.expected_value_ratio);
            insertValue(kSimulationProfitAverageRatio0Field, sRes.pr0.average_value_ratio);
            insertValue(kSimulationProfitLockRatio10Field, sRes.pr10.locking_day_ratio);
            insertValue(kSimulationProfitExpectedRatio10Field, sRes.pr10.expected_value_ratio);
            insertValue(kSimulationProfitAverageRatio10Field, sRes.pr10.average_value_ratio);
            insertValue(kSimulationProfitLockRatio20Field, sRes.pr20.locking_day_ratio);
            insertValue(kSimulationProfitExpectedRatio20Field, sRes.pr20.expected_value_ratio);
            insertValue(kSimulationProfitAverageRatio20Field, sRes.pr20.average_value_ratio);
            insertValue(kSimulationScoreField, getSimulationResScore(sRes));
            insertIntoTable(tableName);
        }
    }

    public class OverallResultItem {
        public SimulationResult res;
        public String desc;
        public float itemScore;
        public float weight;
        public OverallResultItem(SimulationResult r, String d, float w) {
            res = r;
            desc = d;
            weight = w;
            itemScore = getSimulationResScore(res);
        }
        public float getWeightedItemScore() {
            return itemScore * weight;
        }
    }
    ArrayList<OverallResultItem> gOverallItemList;
    public void initOverallScore() {
        gOverallItemList = new ArrayList<>();
    }

    public void addOverallItem(SimulationResult r, String d, float w) {
        OverallResultItem i = new OverallResultItem(r,d,w);
        gOverallItemList.add(i);
    }

    public float countOverallItemScore(ArrayList<OverallResultItem> sResItemList) {
        Iterator iter = sResItemList.iterator();
        OverallResultItem item;
        float overall_score = 0;
        while(iter.hasNext()) {
            item = (OverallResultItem)iter.next();
            if(item != null)
                overall_score += item.getWeightedItemScore();
        }
        return overall_score;
    }
    public void addOverallSimulationResult(String stockid, Date date, boolean do_update_db) {
        addOverallSimulationResult(stockid,date, gOverallItemList, do_update_db);
    }
    public final String kDateField = "date";
    public final String[] kSimulationScoreYoyScoreField = {"score1", "score2", "score3"};
    public final String[] kSimulationScoreYoyScoreDescField = {"score1_desc", "score2_desc", "score3_desc"};
    public final String kSimulationScoreYoyOverallScoreField = "overall_score";

    public void addOverallSimulationResult(String stockid, Date date, ArrayList<OverallResultItem> sResList, boolean do_update_db) {
        String tableName = kSimulationYoyTableName;
        float score = countOverallItemScore(sResList);
        System.out.println("Total score:" + score);
        if(!do_update_db) {
            return;
        }
        OverallResultItem resi;
        if(checkIdExistInTable(tableName, stockid, kDateField, date)) {
            initUpdateTable();
            for(int i = 0;i < 3/*kSimulationScoreYoyScoreField.length*/;i++) {
                resi = sResList.get(i);
                if(resi != null) {
                    addUpdateCol(kSimulationScoreYoyScoreField[i], resi.itemScore);
                    addUpdateCol(kSimulationScoreYoyScoreDescField[i], resi.desc);
                    System.out.println(sResList.get(i).desc + " score:" + resi.itemScore);
                } else {
                    addUpdateCol(kSimulationScoreYoyScoreField[i], 0);
                    addUpdateCol(kSimulationScoreYoyScoreDescField[i], "no rec");
                }
            }
            addUpdateCol(kSimulationScoreYoyOverallScoreField, score);
            addUpdateParam(kStockIdField, stockid);
            addUpdateParam(kDateField, date);
            performUpdateTable(tableName);
        } else {
            initInsertTable();
            insertValue(kStockIdField, stockid);
            insertValue(kDateField, date);
            for(int i = 0;i < 3/*kSimulationScoreYoyScoreField.length*/;i++) {
                resi = sResList.get(i);
                if (resi != null) {
                    insertValue(kSimulationScoreYoyScoreField[i], resi.itemScore);
                    insertValue(kSimulationScoreYoyScoreDescField[i], resi.desc);
                    System.out.println(sResList.get(i).desc + " score:" + resi.itemScore);
                } else {
                    insertValue(kSimulationScoreYoyScoreField[i], 0);
                    insertValue(kSimulationScoreYoyScoreDescField[i], "no rec");
                }
            }
            insertValue(kSimulationScoreYoyOverallScoreField, score);
            insertIntoTable(tableName);
        }
    }

    public float getPriseHistScore(String stockid, Date buyDate) {
        String tableName = dailyInfoHistoryTable;
        float buyPrise = getPrise(stockid, buyDate);
        if(buyPrise <= 0) return 0;
        float tracePrise;
        float maxPrise = 0;
        float totalPositiveEarning = 0;
        float totalNegativeEarning = 0;
        int recordCount = 0;
        int positiveCount = 0;
        int negativeCount = 0;

        int max_30pa_duration = 0, current_30pa_duration = 0, total_30pa_dur_count = 0, total_count = 0;
        int max_10pa_duration = 0, current_10pa_duration = 0, total_10pa_dur_count = 0;
        boolean have10Pa = false;
        boolean have20Pa = false;
        boolean have30Pa = false;
        boolean have40Pa = false;
        boolean have50Pa = false;
        float lockupScore;
        AvoidLockUpScore lockUpScore = new AvoidLockUpScore();
        initSelectTable();
        addSelCol("dealprise");
        addSelParmValue("stockid", stockid);
        addSelOrder("date", false);
        ResultSet rSet = performSelectTable(tableName);
        priseHistInfoList.clear();
        lockUpScore.buyPrise = buyPrise;
        try {
            PriseHistInfo pInfo;
            while (rSet.next()) {
                tracePrise = rSet.getFloat("dealprise");
                lockUpScore.addMonitorRecord(tracePrise);
                total_count ++;
                current_30pa_duration ++;
                if(tracePrise > buyPrise) {
                    positiveCount ++;
                    if((tracePrise - buyPrise)/buyPrise > 0.1) {
                        have10Pa = true;
                    }
                    if((tracePrise - buyPrise)/buyPrise > 0.2) {
                        have20Pa = true;
                    }
                    if((tracePrise - buyPrise)/buyPrise > 0.3) {
                        have30Pa = true;
                        if(current_30pa_duration > max_30pa_duration) max_30pa_duration = current_30pa_duration;
                        total_30pa_dur_count ++;
                        current_30pa_duration = 0;
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
                if(recordCount == 30 || recordCount == 90 || recordCount == 180 || recordCount == 270 || (recordCount % 360) == 0) {
                    System.out.println("id:" + stockid + " " + recordCount + " days 10%:" + have10Pa + " 20%:" + have20Pa + " 30%:" + have30Pa + " 40%:" + have40Pa  + " 50%:" + have50Pa +
                            " max earning:" + (maxPrise - buyPrise)/buyPrise + " totEarn:" + totalPositiveEarning/(positiveCount * buyPrise) + " totLoss: -" + totalNegativeEarning/(negativeCount*buyPrise));
                    pInfo = new PriseHistInfo();
                    pInfo.stockid = stockid;
                    pInfo.date = buyDate;
                    pInfo.duration = recordCount / 30;
                    pInfo.earn10pa = have10Pa;
                    pInfo.earn20pa = have20Pa;
                    pInfo.earn30pa = have30Pa;
                    pInfo.earn40pa = have40Pa;
                    pInfo.earn50pa = have50Pa;
                    pInfo.max_earning = (maxPrise - buyPrise)/buyPrise;
                    if(positiveCount > 0) {
                        pInfo.total_earning = totalPositiveEarning/positiveCount;
                    } else {
                        pInfo.total_earning = 0;
                    }
                    if(negativeCount > 0) {
                        pInfo.total_loss = totalNegativeEarning/negativeCount;
                    } else {
                        pInfo.total_loss = 0;
                    }
                    priseHistInfoList.add(pInfo);
                    pInfo.score = 0;
                    if(recordCount == 180) {
//                        if(have50Pa) pInfo.score = -0.1f;
                        if(have30Pa) pInfo.score = 0.1f;
                        if(!have10Pa) pInfo.score = 0.1f;
                    }
//                    sqlU.initInsertTable();
//                    sqlU.insertValue("stockid", stockid);
//                    sqlU.insertValue("date", buyDate);
//                    sqlU.insertValue("duration", recordCount/30);
//                    sqlU.insertValue("earn_10_pa", have10Pa);
//                    sqlU.insertValue("earn_20_pa", have20Pa);
//                    sqlU.insertValue("earn_30_pa", have30Pa);
//                    sqlU.insertValue("earn_40_pa", have40Pa);
//                    sqlU.insertValue("earn_50_pa", have50Pa);
//                    sqlU.insertValue("max_earning", (maxPrise - buyPrise)/buyPrise);
//                    if(positiveCount > 0) {
//                        sqlU.insertValue("total_earning", totalPositiveEarning/positiveCount);
//                    } else {
//                        sqlU.insertValue("total_earning", 0);
//                    }
//                    if(negativeCount > 0) {
//                        sqlU.insertValue("total_loss", totalNegativeEarning/negativeCount);
//                    } else {
//                        sqlU.insertValue("total_loss", 0);
//                    }
//                    tableName = sqlU.kPriseHistScoreTableName;
//                    sqlU.insertIntoTable(tableName);
                }
//                if(recordCount > 360) break;
            }
            finishSelectQuery();
//            System.out.println("Max 30% duration:" + max_30pa_duration + " average 30% duration:" + (total_30pa_dur_count == 0 ? 0:(total_count/total_30pa_dur_count)));
            lockupScore = lockUpScore.getLockUpScore();
            tableName = kPriseHistScoreTableName;
            for(int i = 0;i < priseHistInfoList.size(); i++) {
                pInfo = priseHistInfoList.get(i);
                if(checkIdExistInTable(tableName, pInfo.stockid)) {
                    initUpdateTable();
                    addUpdateCol("date", pInfo.date);
                    addUpdateCol("duration", pInfo.duration);
                    addUpdateCol("earn_10_pa", pInfo.earn10pa);
                    addUpdateCol("earn_20_pa", pInfo.earn20pa);
                    addUpdateCol("earn_30_pa", pInfo.earn30pa);
                    addUpdateCol("earn_40_pa", pInfo.earn40pa);
                    addUpdateCol("earn_50_pa", pInfo.earn50pa);
                    addUpdateCol("max_earning", pInfo.max_earning);
                    addUpdateCol("total_earning", pInfo.total_earning);
                    addUpdateCol("total_loss", pInfo.total_loss);
                    addUpdateCol("score", 0);
                    performUpdateTable(tableName);

                } else {
                    initInsertTable();
                    insertValue("stockid", pInfo.stockid);
                    insertValue("date", pInfo.date);
                    insertValue("duration", pInfo.duration);
                    insertValue("earn_10_pa", pInfo.earn10pa);
                    insertValue("earn_20_pa", pInfo.earn20pa);
                    insertValue("earn_30_pa", pInfo.earn30pa);
                    insertValue("earn_40_pa", pInfo.earn40pa);
                    insertValue("earn_50_pa", pInfo.earn50pa);
                    insertValue("max_earning", pInfo.max_earning);
                    insertValue("total_earning", pInfo.total_earning);
                    insertValue("total_loss", pInfo.total_loss);
                    insertValue("score", 0);
                    insertIntoTable(tableName);
                }
            }
            System.out.println("score log:" + Math.log10(lockupScore));
            return (float)(Math.log10(lockupScore)/10);
        } catch ( Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
