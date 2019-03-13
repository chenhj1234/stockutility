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
    private Connection mConnection = null;
    private Statement mStatement = null;
    private ResultSet mResultSet = null;
    private PreparedStatement mPreparedStatement = null;
    private String serverName = "10.20.70.136";
    private String mydatabase = "stock_identifier_alpha";
    private String tblStockId = "stockid";
    public String dailyInfoTable = "daily_info_table";
    public String monthlyInfoTable = "monthly_info_table";
    public String annuallyInfoTable = "annual_info_table";
    public String annualDividendTable = "annual_share_table";
    public String seasonEarningInfoTable = "season_info_table";
    public String annualEarningShareTable = "annural_earning_share_table";
    public String seasonEarningShareTable = "season_earning_share_table";
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
            System.out.println("id:" + stockid + " date:" + dateFormat.format(date) + " exist, try update");
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
                System.out.println("update success");

            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("update fail");
            }
        } else {
            String query = "insert into " + tableName + " values ( '" + stockid + "', " +
                    buyin + ", " + sellout + ", " + dealprise + ", " + amount + ", " + dateString + ", " + timeString + ", 0" + ")";
            System.out.println("id:" + stockid + " date:" + dateFormat.format(date) + " not exist, try insert");
            try {
                System.out.println(query);
                connectToServer();
                mStatement.executeUpdate(query);
                if (mConnection != null) {
                    mConnection.close();
                    mConnection = null;
                }
                System.out.println("insert success");
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
            System.out.println("id:" + stockid + " year:" + year + " exist, try update");
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
                System.out.println("update success");

            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("update fail");
            }
            return true;
        }
        System.out.println("id:" + stockid + " year:" + year + " not exist, try insert");

        String query = "insert into " + tableName + " values ( '" + stockid + "', " +
                year + ", " + roe + ", " + roa + ", " + stock_dividend + ", " + cash_dividend +
                ", " + exDateString + ", " + elDateString + ", " +
                gross_profit_margin + ", " + operating_profit_margin + ", " +
                book_value_per_share + ", " + earning_before_tax_margin + ")";
        try {
            System.out.println(query);
            connectToServer();
            mStatement.executeUpdate(query);
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
            System.out.println("insert success");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insert failed");
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
                System.out.println("id:" + stockid + " year:" + rec.year + " exist, leave");
                continue;
            }
            System.out.println("id:" + stockid + " year:" + rec.year + " not exist, try insert");
            query = "insert into " + tableName + " values ( '" + stockid + "', " +
                    rec.year + ", " +
                    rec.dividend[DividendGrammarParser.TYPE_INDEX_CASH] + ", " +
                    rec.dividend[DividendGrammarParser.TYPE_INDEX_EARN] + ", " +
                    rec.dividend[DividendGrammarParser.TYPE_INDEX_STOCK_CAP] + ", " +
                    rec.dividend[DividendGrammarParser.TYPE_INDEX_STOCK] + ", " +
                    rec.dividend[DividendGrammarParser.TYPE_INDEX_TOTAL] + ")";
            try {
                System.out.println(query);
                connectToServer();
                mStatement.executeUpdate(query);
                if(mConnection != null) {
                    mConnection.close();
                    mConnection = null;
                }
                System.out.println("insert success");
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
        System.out.println("query:" + checkQuery);
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
                System.out.println("tdiv:" + tdiv + " sdiv:" + sdiv);
            }
            mResultSet.close();
            tableName = dailyInfoTable;
            checkQuery = "select dealprise from " + tableName + " where stockid = " + stockid + " and date = CURDATE();";
            mResultSet = mStatement.executeQuery(checkQuery);
            if(mResultSet.next()) {
                prz = mResultSet.getFloat("dealprise");
                System.out.println("prz:" + prz);
            }
            mResultSet.close();
            float ratio = 0;
            if(prz != 0) ratio = (tdiv * 100 )/prz;
            System.out.println("ratio : " + ratio);
            PreparedStatement stmt = mConnection.prepareStatement("UPDATE " + tableName +
                    " SET returnratio = ?" +
                    " WHERE stockid = ? AND date = CURDATE()");
            stmt.setFloat(1, ratio);
            stmt.setString(2, stockid);
            int ret = stmt.executeUpdate();
            stmt.close();
            System.out.println("update success, ret:" + ret);
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
        System.out.println(query);
        try {
            System.out.println(query);
            connectToServer();
            mStatement.executeUpdate(query);
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
            System.out.println("insert success");
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
            System.out.println("stockid:" + stockid + " date:" + dateStr + " already bought");
        }
        return true;
    }
    public void applyStrategy(String stockid, int strategyIndex) {
        String dateStr = convertJavaDateToMySQLStr(new Date());
        float retr = getReturnRatio(stockid, convertStrToJavaDate(dateStr));
        if(retr > ratio_threshold) {
            System.out.println("stockid:" + stockid + " rr:" + retr + " buyin, threshold:" + ratio_threshold);
            buyin(stockid, 1,dateStr,mBuyinPrise,1,"ratio > 10", mBuyinReturnRatio);
        }
        //System.out.println("stock id:" + stockid + " rr:" + retr);
    }

    public boolean insertSeasonEarningTable(String stockid, int annualYear,
                                            ArrayList<EarningGrammarDataListener.seasonInfo> seasonPostTaxEarning,
                                            ArrayList<EarningGrammarDataListener.seasonInfo> seasonPreTaxEarning) {
        if(seasonPostTaxEarning.size() != seasonPreTaxEarning.size()) {
            System.out.println("Season post tax record count:" + seasonPostTaxEarning.size() +
            " is not equal to season pre tax record count:" + seasonPreTaxEarning.size());
            return false;
        }
        for(int i = 0;i < seasonPostTaxEarning.size(); i++) {
            EarningGrammarDataListener.seasonInfo preTaxInfo = seasonPreTaxEarning.get(i);
            EarningGrammarDataListener.seasonInfo postTaxInfo = seasonPostTaxEarning.get(i);
            if((preTaxInfo.earning == 0) && (preTaxInfo.yearIncRatio == 0)) {
                System.out.println("Year:" + annualYear + " season:" + preTaxInfo.season + " record invalid");
                continue;
            }
            if(checkIdYearSeasonExistInTable(seasonEarningInfoTable, stockid, annualYear, preTaxInfo.season)) {
                System.out.println("Id:" + stockid +
                        " year:" + annualYear +
                        " season:" + preTaxInfo.season +
                        " exist in table:" + seasonEarningInfoTable);
                continue;
            }
            ArrayList<String> aList = initInsertTable();
            insertValue("stockid", stockid, aList);
            insertValue("year", annualYear, aList);
            insertValue("season", preTaxInfo.season, aList);
            insertValue("post_tax_surplus", postTaxInfo.earning, aList);
            insertValue("post_tax_increase_ratio", postTaxInfo.yearIncRatio, aList);
            insertValue("pre_tax_surplus", preTaxInfo.earning, aList);
            insertValue("pre_tax_increase_ratio", preTaxInfo.yearIncRatio, aList);
            insertIntoTable(seasonEarningInfoTable, aList);
        }

        return true;
    }

    public boolean insertSeasonShareTable(String stockid,
                                          ArrayList<StackBasicInformation.seasonRecord> recList) {
        for(int i = 0;i < recList.size(); i++) {
            StackBasicInformation.seasonRecord rec = recList.get(i);
            if(checkIdYearSeasonExistInTable(seasonEarningShareTable, stockid, rec.year, rec.season)) {
                System.out.println("Id:" + stockid +
                        " year:" + rec.year +
                        " season:" + rec.season +
                        " exist in table:" + seasonEarningShareTable);
                continue;
            }
            ArrayList<String> aList = initInsertTable();
            insertValue("stockid", stockid, aList);
            insertValue("year", rec.year, aList);
            insertValue("season", rec.season, aList);
            insertValue("earn_per_share", rec.share, aList);
            insertIntoTable(seasonEarningShareTable, aList);
        }

        return true;
    }
    public boolean insertAnnualShareTable(String stockid,
                                          ArrayList<StackBasicInformation.seasonRecord> recList) {
        for(int i = 0;i < recList.size(); i++) {
            StackBasicInformation.seasonRecord rec = recList.get(i);
            if(checkIdExistInTable(annualEarningShareTable, stockid, rec.year)) {
                System.out.println("Id:" + stockid +
                        " year:" + rec.year +
                        " exist in table:" + annualEarningShareTable);
                continue;
            }
            ArrayList<String> aList = initInsertTable();
            insertValue("stockid", stockid, aList);
            insertValue("year", rec.year, aList);
            insertValue("earn_per_share", rec.share, aList);
            insertIntoTable(annualEarningShareTable, aList);
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
                System.out.println("mRetrievePrise:" + mRetrievePrise + " mRetrieveDate:" + mRetrieveDate);
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
    }
    public float getPerformance(String stockid) {
        String tableName = buyinTable;
        String checkQuery = "select * from " + tableName +
                " where stockid = '" + stockid + "'";
        float prz = getPrise(stockid, null);
        float dealprz, totalPrise = 0, rr = 0;
        int count = 0;
        float total = 0;
        java.sql.Date buyDate = null;
        performanceEntry performEnt = new performanceEntry();
        try {
            connectToServer();
            mResultSet = mStatement.executeQuery(checkQuery);
            while(mResultSet.next()) {
                dealprz = mResultSet.getFloat("prise");
                buyDate = mResultSet.getDate("buyday");
                rr = mResultSet.getFloat("returnratio");
                System.out.println("get prise:" + dealprz + " date:" + buyDate);
                total += (prz - dealprz);
                count ++;
                totalPrise += dealprz;
            }
            mResultSet.close();
            if(count > 0) {
                performEnt.count = count;
                performEnt.perform = total;
                performEnt.avgprise = totalPrise / count;
                performEnt.buyinDate = buyDate;
                performEnt.dealDate = null;
                performEnt.returnratio = rr;
            }
            System.out.println("performance, total:" + total);
            if(mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(performEnt.count != 0 && performEnt.perform != 0) {
            updatePerformance(stockid, performEnt);
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
            insertValue("dealdate", convertJavaDateToMySQLStr(todayDate), aList);
            insertValue("sold", false, aList);
            insertValue("buyincount", pe.count, aList);
            insertIntoTable("buyin_performance", aList);
        } else {
            System.out.println("Date exist : stockid" + stockid + " date:" + todayDate);
        }
    }
}
