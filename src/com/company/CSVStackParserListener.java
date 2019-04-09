package com.company;

import java.util.ArrayList;

public class CSVStackParserListener extends CSVBaseListener {
    private boolean findStockStart = false;
    private boolean findOverTheCounter = false;
    private boolean findCurrency = false;
    public StockInfoList infoList = new StockInfoList();
    @Override public void enterHdr(CSVParser.HdrContext ctx) {
    }

    @Override public void enterRow(CSVParser.RowContext ctx) {
        String onerow = "";
        ArrayList<String> rowInfo = new ArrayList<>();
//        if(ctx.field() != null && ctx.field().size() > 10 ) {
        if(!findStockStart && !findOverTheCounter && !findCurrency && ctx.getText().contains("最後揭示買價")) {
            System.out.println("Find data table start " + ctx.getText());
            findStockStart = true;
        } else if(!findStockStart && !findOverTheCounter && !findCurrency && ctx.getText().contains("最後賣價")) {
            System.out.println("Find over the counter table start " + ctx.getText());
            findOverTheCounter = true;
        } else if(!findStockStart && !findOverTheCounter && !findCurrency && ctx.getText().contains("遠期30天")) {
            findCurrency = true;
        } else if(findStockStart) {
            for (int i = 0; i < ctx.field().size(); i++) {
                CSVParser.FieldContext field = ctx.field(i);
                if (field.STRING() != null) {
                    onerow = field.STRING().getText();
                    onerow = onerow.substring(1, onerow.length() - 1);
                } else if (field.TEXT() != null) {
                    onerow = field.TEXT().getText();
                } else {
                    onerow = "";
                }
                rowInfo.add(onerow);
            }
            infoList.addEntry(rowInfo);
        } else if(findOverTheCounter) {
            boolean validRow = true;
            for (int i = 0; i < ctx.field().size(); i++) {
                CSVParser.FieldContext field = ctx.field(i);
                if (field.STRING() != null) {
                    onerow = field.STRING().getText();
                    onerow = onerow.substring(1, onerow.length() - 1);
                } else if (field.TEXT() != null) {
                    onerow = field.TEXT().getText();
                } else {
                    onerow = "";
                }
                if(i == 0) {
                    if(onerow.equals("") || (onerow.length() <4)) {
                        validRow = false;
                        break;
                    }
                    try {
                        Integer.parseInt(onerow.substring(0,4));
                    } catch(NumberFormatException e) {
                        validRow = false;
                        break;
                    }
                }
                rowInfo.add(onerow);
            }
            if(validRow && rowInfo.size() > 2) {
                infoList.addEntry(rowInfo);
            } else {
                System.out.println("Ignore invalid row " + ctx.getText());
            }
        } else if(findCurrency) {
            for (int i = 0; i < ctx.field().size(); i++) {
                CSVParser.FieldContext field = ctx.field(i);
                if (field.STRING() != null) {
                    onerow = field.STRING().getText();
                    onerow = onerow.substring(1, onerow.length() - 1);
                } else if (field.TEXT() != null) {
                    onerow = field.TEXT().getText();
                } else {
                    onerow = "";
                }
                rowInfo.add(onerow);
            }
            infoList.addEntry(rowInfo);
        }
        //System.out.println(ctx.getText() + " size:" + ctx.field().size());
    }
}
