package sqlutil;

import com.company.StockListUtil;
import htmlparser.DividendGrammarParser;
import htmlparser.EarningGrammarDataListener;
import htmlparser.HTMLParserDividendListener;
import htmlparser.StackBasicInformation;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class StockSqlUtil {
    private boolean DEBUG_SQL_CMD = false;
    private boolean DEBUG_VERBOSE = false;
    private Connection mConnection = null;
    private Statement mStatement = null;
    private ResultSet mResultSet = null;
    private PreparedStatement mPreparedStatement = null;
    private String serverName = "10.20.70.136";
    private String mydatabase = "stock_identifier_alpha";
    private String tblStockId = "stockid";
    public String dailyInfoTable = "daily_info_table";
    public String monthlyEarnTable = "monthly_earn_table";
    public String annuallyInfoTable = "annual_info_table";
    public String annualDividendTable = "annual_share_table";
    public String seasonEarningInfoTable = "season_info_table";
    public String annualEarningShareTable = "annural_earning_share_table";
    public String seasonEarningShareTable = "season_earning_share_table";
    public String buyinTableAnalysis = "buyin_table_analysis";
    public String buyinTable = "buyin_table";
    private String url = "jdbc:mysql://" + serverName + "/" + mydatabase + "?serverTimezone=UTC&useUnicode=yes&characterEncoding=UTF-8";

    private String username = "holmas";
    private String password = "chenhj";
    private String table_stockid = mydatabase + "." + tblStockId;
    private String driverName = "com.mysql.cj.jdbc.Driver";

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
    public boolean insertDailyTable(String stockid, Date date, float buyin, float sellout, float dealprise, float shift, int amount) {
        String tableName = dailyInfoTable;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        //java.sql.Date date = new java.sql.Date();
        String dateString = "null";
        String timeString = "null";
        if(date == null || dealprise == -1) return false;
        dateString = "'" + dateFormat.format(date) + "'";
        timeString = "'" + timeFormat.format(date) + "'";
        if(checkIdExistInTable(tableName, stockid, dateFormat.format(date))) {
//            PreparedStatement stmt = mConnection.prepareStatement("update`user` set `exp` = '666'  where `username` = '"+loggedusername+"'");
            if(DEBUG_VERBOSE) System.out.println("id:" + stockid + " date:" + dateFormat.format(date) + " exist, try update");
            try {
                connectToServer();

                PreparedStatement stmt = mConnection.prepareStatement("UPDATE " + tableName +
                        " SET buyin = ?, sellout = ? , dealprise = ? , amount = ? , time = ? , returnratio = ? " +
                        "WHERE stockid = ? AND date = ?");
                stmt.setFloat(1, buyin);
                stmt.setFloat(2, sellout);
                stmt.setFloat(3, dealprise);
                stmt.setInt(4, amount);
                java.sql.Time sqlTime = java.sql.Time.valueOf(timeFormat.format(date));
                stmt.setTime(5, sqlTime);
                stmt.setFloat(6, 0);
                stmt.setString(7, stockid);
                java.sql.Date sqlDate = java.sql.Date.valueOf(dateFormat.format(date));
                stmt.setDate(8, sqlDate);
//                PreparedStatement stmt = mConnection.prepareStatement("UPDATE " + tableName +
//                        " SET buyin = " + buyin + ", sellout = ? , dealprise = ? , amount = ? , time = ? " +
//                        "WHERE stockid = ? AND date = ?");
                stmt.executeUpdate();
                stmt.close();
                if(DEBUG_VERBOSE) System.out.println("update success");
                if(mConnection != null) {
                    mConnection.close();
                    mConnection = null;
                }

            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("update fail");
            }
        } else {
            String query = "insert into " + tableName + " values ( '" + stockid + "', " +
                    buyin + ", " + sellout + ", " + dealprise + ", " + amount + ", " + dateString + ", " + timeString + ", 0" + ")";
            if(DEBUG_VERBOSE) System.out.println("id:" + stockid + " date:" + dateFormat.format(date) + " not exist, try insert");
            try {
                System.out.println(query);
                connectToServer();
                mStatement.executeUpdate(query);
                if (mConnection != null) {
                    mConnection.close();
                    mConnection = null;
                }
                if(DEBUG_VERBOSE) System.out.println("insert success");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("insert fail");
                System.exit(1);
            }
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
            try {
                connectToServer();

                PreparedStatement stmt = mConnection.prepareStatement("UPDATE " + tableName +
                        " SET roe = ?, roa = ? , stock_dividend = ? , cash_dividend = ? , " +
                        "exclusion_date = ? , elimination_date = ? , " +
                        "gross_profit_margin = ? , operating_profit_margin = ? ," +
                        "book_value_per_share = ? , earning_before_tax_margin = ?  " +
                        "WHERE stockid = ? AND year = ?");
                stmt.setFloat(1, roe);
                stmt.setFloat(2, roa);
                stmt.setFloat(3, stock_dividend);
                stmt.setFloat(4, cash_dividend);
                java.sql.Date sqlDate = null;
                if(exdate == null) {
                    stmt.setDate(5, null);
                } else {
                    sqlDate = java.sql.Date.valueOf(dateFormat.format(exdate));
                    stmt.setDate(5, sqlDate);
                }
                if(eldate == null) {
                    stmt.setDate(6, null);
                } else {
                    sqlDate = java.sql.Date.valueOf(dateFormat.format(eldate));
                    stmt.setDate(6, sqlDate);
                }
                stmt.setFloat(7, gross_profit_margin);
                stmt.setFloat(8, operating_profit_margin);
                stmt.setFloat(9, book_value_per_share);
                stmt.setFloat(10, earning_before_tax_margin);
                stmt.setString(11, stockid);
                stmt.setInt(12, year);
                stmt.executeUpdate();
                stmt.close();
                if(DEBUG_VERBOSE) System.out.println("update " + tableName + " success");
                if(mConnection != null) {
                    mConnection.close();
                    mConnection = null;
                }
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("update " + tableName + " fail");
            }
            return true;
        }
        if(DEBUG_VERBOSE) System.out.println("id:" + stockid + " year:" + year + " not exist, try insert");

        String query = "insert into " + tableName + " values ( '" + stockid + "', " +
                year + ", " + roe + ", " + roa + ", " + stock_dividend + ", " + cash_dividend +
                ", " + exDateString + ", " + elDateString + ", " +
                gross_profit_margin + ", " + operating_profit_margin + ", " +
                book_value_per_share + ", " + earning_before_tax_margin + ")";
        try {
            if(DEBUG_SQL_CMD) System.out.println(query);
            connectToServer();
            mStatement.executeUpdate(query);
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
            if(DEBUG_VERBOSE) System.out.println("insert " + tableName + " success");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insert " + tableName + " failed");
            System.exit(1);
        }
        return true;
    }

    public boolean insertAnnualDividendTable(String stockid, ArrayList<HTMLParserDividendListener.dividendRecord> aList) {
        String tableName = annualDividendTable;
        String query = null;
        for(int i = 0;i < aList.size();i++) {
            HTMLParserDividendListener.dividendRecord rec = aList.get(i);
            if(checkIdExistInTable(tableName, stockid, rec.year)) {
                if(DEBUG_VERBOSE) System.out.println("id:" + stockid + " year:" + rec.year + " exist, leave");
                continue;
            }
            if(DEBUG_VERBOSE) System.out.println("id:" + stockid + " year:" + rec.year + " not exist, try insert");
            query = "insert into " + tableName + " values ( '" + stockid + "', " +
                    rec.year + ", " +
                    rec.dividend[DividendGrammarParser.TYPE_INDEX_CASH] + ", " +
                    rec.dividend[DividendGrammarParser.TYPE_INDEX_EARN] + ", " +
                    rec.dividend[DividendGrammarParser.TYPE_INDEX_STOCK_CAP] + ", " +
                    rec.dividend[DividendGrammarParser.TYPE_INDEX_STOCK] + ", " +
                    rec.dividend[DividendGrammarParser.TYPE_INDEX_TOTAL] + ")";
            try {
                if(DEBUG_SQL_CMD) System.out.println(query);
                connectToServer();
                mStatement.executeUpdate(query);
                if(mConnection != null) {
                    mConnection.close();
                    mConnection = null;
                }
                if(DEBUG_VERBOSE) System.out.println("insert success");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("insert failed");
                System.exit(1);
            }
        }
        return true;
    }

    public boolean checkIdExistInTable(String tableName, String stockid) {
        String checkQuery = "";
            checkQuery = "select * from " + tableName +
                    " where stockid = '" + stockid + "'";
        boolean retval = false;
        try {
            connectToServer();
            mResultSet = mStatement.executeQuery(checkQuery);
            if(mResultSet.next()) {
                retval = true;
            }
            mResultSet.close();
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public boolean checkIdExistInTable(String tableName, String stockid, int year) {
        String checkQuery = "";
        checkQuery = "select * from " + tableName +
                " where stockid = '" + stockid + "' and year = " + year;
        boolean retval = false;
        try {
            connectToServer();
            mResultSet = mStatement.executeQuery(checkQuery);
            if(mResultSet.next()) {
                retval = true;
            }
            mResultSet.close();
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public boolean checkIdYearMonthExistInTable(String tableName, String stockid, int year, int month) {
        String checkQuery = "";
        checkQuery = "select * from " + tableName +
                " where stockid = '" + stockid + "' and year = " + year + " and month = " + month;
        boolean retval = false;
        try {
            connectToServer();
            mResultSet = mStatement.executeQuery(checkQuery);
            if(mResultSet.next()) {
                retval = true;
            }
            mResultSet.close();
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public boolean checkIdYearSeasonExistInTable(String tableName, String stockid, int year, int season) {
        String checkQuery = "";
        checkQuery = "select * from " + tableName +
                " where stockid = '" + stockid + "' and year = " + year + " and season = " + season;
        boolean retval = false;
        try {
            connectToServer();
            mResultSet = mStatement.executeQuery(checkQuery);
            if(mResultSet.next()) {
                retval = true;
            }
            mResultSet.close();
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
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
            connectToServer();
            mResultSet = mStatement.executeQuery(checkQuery);
            if(mResultSet.next()) {
                retval = true;
            }
            mResultSet.close();
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public boolean checkIdExistInTable(String tableName, String stockid, String colName, String colStr) {
        String checkQuery = "";
        checkQuery = "select * from " + tableName +
                " where stockid = '" + stockid + "' and " + colName + " = '" + colStr + "'";
        boolean retval = false;
        try {
            connectToServer();
            mResultSet = mStatement.executeQuery(checkQuery);
            if(mResultSet.next()) {
                retval = true;
            }
            mResultSet.close();
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }


    public void analysisReturnRatio(String stockid) {
        String checkQuery;
        String tableName = annualDividendTable;
        checkQuery = "select * from " + tableName +
                " where stockid = '" + stockid + "' and year = 106";
        float tdiv = 0, sdiv = 0, prz = 0;
        try {
            connectToServer();
            mResultSet = mStatement.executeQuery(checkQuery);
            if(mResultSet.next()) {
                tdiv = mResultSet.getFloat("total_dividend");
                sdiv = mResultSet.getFloat("stock_dividend");
                if(DEBUG_VERBOSE) System.out.println("tdiv:" + tdiv + " sdiv:" + sdiv);
            }
            mResultSet.close();
            tableName = dailyInfoTable;
            checkQuery = "select dealprise from " + tableName + " where stockid = " + stockid + " and date = CURDATE();";
            mResultSet = mStatement.executeQuery(checkQuery);
            if(mResultSet.next()) {
                prz = mResultSet.getFloat("dealprise");
                if(DEBUG_VERBOSE) System.out.println("prz:" + prz);
            }
            mResultSet.close();
            float ratio = 0;
            if(prz != 0) ratio = (tdiv * 100 )/prz;
            if(DEBUG_VERBOSE) System.out.println("ratio : " + ratio);
            PreparedStatement stmt = mConnection.prepareStatement("UPDATE " + tableName +
                    " SET returnratio = ?" +
                    " WHERE stockid = ? AND date = CURDATE()");
            stmt.setFloat(1, ratio);
            stmt.setString(2, stockid);
            int ret = stmt.executeUpdate();
            stmt.close();
            if(DEBUG_VERBOSE) System.out.println("update success, ret:" + ret);
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    java.sql.Date convertJavaDateToMySQL(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return java.sql.Date.valueOf(dateFormat.format(date));
    }

    String convertJavaDateToMySQLStr(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    Date convertStrToJavaDate(String dateStr) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    ArrayList<String> insertData = null;
    String insertTableName;
    ArrayList<String> initInsertTable() {
        insertData = new ArrayList<>();
        return insertData;
    }
    void insertNullValue(String name, ArrayList<String> aList) {
        String insertStr = "null";
        aList.add(insertStr);
    }
    void insertValue(String name, String value, ArrayList<String> aList) {
        String insertStr = "'" + value + "'";
        aList.add(insertStr);
    }
    void insertValue(String name, int value, ArrayList<String> aList) {
        String insertStr = "" + value;
        aList.add(insertStr);
    }
    void insertValue(String name, float value, ArrayList<String> aList) {
        String insertStr = "" + value;
        aList.add(insertStr);
    }
    void insertValue(String name, Date value, ArrayList<String> aList) {
        String insertStr = "'" + convertJavaDateToMySQLStr(value) + "'";
        aList.add(insertStr);
    }
    void insertValue(String name, boolean value, ArrayList<String> aList) {
        String insertStr = "'" + (value?1:0) + "'";
        aList.add(insertStr);
    }
    void insertIntoTable(String tab, ArrayList<String> aList) {
        insertTableName = tab;
        String insertValueStr = aList.get(0);

        for(int i = 1; i < aList.size(); i++) {
            insertValueStr += " , " + aList.get(i);
        }

        String query = "insert into " + insertTableName + " values ( " + insertValueStr + " )";
        if(DEBUG_SQL_CMD) System.out.println(query);
        try {
            connectToServer();
            mStatement.executeUpdate(query);
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
            if(DEBUG_VERBOSE) System.out.println("insert success");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insert failed");
            System.exit(1);
        }
    }
    Date convertMySQLDateToJava(java.sql.Date sqlDate) {
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
    float getReturnRatio(String stockid, Date date) {
        String tableName = dailyInfoTable;
        String dateStr = convertJavaDateToMySQLStr(date);
        String checkQuery = "select * from " + tableName + " where stockid = " + stockid + " and date = '" + dateStr + "';";
        float retr = 0;
        try {
            connectToServer();
            mResultSet = mStatement.executeQuery(checkQuery);
            if (mResultSet.next()) {
                mBuyinReturnRatio = retr = mResultSet.getFloat("returnratio");
                mBuyinPrise = mResultSet.getFloat("dealprise");
            }
            mResultSet.close();
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
            retr = -1;
        }
        return retr;
    }
    float ratio_threshold = 10;
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
            insertIntoTable("buyin_table", aList);
        } else {
            if(DEBUG_VERBOSE) System.out.println("stockid:" + stockid + " date:" + dateStr + " already bought");
        }
        return true;
    }
    public void applyStrategy(String stockid, int strategyIndex) {
        String dateStr = convertJavaDateToMySQLStr(new Date());
        float retr = getReturnRatio(stockid, convertStrToJavaDate(dateStr));
        if(retr > ratio_threshold) {
            if(DEBUG_VERBOSE) System.out.println("stockid:" + stockid + " rr:" + retr + " buyin, threshold:" + ratio_threshold);
            buyin(stockid, 1,dateStr,mBuyinPrise,1,"ratio > 10", mBuyinReturnRatio);
        }
        //System.out.println("stock id:" + stockid + " rr:" + retr);
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
                try {
                    connectToServer();
                    PreparedStatement stmt = mConnection.prepareStatement("UPDATE " + tableName +
                            " SET post_tax_surplus = ?, post_tax_increase_ratio = ? , pre_tax_surplus = ? , pre_tax_increase_ratio = ? " +
                            "WHERE stockid = ? AND year = ? AND season = ?");
                    stmt.setInt(1, postTaxInfo.earning);
                    stmt.setFloat(2, postTaxInfo.yearIncRatio);
                    stmt.setInt(3, preTaxInfo.earning);
                    stmt.setFloat(4, preTaxInfo.yearIncRatio);
                    stmt.setString(5, stockid);
                    stmt.setInt(6, annualYear);
                    stmt.setInt(7, preTaxInfo.season);
                    stmt.executeUpdate();
                    stmt.close();
                    if(DEBUG_VERBOSE) System.out.println("update " + tableName + " success");
                    if(mConnection != null) {
                        mConnection.close();
                        mConnection = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                if(DEBUG_VERBOSE) System.out.println("Id:" + stockid +
                        " year:" + rec.year +
                        " season:" + rec.season +
                        " exist in table:" + tableName + ", try update");
                connectToServer();
                try {
                    PreparedStatement stmt = mConnection.prepareStatement("UPDATE " + tableName +
                            " SET earn_per_share = ? " +
                            "WHERE stockid = ? AND year = ? AND season = ?");
                    stmt.setFloat(1, rec.share);
                    stmt.setString(2, stockid);
                    stmt.setInt(3, rec.year);
                    stmt.setInt(4, rec.season);
                    stmt.executeUpdate();
                    stmt.close();
                    if(DEBUG_VERBOSE) System.out.println("update " + tableName + " success");
                    if(mConnection != null) {
                        mConnection.close();
                        mConnection = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                if(DEBUG_VERBOSE) System.out.println("Id:" + stockid +
                        " year:" + rec.year +
                        " exist in table:" + tableName + ", try update");
                connectToServer();
                try {
                    PreparedStatement stmt = mConnection.prepareStatement("UPDATE " + tableName +
                            " SET earn_per_share = ? " +
                            "WHERE stockid = ? AND year = ?");
                    stmt.setFloat(1, rec.share);
                    stmt.setString(2, stockid);
                    stmt.setInt(3, rec.year);
                    stmt.executeUpdate();
                    stmt.close();
                    if(DEBUG_VERBOSE) System.out.println("update " + tableName + " success");
                    if(mConnection != null) {
                        mConnection.close();
                        mConnection = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        String checkQuery = "select dealprise,date from " +
                tableName +
                " where stockid = '" + stockid + "'";
        if(date == null) {
            checkQuery = checkQuery + " order by date desc";
        } else {
            checkQuery = checkQuery + " and date = '" + convertJavaDateToMySQLStr(date) + "'";
        }
        try {
            connectToServer();
            mResultSet = mStatement.executeQuery(checkQuery);
            if(mResultSet.next()) {
                mRetrievePrise = mResultSet.getFloat("dealprise");
                java.sql.Date sqlDate = mResultSet.getDate("date");
                mRetrieveDate = convertMySQLDateToJava(sqlDate);
                if(DEBUG_VERBOSE) System.out.println("mRetrievePrise:" + mRetrievePrise + " mRetrieveDate:" + mRetrieveDate);
            }
            mResultSet.close();
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mRetrievePrise;
    }
    class performanceEntry {
        float avgprise = 0;
        float perform = 0;
        int count = 0;
        float returnratio = 0;
        java.sql.Date buyinDate = null;
        java.sql.Date dealDate = null;
        float min = 0;
        float max = 0;
        float roe = 0;
        float roa = 0;
        float stockdiv = 0;
        float nv = 0;
        float season[] = new float[4];
        float year[] = new float[4];
        float div[] = new float[4];
    }

    ArrayList<String> colData = null;
    ArrayList<String> parmData = null;
    ArrayList<String> orderData = null;

    void initSelectTable() {
        colData = new ArrayList<>();
        parmData = new ArrayList<>();
        orderData = new ArrayList<>();
    }
    void addSelCol(String name) {
        colData.add(name);
    }
    void addOrder(String name, boolean inc) {
        if(inc) {
            orderData.add(name + " asc");
        } else {
            orderData.add(name + " desc");
        }
    }
    void addSelParmValue(String name, String value) {
        String insertStr = name + " = '" + value + "'";
        parmData.add(insertStr);
    }
    void addSelParmValue(String name, int value) {
        String insertStr = name + " = " + value;
        parmData.add(insertStr);
    }
    void addSelParmValue(String name, float value) {
        String insertStr = name + " = " + value;
        parmData.add(insertStr);
    }
    void addSelParmValue(String name, Date value) {
        String insertStr = name + " = '" + convertJavaDateToMySQLStr(value) + "'";
        parmData.add(insertStr);
    }
    void addSelParmValue(String name, boolean value) {
        String insertStr = name + " = '" + (value?1:0) + "'";
        parmData.add(insertStr);
    }
    ResultSet performSelectTable(String tab) {
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
        if(DEBUG_SQL_CMD) System.out.println(selColStr);
        try {
            connectToServer();
            mResultSet = mStatement.executeQuery(selColStr);
            return mResultSet;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insert failed");
            System.exit(1);
        }
        return null;
    }

    void finishSelectQuery() {
        try {
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
            if(mResultSet != null) {
                mResultSet.close();
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
        addOrder("year", false);
        addOrder("season", false);
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
        addOrder("year", false);
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
        addSelParmValue("stockid",stockid);
        addOrder("year", false);
        ResultSet resSet = performSelectTable(tableName);
        try {
            int i = 0;
            while (resSet.next()) {
                pe.div[i] = resSet.getFloat("total_dividend");
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
    public float getPerformance(String stockid) {
        String tableName = buyinTable;
        String checkQuery = "select * from " + tableName +
                " where stockid = '" + stockid + "'";
        float prz = getPrise(stockid, null);
        float min = 1000000, max = 0;
        float dealprz, totalPrise = 0, rr = 0;
        int count = 0;
        float total = 0;
        java.sql.Date buyDate = null, lastBuyDay = null;
        performanceEntry performEnt = new performanceEntry();
        try {
            connectToServer();
            mResultSet = mStatement.executeQuery(checkQuery);
            while(mResultSet.next()) {
                dealprz = mResultSet.getFloat("prise");
                buyDate = mResultSet.getDate("buyday");
                if((lastBuyDay == null) || lastBuyDay.before(buyDate)) {
                    lastBuyDay = buyDate;
                }
                rr = mResultSet.getFloat("returnratio");
                if(dealprz > max) max = dealprz;
                if(dealprz < min) min = dealprz;
                total += (prz - dealprz);
                count ++;
                totalPrise += dealprz;
            }
            mResultSet.close();
            if(count > 0) {
                performEnt.count = count;
                performEnt.perform = total / totalPrise;
                performEnt.avgprise = totalPrise / count;
                performEnt.buyinDate = lastBuyDay;
                performEnt.dealDate = null;
                performEnt.returnratio = rr;
                performEnt.min = min;
                performEnt.max = max;
            }
            if(DEBUG_VERBOSE) System.out.println("performance, total:" + total);
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getInfoForAnalysis(stockid, performEnt);
        getSeasonEarnForAnalysis(stockid, performEnt);
        getAnnualEarnForAnalysis(stockid, performEnt);
        getAnnualDividendForAnalysis(stockid, performEnt);
        if(performEnt.count != 0 && performEnt.perform != 0) {
            updatePerformance(stockid, performEnt);
            updateBuyinAnalysis(stockid, performEnt);
        }
        return total;
    }

    public void updatePerformance(String stockid, performanceEntry pe) {
        Date todayDate = new Date();
        if(!checkIdExistInTable("buyin_performance", stockid,"dealdate", convertJavaDateToMySQLStr(todayDate) )) {
            ArrayList<String> aList = initInsertTable();
            insertValue("stockid", stockid, aList);
            insertValue("averageprise", pe.avgprise, aList);
            insertValue("returnratio", pe.returnratio, aList);
            insertValue("performance", pe.perform, aList);
            insertValue("dealdate", /*convertJavaDateToMySQLStr(todayDate)*/ pe.buyinDate, aList);
            insertValue("sold", false, aList);
            insertValue("buyincount", pe.count, aList);
            insertIntoTable("buyin_performance", aList);
        } else {
            if(DEBUG_VERBOSE) System.out.println("updatePerformance Date exist : stockid:" + stockid + " date:" + todayDate);
        }
    }

    ArrayList<String> updateColData = null;
    ArrayList<String> updateParmData = null;

    void initUpdateTable() {
        updateColData = new ArrayList<>();
        updateParmData = new ArrayList<>();
    }
    void addUpdateCol(String name) {
        updateColData.add(name + " = null");
    }
    void addUpdateCol(String name, String value) {
        String insertStr = name + " = '" + value + "'";
        updateColData.add(insertStr);
    }
    void addUpdateCol(String name, int value) {
        String insertStr = name + " = " + value;
        updateColData.add(insertStr);
    }
    void addUpdateCol(String name, float value) {
        String insertStr = name + " = " + value;
        updateColData.add(insertStr);
    }
    void addUpdateCol(String name, Date value) {
        String insertStr = name + " = '" + convertJavaDateToMySQLStr(value) + "'";
        updateColData.add(insertStr);
    }
    void addUpdateCol(String name, boolean value) {
        String insertStr = name + " = '" + (value?1:0) + "'";
        updateColData.add(insertStr);
    }
    void addUpdateParam(String name) {
        updateParmData.add(name + " = null");
    }
    void addUpdateParam(String name, String value) {
        String insertStr = name + " = '" + value + "'";
        updateParmData.add(insertStr);
    }
    void addUpdateParam(String name, int value) {
        String insertStr = name + " = " + value;
        updateParmData.add(insertStr);
    }
    void addUpdateParam(String name, float value) {
        String insertStr = name + " = " + value;
        updateParmData.add(insertStr);
    }
    void addUpdateParam(String name, Date value) {
        String insertStr = name + " = '" + convertJavaDateToMySQLStr(value) + "'";
        updateParmData.add(insertStr);
    }
    void addUpdateParam(String name, boolean value) {
        String insertStr = name + " = '" + (value?1:0) + "'";
        updateParmData.add(insertStr);
    }
    ResultSet performUpdateTable(String tab) {
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
        if(DEBUG_SQL_CMD) System.out.println(updateColStr);
        connectToServer();
        try {
            PreparedStatement stmt = mConnection.prepareStatement(updateColStr);
            stmt.executeUpdate();
            stmt.close();
            if(DEBUG_VERBOSE) System.out.println("update " + tab + " success");
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    public void updateBuyinAnalysis(String stockid, performanceEntry pe) {
        String tableName= buyinTableAnalysis;
        Date todayDate = new Date();
        if(!checkIdExistInTable(tableName, stockid)) {
            ArrayList<String> aList = initInsertTable();
            insertValue("stockid", stockid, aList);
            insertValue("lastbuydate", pe.buyinDate, aList);
            insertValue("amount", pe.count, aList);
            insertValue("avg_prise", pe.avgprise, aList);
            insertValue("returnratio", pe.returnratio, aList);
            insertValue("min_prise", pe.min, aList);
            insertValue("max_prise", pe.max, aList);
            insertValue("performance", pe.perform, aList);
            insertValue("roe", pe.roe, aList);
            insertValue("roa", pe.roa, aList);
            insertValue("book_value", pe.nv, aList);
            insertValue("stock_div", pe.stockdiv, aList);
            for(int i = 0;i < 4;i++) {
                insertValue("season" + (i+1) + "_earn", pe.season[i], aList);
            }
            for(int i = 0;i < 4;i++) {
                insertValue("year" + (i+1) + "_earn", pe.year[i], aList);
            }
            for(int i = 0;i < 4;i++) {
                insertValue("year" + (i+1) + "_div", pe.div[i], aList);
            }
            insertIntoTable(tableName, aList);
        } else {
            if(DEBUG_VERBOSE) System.out.println("updateBuyinAnalysis Date exist : stockid:" + stockid + " table:" + tableName + " date:" + todayDate);
            initUpdateTable();
            addUpdateCol("lastbuydate", convertJavaDateToMySQLStr(todayDate));
            addUpdateCol("amount", pe.count);
            addUpdateCol("avg_prise", pe.avgprise);
            addUpdateCol("returnratio", pe.returnratio);
            addUpdateCol("min_prise", pe.min);
            addUpdateCol("max_prise", pe.max);
            addUpdateCol("performance", pe.perform);
            addUpdateCol("roe", pe.roe);
            addUpdateCol("roa", pe.roa);
            addUpdateCol("book_value", pe.nv);
            addUpdateCol("stock_div", pe.stockdiv);
            for(int i = 0;i < 4;i++) {
                addUpdateCol("season" + (i+1) + "_earn", pe.season[i]);
            }
            for(int i = 0;i < 4;i++) {
                addUpdateCol("year" + (i+1) + "_earn", pe.year[i]);
            }
            for(int i = 0;i < 4;i++) {
                addUpdateCol("year" + (i+1) + "_div", pe.div[i]);
            }
            addUpdateParam("stockid", stockid);
            performUpdateTable(tableName);
        }
    }
}
