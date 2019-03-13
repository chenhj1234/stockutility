package com.company;

import java.sql.*;
import java.util.ArrayList;

public class StockListUtil {
    private Connection mConnection = null;
    private Statement mStatement = null;
    private ResultSet mResultSet = null;
    private PreparedStatement mPreparedStatement = null;
    private String serverName = "10.20.70.136";
    private String mydatabase = "stock_identifier_alpha";
    private String tblStockId = "stockid";
    private String url = "jdbc:mysql://" + serverName + "/" + mydatabase + "?serverTimezone=UTC&useUnicode=yes&characterEncoding=UTF-8";

    private String username = "holmas";
    private String password = "chenhj";
    private String table_stockid = mydatabase + "." + tblStockId;
    private String driverName = "com.mysql.cj.jdbc.Driver";
    private String getUrl() {
        url = "jdbc:mysql://" + serverName + "/" + mydatabase + "?serverTimezone=UTC&useUnicode=yes&characterEncoding=UTF-8";
        return url;
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
        }
        return false;
    }
    private boolean connectToServer(String dbServer, String dbName, String dbUser, String dbPass) {
        serverName = dbServer;
        mydatabase = dbName;
        username = dbUser;
        password = dbPass;
        getUrl();
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
        }
        return false;
    }
    private boolean disConnectFromServer() {
        try {
            mStatement.close();
            mStatement = null;
            mConnection.close();
            mConnection = null;
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    public void printAllTableRow() {
        try {
            connectToServer();
            mResultSet = mStatement.executeQuery("select * from " + table_stockid);
            while (mResultSet.next()) {
                String id = mResultSet.getString("stockid");
                String name = mResultSet.getString("stockname");
                System.out.println("id:" + id + " name:" + name);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public class StockIdEntry {
        String id;
        String name;
        public StockIdEntry(String nId, String nName) {
            id = nId;
            name = nName;
        }
    };
    public ArrayList<StockIdEntry> stockIdList = new ArrayList<>();

    public void getStockList() {
        try {
            connectToServer();
            mResultSet = mStatement.executeQuery("select * from " + table_stockid);
            while (mResultSet.next()) {
                String id = mResultSet.getString("stockid");
                String name = mResultSet.getString("stockname");
                stockIdList.add(new StockIdEntry(id, name));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
