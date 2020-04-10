package com.company;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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
    HttpURLConnection yc = null;
    InputStream inStr = null;
    InputStreamReader inStrRead = null;
    BufferedReader inBufRead = null;
    BufferedReader openURLForRead(String url) {
        return openURLForRead(url, "Big5-HKSCS");
    }
    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
        public X509Certificate[] getAcceptedIssuers(){return null;}
        public void checkClientTrusted(X509Certificate[] certs, String authType){}
        public void checkServerTrusted(X509Certificate[] certs, String authType){}
    }};

    BufferedReader openURLForRead(String url, String encode) {
        try {
            URL siteurl = new URL(url);
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            if(url.startsWith("https")) {
                yc = ((HttpsURLConnection) siteurl.openConnection());
            } else {
                yc = ((HttpURLConnection) siteurl.openConnection());
            }
            yc.setUseCaches(false);
            yc.setAllowUserInteraction(false);
            yc.setRequestProperty("User-Agent","Mozilla/5.0");
            yc.connect();
            int code = yc.getResponseCode();
            System.out.println("Response code:" + code);
            if(code >= 390) {
                yc.disconnect();
                return null;
            }
            inStr = yc.getInputStream();
            if((encode == null) || encode.equals("big5") || encode.equals("")) {
                inStrRead = new InputStreamReader(inStr, "Big5-HKSCS");
            } else {
                inStrRead = new InputStreamReader(inStr, encode);
            }
            inBufRead = new BufferedReader(inStrRead);
            return inBufRead;
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
            openURLForRead(url);
            if(inBufRead == null) return false;
            BufferedWriter fileOut = openFileForWrite(fileName);
            if(fileOut == null) {
                inBufRead.close();
                return false;
            }
            String inputLine;
            int writeLen = 0;
            while ((inputLine = inBufRead.readLine()) != null) {
                writeLen += inputLine.length();
                fileOut.write(inputLine);
                fileOut.write("\n");
            }
            fileOut.close();

            inBufRead.close();
            inBufRead = null;
            if(inStrRead != null) {
                inStrRead.close();
                inStrRead = null;
            }
            if(inStr != null) {
                inStr.close();
                inStr = null;
            }
            if(yc != null) {
                yc.disconnect();
                yc = null;
            }
            if(writeLen > 500) {
                return true;
            } else {
                return false;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean writeURLToFile(String fileName, String url, String encode) {
        try {
            BufferedReader in = openURLForRead(url, encode);
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
    public String getPageAndSave(String url, String storePath, String encode) {
        String sFile = createOutputFolder();
        sFile = sFile+"/" + storePath + ".html";
        if(writeURLToFile(sFile,url, encode)) {
            return sFile;
        }
        return null;
    }

//    final String twseIndexPageURL =     "http://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=20190723&type=MS";
    final String twseIndexPageURL = "https://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=__DATE__&type=ALL";
    final String tpexIndexPageURLOld = "http://www.tpex.org.tw/web/stock/aftertrading/daily_close_quotes/stk_quote_result.php?l=zh-tw&o=csv&d=__DATE__&s=0,asc,0";
    final String tpexIndexPageURL = "https://www.tpex.org.tw/web/stock/aftertrading/otc_quotes_no1430/stk_wn1430_result.php?l=zh-tw&o=csv&d=__DATE__&se=EW&s=0,asc,0";

    // return : saved file path if URL can read with code 200 and not 0 byte content
    //        : null if return 200, url unreachable or 0 byte content
    public String getTWSEPageAndSave(Date date) {
        // We will to get stock list info page from here http://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=20190322&type=ALL
        String twseIndexPage = twseIndexPageURL;
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String todaystr = dateFormat.format(date);
        String savefile = "MI_INDEX_" + todaystr +".csv";
        twseIndexPage = twseIndexPage.replace("__DATE__", todaystr);
        PageAndFile pf = new PageAndFile();
        System.out.println("twseIndexPage:" + twseIndexPage);
        savefile = pf.getPageAndSave(twseIndexPage,savefile);
        System.out.println("MI Index save to " + savefile);
        return savefile;
    }
    public String getTPEXPageAndSave(Date date) {
        String tpexIndexPage = tpexIndexPageURL;
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String todaystr = dateFormat.format(date);
        String tyear = String.valueOf(Integer.parseInt(todaystr.substring(0,4)) - 1911);
        String tmm = todaystr.substring(4,6);
        String tdd = todaystr.substring(6,8);
        String savefile = "RSTA_" + todaystr +".csv";
        tpexIndexPage = tpexIndexPage.replace("__DATE__", tyear + "/" + tmm + "/" + tdd);
        PageAndFile pf = new PageAndFile();
        savefile = pf.getPageAndSave(tpexIndexPage, savefile);
        System.out.println("TPEX index save to " + savefile);
        return savefile;
    }
}
