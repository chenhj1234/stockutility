package sqlutil;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StockSqlUtil {
    private Connection mConnection = null;
    private Statement mStatement = null;
    private ResultSet mResultSet = null;
    private PreparedStatement mPreparedStatement = null;
    private String serverName = "10.20.240.102";
    private String mydatabase = "stock_identifier_alpha";
    private String tblStockId = "stockid";
    private String dailyInfoTable = "daily_info_table";
    private String monthlyInfoTable = "monthly_info_table";
    private String seasonlyInfoTable = "season_info_table";
    private String annuallyInfoTable = "annual_info_table";
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
        String query = "insert into " + tableName + " values ( '" + stockid + "', " +
                year + ", " + roe + ", " + roa + ", " + stock_dividend + ", " + cash_dividend +
                ", " + exDateString + ", " + elDateString + ", " +
                gross_profit_margin + ", " + operating_profit_margin + ", " +
                book_value_per_share + ", " + earning_before_tax_margin + ")";
        try {
            System.out.println(query);
            connectToServer();
            /*
            mPreparedStatement = mConnection.prepareStatement("insert into  " + tableName +
                    " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            mPreparedStatement.setString(1, stockid);
            mPreparedStatement.setInt(2, year);
            mPreparedStatement.setFloat(2, roe);
            mPreparedStatement.setFloat(2, roa);
            mPreparedStatement.setFloat(2, stock_dividend);
            mPreparedStatement.setFloat(2, cash_dividend);

            mPreparedStatement.setDate(2, exDateString);
            mPreparedStatement.setFloat(2, roe);

            mPreparedStatement.setFloat(2, roe);
            mPreparedStatement.setFloat(2, roe);
            mPreparedStatement.setFloat(2, roe);
            mPreparedStatement.setFloat(2, roe);
            mPreparedStatement.executeUpdate();
            */

            mStatement.executeUpdate(query);
            if(mConnection != null) {
                mConnection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return true;
    }
    private boolean checkIdExistInTable(String stockid, int year, int season, int month, Date date) {
        String tableName = "";
        String checkQuery = "";
        if (season != -1) {
            tableName = seasonlyInfoTable;
            checkQuery = "select * from " + tableName +
                    " where stockid = '" + stockid +
                    "' and year = " + year +
                    " and season = " + season;
        } else if(month != -1) {
            tableName = monthlyInfoTable;
            checkQuery = "select * from " + tableName +
                    " where stockid = '" + stockid +
                    "' and year = " + year +
                    " and month = " + month;
        } else if(date != null) {
            tableName = dailyInfoTable;
            checkQuery = "select * from " + tableName +
                    " where stockid = '" + stockid +
                    " and date = " + date;
        } else {
            tableName = annuallyInfoTable;
            checkQuery = "select * from " + tableName + " where stockid = '" + stockid + "' and year = " + year;
        }
        try {
            connectToServer();
            checkQuery = "select * from " + table_stockid + " where stockid = '" + stockid + "'";
            mResultSet = mStatement.executeQuery(checkQuery);
            if(mResultSet.next()) {
                return true;
            }
            mResultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
