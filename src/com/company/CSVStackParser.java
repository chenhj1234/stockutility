package com.company;


import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import sqlutil.StockSqlUtil;

import java.sql.*;
//import java.sql.*;

public class CSVStackParser {
    CSVLexer mCsvLexer = null;
    CSVParser mCsvParser = null;
    public CSVStackParserListener mCsvListener = new CSVStackParserListener();
    /* MySQL */
    private Connection mConnection = null;
    private Statement mStatement = null;
    private ResultSet mResultSet = null;
    private PreparedStatement mPreparedStatement = null;
    private String serverName = "10.20.240.102";
    private String mydatabase = "stock_identifier_alpha";
    private String tblStockId = "stockid";
    private String url = "jdbc:mysql://" + serverName + "/" + mydatabase + "?serverTimezone=UTC&useUnicode=yes&characterEncoding=UTF-8";

    private String username = "holmas";
    private String password = "chenhj";
    private String table_stockid = mydatabase + "." + tblStockId;
    private String driverName = "com.mysql.cj.jdbc.Driver";
    private boolean DEBUG_TOKEN = false;
    private boolean DEBUG_WRITE_DB = true;
    private boolean DEBUG_READ_DB = false;
    private boolean DEBUG_WRITE_DAILY_DB = true;
    private boolean DEBUG_PARSE_COMPANY_ONLY = true;
    private boolean DEBUG_WRITE_OTC_DAILY_DB = true;
    private boolean DEBUG_READ_DAILY_DB = false;
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
    private boolean checkIdExistInTable(String tableName, String stockid) {
        try {
            connectToServer();
            String checkQuery = "select * from " + table_stockid + " where stockid = '" + stockid + "'";
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
    public void resetInfoList() {
        mCsvListener.infoList = new StockInfoList();;
    }
    private boolean insertStockIntoIdTable(String stockid, String stockName, boolean checkDuplicate) {
        StockSqlUtil sutil = StockSqlUtil.getInstence();
        String tableName = sutil.getTblStockIdUpdate();
        if(!sutil.checkIdExistInTable(tableName,stockid)) {
            System.out.println("insert " + stockid + " " + stockName);
            sutil.initInsertTable();
            sutil.insertValue("stockid", stockid);
            sutil.insertValue("stockname", stockName);
            sutil.insertIntoTable(tableName);
        } else {
            System.out.println("data duplicat " + stockid + " " + stockName);
        }
//        try {
//            connectToServer();
//            if(checkDuplicate) {
//                if(checkIdExistInTable(table_stockid, stockid)) {
//                    return true;
//                }
//            }
//            mPreparedStatement = mConnection.prepareStatement("insert into  " + table_stockid + " values (?, ?)");
//            mPreparedStatement.setString(1, stockid);
//            mPreparedStatement.setString(2, stockName);
//            mPreparedStatement.executeUpdate();
//            if(mConnection != null) {
//                mConnection.close();
//                mConnection = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return false;
    }
    private void printAllTableRow() {
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

    public void StartBdd(){
        //insertStockIntoIdTable("1001","台泥", true);
        printAllTableRow();
    }

    public int parseCSVFile(String fileNmae) {
        CharStream strInput = null;
        try {
            strInput = CharStreams.fromFileName(fileNmae);
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
        mCsvLexer = new CSVLexer(strInput);

        while (DEBUG_TOKEN) {
            Token token = mCsvLexer.nextToken();
            if (token.getType() == Token.EOF) {
                break;
            }

            System.out.println("Token: ‘" + token.getText() + "’" + " index:" + token.getType());
        }
        if(DEBUG_TOKEN) mCsvLexer.reset();

        CommonTokenStream iTokens = new CommonTokenStream(mCsvLexer);
        mCsvParser = new CSVParser(iTokens);
        //System.out.println("+++" + mCsvParser.csfParagraph().getText());
        ParseTreeWalker walker = new ParseTreeWalker();
        //InfoStringGrammarDataListener infoListener = new InfoStringGrammarDataListener();
        //CSVParser.CsvFileContext csvf = mCsvParser.csfParagraph();
        //System.out.println(csvf.getText());
        walker.walk(mCsvListener, mCsvParser.csfParagraph());
        //mCsvListener.infoList.printOneEntry(0);
        //mCsvListener.infoList.printOneValue(0);
        return mCsvListener.infoList.getEntryCount();
    }

    public void insertStockId(StockInfoList sInfo) {
        String seString, senString;
        for(int i = 0;i < sInfo.stockInfoList.size();i++) {
            StockInfoList.StockEntry se = sInfo.stockInfoList.get(i);
            seString = se.stockEntry.get(0);
            senString = se.stockEntry.get(1);
            if(!seString.matches("\\d+")) {
                continue;
            }
            if(DEBUG_WRITE_DB) insertStockIntoIdTable(seString, senString, true);
        }
        if(DEBUG_READ_DB) StartBdd();
    }

    public final int TWSE_MI_INDEX_COL_BUYIN = 11;
    public final int TWSE_MI_INDEX_COL_SELLOUT = 13;
    public final int TWSE_MI_INDEX_COL_DEALPRIZE = 8;
    public final int TWSE_MI_INDEX_COL_AMOUNT = 2;

    public void insertStockDailyInfo(StockInfoList sInfo, java.util.Date date, String targetTableName) {
        StockSqlUtil sqlU = StockSqlUtil.getInstence();
        String seString, senString;
        float buyinprz, selloutprz, dealprz;
        int amount;
        String timeStr = "01:30:00";
        String przStr, buyinStr, selloutStr, dealStr;
        for(int i = 0;i < sInfo.stockInfoList.size();i++) {
            StockInfoList.StockEntry se = sInfo.stockInfoList.get(i);
            seString = se.stockEntry.get(0);
            if(DEBUG_PARSE_COMPANY_ONLY && (seString.length() > 4)) {
                continue;
            }
            buyinStr = se.stockEntry.get(TWSE_MI_INDEX_COL_BUYIN).replace(",","");
            selloutStr = se.stockEntry.get(TWSE_MI_INDEX_COL_SELLOUT).replace(",","");
            dealStr = se.stockEntry.get(TWSE_MI_INDEX_COL_DEALPRIZE).replace(",","");
            przStr = seString + " " + buyinStr + " " + selloutStr + " " + dealStr + " " + se.stockEntry.get(TWSE_MI_INDEX_COL_AMOUNT);
            System.out.println(przStr);
            if(buyinStr.equals("--") ||buyinStr.equals("-")) {
                if(dealStr.equals("--") || dealStr.equals("-")) {
                    if(selloutStr.equals("--") || selloutStr.equals("-")) {
                        dealStr = buyinStr = selloutStr = "-1.0";
                    } else {
                        buyinStr = selloutStr;
                    }
                } else {
                    buyinStr = dealStr;
                }
            }
            if(selloutStr.equals("--") || selloutStr.equals("-")) {
                if(dealStr.equals("--") || dealStr.equals("-")) {
                    if(buyinStr.equals("--") || buyinStr.equals("-")) {
                        dealStr = buyinStr = selloutStr = "-1.0";
                    } else {
                        selloutStr = buyinStr;
                    }
                } else {
                    selloutStr = dealStr;
                }
            }
            if(dealStr.equals("--") || dealStr.equals("-")) {
                if(selloutStr.equals("--") || selloutStr.equals("-")) {
                    if(buyinStr.equals("--") || buyinStr.equals("-")) {
                        dealStr = buyinStr = selloutStr = "-1.0";
                    } else {
                        dealStr = buyinStr;
                    }
                } else {
                    dealStr = selloutStr;
                }
            }
            przStr = seString + " fixed " + buyinStr + " " + selloutStr + " " + dealStr + " " + se.stockEntry.get(TWSE_MI_INDEX_COL_AMOUNT);
            System.out.println(przStr);

            buyinprz = Float.parseFloat(buyinStr);
            selloutprz = Float.parseFloat(selloutStr);
            dealprz = Float.parseFloat(dealStr);

            amount = Integer.parseInt(se.stockEntry.get(TWSE_MI_INDEX_COL_AMOUNT).replace(",",""));
            if(!seString.matches("\\d+")) {
                continue;
            }
            if(DEBUG_WRITE_DAILY_DB) {
                if(targetTableName != null && !targetTableName.equals("")) {
                    sqlU.insertDailyTable(seString, date, buyinprz, selloutprz, dealprz, 0, amount, false, targetTableName);
                } else {
                    sqlU.insertDailyTable(seString, date, buyinprz, selloutprz, dealprz, 0, amount, false);
                }
            }
        }
    }
    public void insertStockDailyInfo(StockInfoList sInfo, java.util.Date date) {
        StockSqlUtil sqlU = StockSqlUtil.getInstence();
        String seString, senString;
        float buyinprz, selloutprz, dealprz;
        int amount;
        String timeStr = "01:30:00";
        String przStr, buyinStr, selloutStr, dealStr;
        for(int i = 0;i < sInfo.stockInfoList.size();i++) {
            StockInfoList.StockEntry se = sInfo.stockInfoList.get(i);
            seString = se.stockEntry.get(0);
            if(DEBUG_PARSE_COMPANY_ONLY && (seString.length() > 4)) {
                continue;
            }
            buyinStr = se.stockEntry.get(TWSE_MI_INDEX_COL_BUYIN).replace(",","");
            selloutStr = se.stockEntry.get(TWSE_MI_INDEX_COL_SELLOUT).replace(",","");
            dealStr = se.stockEntry.get(TWSE_MI_INDEX_COL_DEALPRIZE).replace(",","");
            przStr = seString + " " + buyinStr + " " + selloutStr + " " + dealStr + " " + se.stockEntry.get(TWSE_MI_INDEX_COL_AMOUNT);
            System.out.println(przStr);
            if(buyinStr.equals("--") ||buyinStr.equals("-")) {
                if(dealStr.equals("--") || dealStr.equals("-")) {
                    if(selloutStr.equals("--") || selloutStr.equals("-")) {
                        dealStr = buyinStr = selloutStr = "-1.0";
                    } else {
                        buyinStr = selloutStr;
                    }
                } else {
                    buyinStr = dealStr;
                }
            }
            if(selloutStr.equals("--") || selloutStr.equals("-")) {
                if(dealStr.equals("--") || dealStr.equals("-")) {
                    if(buyinStr.equals("--") || buyinStr.equals("-")) {
                        dealStr = buyinStr = selloutStr = "-1.0";
                    } else {
                        selloutStr = buyinStr;
                    }
                } else {
                    selloutStr = dealStr;
                }
            }
            if(dealStr.equals("--") || dealStr.equals("-")) {
                if(selloutStr.equals("--") || selloutStr.equals("-")) {
                    if(buyinStr.equals("--") || buyinStr.equals("-")) {
                        dealStr = buyinStr = selloutStr = "-1.0";
                    } else {
                        dealStr = buyinStr;
                    }
                } else {
                    dealStr = selloutStr;
                }
            }
            przStr = seString + " fixed " + buyinStr + " " + selloutStr + " " + dealStr + " " + se.stockEntry.get(TWSE_MI_INDEX_COL_AMOUNT);
            System.out.println(przStr);

            buyinprz = Float.parseFloat(buyinStr);
            selloutprz = Float.parseFloat(selloutStr);
            dealprz = Float.parseFloat(dealStr);

            amount = Integer.parseInt(se.stockEntry.get(TWSE_MI_INDEX_COL_AMOUNT).replace(",",""));
            if(!seString.matches("\\d+")) {
                continue;
            }
            if(DEBUG_WRITE_DAILY_DB) sqlU.insertDailyTable(seString, date, buyinprz, selloutprz, dealprz, 0, amount, false);
        }
    }

    public final int TPEX_INDEX_COL_BUYIN = 10;
    public final int TPEX_INDEX_COL_SELLOUT = 12;
    public final int TPEX_INDEX_COL_DEALPRIZE = 2;
    public final int TPEX_INDEX_COL_AMOUNT = 7;

    public void insertOTCStockDailyInfo(StockInfoList sInfo, java.util.Date date, String dailyTargetTableName) {
        StockSqlUtil sqlU = StockSqlUtil.getInstence();
        String seString, senString;
        float buyinprz, selloutprz, dealprz;
        int amount;
        String timeStr = "02:30:00";
        String przStr, buyinStr, selloutStr, dealStr;
        for(int i = 0;i < sInfo.stockInfoList.size();i++) {
            StockInfoList.StockEntry se = sInfo.stockInfoList.get(i);
            seString = se.stockEntry.get(0);
            if(DEBUG_PARSE_COMPANY_ONLY && (seString.length() > 4)) {
                continue;
            }
            buyinStr = se.stockEntry.get(TPEX_INDEX_COL_BUYIN).replace(",","");
            selloutStr = se.stockEntry.get(TPEX_INDEX_COL_SELLOUT).replace(",","");
            dealStr = se.stockEntry.get(TPEX_INDEX_COL_DEALPRIZE).replace(",","");
            przStr = seString + " " + buyinStr + " " + selloutStr + " " + dealStr + " " + se.stockEntry.get(TPEX_INDEX_COL_AMOUNT);
            System.out.println(przStr);
            if(buyinStr.contains("--") || buyinStr.equals("-")) {
                if(dealStr.contains("--") || dealStr.equals("-")) {
                    if(selloutStr.contains("--") || selloutStr.equals("-")) {
                        dealStr = buyinStr = selloutStr = "-1.0";
                    } else {
                        buyinStr = selloutStr;
                    }
                } else {
                    buyinStr = dealStr;
                }
            }
            if(selloutStr.contains("--") || selloutStr.equals("-")) {
                if(dealStr.contains("--") || dealStr.equals("-")) {
                    if(buyinStr.contains("--") || buyinStr.equals("-")) {
                        dealStr = buyinStr = selloutStr = "-1.0";
                    } else {
                        selloutStr = buyinStr;
                    }
                } else {
                    selloutStr = dealStr;
                }
            }
            if(dealStr.contains("--") || dealStr.equals("-")) {
                if(selloutStr.contains("--") || selloutStr.equals("-")) {
                    if(buyinStr.contains("--") || buyinStr.equals("-")) {
                        dealStr = buyinStr = selloutStr = "-1.0";
                    } else {
                        dealStr = buyinStr;
                    }
                } else {
                    dealStr = selloutStr;
                }
            }
            przStr = seString + " fixed " + buyinStr + " " + selloutStr + " " + dealStr + " " + se.stockEntry.get(TPEX_INDEX_COL_AMOUNT);
            System.out.println(przStr);

            buyinprz = Float.parseFloat(buyinStr);
            selloutprz = Float.parseFloat(selloutStr);
            dealprz = Float.parseFloat(dealStr);

            amount = Integer.parseInt(se.stockEntry.get(TPEX_INDEX_COL_AMOUNT).replace(",",""));
            if(!seString.matches("\\d+")) {
                continue;
            }
            if(DEBUG_WRITE_OTC_DAILY_DB) {
                if(dailyTargetTableName != null && !dailyTargetTableName.equals("")) {
                    sqlU.insertDailyTable(seString, date, buyinprz, selloutprz, dealprz, 0, amount, false, dailyTargetTableName);
                } else {
                    sqlU.insertDailyTable(seString, date, buyinprz, selloutprz, dealprz, 0, amount, false);
                }
            }
        }
    }
    public void insertOTCStockDailyInfo(StockInfoList sInfo, java.util.Date date) {
        insertOTCStockDailyInfo(sInfo, date, null);
//        StockSqlUtil sqlU = StockSqlUtil.getInstence();
//        String seString, senString;
//        float buyinprz, selloutprz, dealprz;
//        int amount;
//        String timeStr = "02:30:00";
//        String przStr, buyinStr, selloutStr, dealStr;
//        for(int i = 0;i < sInfo.stockInfoList.size();i++) {
//            StockInfoList.StockEntry se = sInfo.stockInfoList.get(i);
//            seString = se.stockEntry.get(0);
//            if(DEBUG_PARSE_COMPANY_ONLY && (seString.length() > 4)) {
//                continue;
//            }
//            buyinStr = se.stockEntry.get(TPEX_INDEX_COL_BUYIN).replace(",","");
//            selloutStr = se.stockEntry.get(TPEX_INDEX_COL_SELLOUT).replace(",","");
//            dealStr = se.stockEntry.get(TPEX_INDEX_COL_DEALPRIZE).replace(",","");
//            przStr = seString + " " + buyinStr + " " + selloutStr + " " + dealStr + " " + se.stockEntry.get(TPEX_INDEX_COL_AMOUNT);
//            System.out.println(przStr);
//            if(buyinStr.contains("--") || buyinStr.equals("-")) {
//                if(dealStr.contains("--") || dealStr.equals("-")) {
//                    if(selloutStr.contains("--") || selloutStr.equals("-")) {
//                        dealStr = buyinStr = selloutStr = "-1.0";
//                    } else {
//                        buyinStr = selloutStr;
//                    }
//                } else {
//                    buyinStr = dealStr;
//                }
//            }
//            if(selloutStr.contains("--") || selloutStr.equals("-")) {
//                if(dealStr.contains("--") || dealStr.equals("-")) {
//                    if(buyinStr.contains("--") || buyinStr.equals("-")) {
//                        dealStr = buyinStr = selloutStr = "-1.0";
//                    } else {
//                        selloutStr = buyinStr;
//                    }
//                } else {
//                    selloutStr = dealStr;
//                }
//            }
//            if(dealStr.contains("--") || dealStr.equals("-")) {
//                if(selloutStr.contains("--") || selloutStr.equals("-")) {
//                    if(buyinStr.contains("--") || buyinStr.equals("-")) {
//                        dealStr = buyinStr = selloutStr = "-1.0";
//                    } else {
//                        dealStr = buyinStr;
//                    }
//                } else {
//                    dealStr = selloutStr;
//                }
//            }
//            przStr = seString + " fixed " + buyinStr + " " + selloutStr + " " + dealStr + " " + se.stockEntry.get(TPEX_INDEX_COL_AMOUNT);
//            System.out.println(przStr);
//
//            buyinprz = Float.parseFloat(buyinStr);
//            selloutprz = Float.parseFloat(selloutStr);
//            dealprz = Float.parseFloat(dealStr);
//
//            amount = Integer.parseInt(se.stockEntry.get(TPEX_INDEX_COL_AMOUNT).replace(",",""));
//            if(!seString.matches("\\d+")) {
//                continue;
//            }
//            if(DEBUG_WRITE_OTC_DAILY_DB) sqlU.insertDailyTable(seString, date, buyinprz, selloutprz, dealprz, 0, amount, false);
//        }
    }

    public int parseCSVRow(String csvstr) {
        CharStream strInput;
        strInput = CharStreams.fromString(csvstr);
        mCsvLexer = new CSVLexer(strInput);
        /*
        while (true) {
            token = mCsvLexer.nextToken();
            if (token.getType() == Token.EOF) {
                break;
            }

            System.out.println("Token: ‘" + token.getText() + "’" + " index:" + token.getType());
        }
        */
        CommonTokenStream iTokens = new CommonTokenStream(mCsvLexer);
        mCsvParser = new CSVParser(iTokens);
        //System.out.println("+++" + mCsvParser.csfParagraph().getText());
        ParseTreeWalker walker = new ParseTreeWalker();
        //InfoStringGrammarDataListener infoListener = new InfoStringGrammarDataListener();
        //CSVParser.CsvFileContext csvf = mCsvParser.csfParagraph();
        //System.out.println(csvf.getText());
        walker.walk(mCsvListener, mCsvParser.csfParagraph());
        //mCsvListener.infoList.printOneEntry(0);
        //mCsvListener.infoList.printOneValue(0);
        insertStockId(mCsvListener.infoList);
        /*
        String seString, senString;
        for(int i = 0;i < mCsvListener.infoList.stockInfoList.size();i++) {
            StockInfoList.StockEntry se = mCsvListener.infoList.stockInfoList.get(i);
            seString = se.stockEntry.get(0);
            senString = se.stockEntry.get(1);
            insertStockIntoIdTable(seString, senString, true);
        }
        */
        return 1;
    }
}
