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
    final String savedFilePath = "/Users/chenhj1234/Android/sample/repo/StockUtility/saved_pages";
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
}
