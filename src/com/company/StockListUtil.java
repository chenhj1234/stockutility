package com.company;

import sqlutil.StockSqlUtil;

import java.sql.*;
import java.util.ArrayList;

public class StockListUtil {
    private Connection mConnection = null;
    private Statement mStatement = null;
    private ResultSet mResultSet = null;
    private PreparedStatement mPreparedStatement = null;
    private String serverName = "10.20.71.108";
//    private String serverName = "192.168.1.90";
    private String mydatabase = "stock_identifier_alpha";
    private String tblStockId = "stockid";
    private String tblStockIdUpdate = "stockid_for_update";
    private String tblBuyinTableAnalysis = "buyin_table_analysis";
    private String url = "jdbc:mysql://" + serverName + "/" + mydatabase + "?serverTimezone=UTC&useUnicode=yes&characterEncoding=UTF-8";

    private String username = "chenhj";
    private String password = "holmas0228";
    private String table_stockid = mydatabase + "." + tblStockId;
    private String table_stockid_update = mydatabase + "." + tblStockIdUpdate;
    private String driverName = "com.mysql.cj.jdbc.Driver";
    private boolean DEBUG_MSG_VERBOSE = false;
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
        public String id;
        public String name;
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
    public void getStockListFromUpdatedList() {
        try {
            String tableName = table_stockid_update;
            connectToServer();
            mResultSet = mStatement.executeQuery("select * from " + tableName + " where length(stockid) <= 5");
            while (mResultSet.next()) {
                String id = mResultSet.getString("stockid");
                String name = mResultSet.getString("stockname");
                if(DEBUG_MSG_VERBOSE) System.out.println("Access id:" + id + " name:" + name);
                stockIdList.add(new StockIdEntry(id, name));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void getStockListFromBuyinAnalysis() {
        StockSqlUtil su = StockSqlUtil.getInstence();
        String tableName = su.buyinTableAnalysis;
        ResultSet resSet;
        su.initSelectTable();
        su.addSelCol("stockid");
        resSet = su.performSelectTable(tableName);
        try {
            while (resSet.next()) {
                String id = resSet.getString("stockid");
                stockIdList.add(new StockIdEntry(id, id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        su.finishSelectQuery();
    }
    public void getStockListFromBuyin(boolean withSellout) {
        StockSqlUtil su = StockSqlUtil.getInstence();
        String tableName = su.buyinTable;
        ResultSet resSet;
        su.initSelectTable();
        if(!withSellout) {
            su.addSelParmValue("sellday");
        }
        su.addSelCol("stockid");
        su.addSelGroup("stockid");
        resSet = su.performSelectTable(tableName);
        try {
            while (resSet.next()) {
                String id = resSet.getString("stockid");
                stockIdList.add(new StockIdEntry(id, id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        su.finishSelectQuery();
    }
    private int getIdInEntryList(ArrayList<StockIdEntry> ent, String id) {
        for(int i = 0;i < ent.size();i++) {
            if(ent.get(i).id.equals(id)) {
                return i;
            }
        }
        return -1;
    }
    private int getNameInEntryList(ArrayList<StockIdEntry> ent, String name) {
        for(int i = 0;i < ent.size();i++) {
            if(ent.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1;
    }
    public ArrayList<StockIdEntry> getIdFromName(ArrayList<String> nl, ArrayList<String> il) {
        int i,j;
        ArrayList<StockIdEntry> idEnt = new ArrayList<>();
        for(i = 0;i < nl.size(); i++) {
            j = getNameInEntryList(stockIdList, nl.get(i));
            if(j < 0) {
                System.out.println("Can not find the stock " + nl.get(i) + ", ignore");
                continue;
            }
            if(getNameInEntryList(idEnt, nl.get(i)) >= 0) {
                System.out.println("Duplicated stock " + nl.get(i) + ", ignore");
                continue;
            }
            idEnt.add(stockIdList.get(j));
        }
        for(i = 0;i < il.size(); i++) {
            j = getIdInEntryList(stockIdList, il.get(i));
            if(j < 0) {
                System.out.println("Can not find the stock " + il.get(i) + ", ignore");
                continue;
            }
            if(getIdInEntryList(idEnt, il.get(i)) >= 0) {
                System.out.println("Duplicated stock " + il.get(i) + ", ignore");
                continue;
            }
            idEnt.add(stockIdList.get(j));
        }
        return idEnt;
    }
    public String getStockNameFromId(String stockId) {
        String returnName = null;
        try {
            String tableName = table_stockid_update;
            connectToServer();
            mResultSet = mStatement.executeQuery("select * from " + tableName + " where length(stockid) = 4");
            while (mResultSet.next()) {
                String id = mResultSet.getString("stockid");
                if(id.equals(stockId)) {
                    returnName = mResultSet.getString("stockname");
                }
                if(DEBUG_MSG_VERBOSE) System.out.println("Access id:" + id + " name:" + returnName);
                stockIdList.add(new StockIdEntry(id, returnName));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return returnName;
    }
}
