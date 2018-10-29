package com.company;

import java.util.ArrayList;

public class CSVStackParserListener extends CSVBaseListener {
    public StockInfoList infoList = new StockInfoList();
    @Override public void enterHdr(CSVParser.HdrContext ctx) {
    }

    @Override public void enterRow(CSVParser.RowContext ctx) {
        String onerow = "";
        ArrayList<String> rowInfo = new ArrayList<>();
        for(int i = 0;i < ctx.field().size();i ++) {
            CSVParser.FieldContext field = ctx.field(i);
            if(field.STRING() != null) {
                onerow = field.STRING().getText();
                onerow = onerow.substring(1, onerow.length()-1);
            } else if (field.TEXT() != null) {
                onerow = field.TEXT().getText();
            } else {
                onerow = "";
            }
            rowInfo.add(onerow);
        }
        infoList.addEntry(rowInfo);
        //System.out.println(ctx.getText() + " size:" + ctx.field().size());
    }
}
