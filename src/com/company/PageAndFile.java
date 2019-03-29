package com.company;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PageAndFile {
    final String basicInfoUrl = "https://tw.stock.yahoo.com/d/s/company_STOCK_ID.html";
    final String dailyPriseUrl = "https://tw.stock.yahoo.com/q/ts?s=STOCK_ID";
    final String annualDividendUtl = "https://tw.stock.yahoo.com/d/s/dividend_STOCK_ID.html";
    final String earningUtl = "https://tw.stock.yahoo.com/d/s/earning_STOCK_ID.html";
    public static final String currencyPage = "https://rate.bot.com.tw/xrt/flcsv/0/day";
    final String savedFilePath = "./saved_pages";
    String dateStr = "";
    BufferedReader openURLForRead(String url) {
        try {
            URL siteurl = new URL(url);
            HttpURLConnection yc = (HttpURLConnection)siteurl.openConnection();
            yc.connect();
            int code = yc.getResponseCode();
            System.out.println("Response code:" + code);
            if(code >= 300) {
                yc.disconnect();
                return null;
            }
            //InputStream ins = oracle.openStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream(), "Big5-HKSCS"));
            return in;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    BufferedWriter openFileForWrite(String fileName) {
        BufferedWriter out = null;
        try
        {
            FileWriter fstream = new FileWriter(fileName, false); //true tells to append data.
            out = new BufferedWriter(fstream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return out;
    }
    public boolean writeURLToFile(String fileName, String url) {
        try {
            BufferedReader in = openURLForRead(url);
            if(in == null) return false;
            BufferedWriter fileOut = openFileForWrite(fileName);
            if(fileOut == null) {
                in.close();
                return false;
            }
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                fileOut.write(inputLine);
                //System.out.println(inputLine);
                fileOut.write("\n");
            }

            in.close();
            fileOut.close();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public String createOutputFolder() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateStr = dateFormat.format(date);
        String sFile = savedFilePath+"/" + dateStr;
        File f = new File(sFile);
        if (!f.exists()) {
            f.mkdir();
        }
        return sFile;
    }

    public String getStockAndSave(String sId) {
        String sUrl = basicInfoUrl.replace("STOCK_ID", sId);
        String sFile = createOutputFolder();
        sFile = sFile+"/" + sId + ".html";
        if(writeURLToFile(sFile,sUrl)) {
            return sFile;
        }
        return null;
    }
    public String getDailyPriseAndSave(String sId) {
        String sUrl = dailyPriseUrl.replace("STOCK_ID", sId);
        String sFile = createOutputFolder();
        sFile = sFile+"/daily_prise_" + sId + ".html";
        if(writeURLToFile(sFile,sUrl)) {
            return sFile;
        }
        return null;
    }
    public String getAnnualDividendAndSave(String sId) {
        String sUrl = annualDividendUtl.replace("STOCK_ID", sId);
        String sFile = createOutputFolder();
        sFile = sFile+"/annual_dividend_" + sId + ".html";
        if(writeURLToFile(sFile,sUrl)) {
            return sFile;
        }
        return null;
    }
    public String getEarningPageAndSave(String sId) {
        String sUrl = earningUtl.replace("STOCK_ID", sId);
        String sFile = createOutputFolder();
        sFile = sFile+"/earning_" + sId + ".html";
        if(writeURLToFile(sFile,sUrl)) {
            return sFile;
        }
        return null;
    }
    public String getPageAndSave(String url, String storePath) {
        String sFile = createOutputFolder();
        sFile = sFile+"/" + storePath + ".html";
        if(writeURLToFile(sFile,url)) {
            return sFile;
        }
        return null;
    }
}
