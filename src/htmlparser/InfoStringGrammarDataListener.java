package htmlparser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InfoStringGrammarDataListener extends InfoStringGrammarBaseListener {
    public StackBasicInformation stockInfo = new StackBasicInformation();
    public int parsedToken = -1;
    public String yearAndSeasonString = "";
    private int annualYear = 0;
    private int recordSeason = 0;
    private void resetParsedToken() {
        parsedToken = -1;
    }
    private int getParsedToken() {
        return parsedToken;
    }
    private void setParsedToken(int v) {
        parsedToken = v;
    }
    private boolean showListenerClass = false;
    private boolean showInfoData = false;
    private boolean showExceptionStock = false;
    public void setShowListenerClass(boolean bs) {
        showListenerClass = bs;
    }
    public void setShowInfoData(boolean bs) {
        showInfoData = bs;
    }
    public void setShowExceptionStock(boolean bs) {
        showExceptionStock = bs;
    }
    private Date strToDate(String dateStr) {
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("yyyy/mm/dd").parse(dateStr);
        } catch (Exception e) {
            if(showExceptionStock) e.printStackTrace();
        }
        return date1;
    }
    public boolean assignRealNumber(String numberStr) {
        StackBasicInformation.seasonRecord srec = null;
        switch (getParsedToken()) {
            case InfoStringGrammarParser.CASH_DIVIDE_END:
                stockInfo.CashDividend = Float.valueOf(numberStr);
                if(showInfoData) System.out.println("CashDividend " + stockInfo.CashDividend);
                break;
            case InfoStringGrammarParser.STOCK_DIVID_END:
                stockInfo.StockDividend = Float.valueOf(numberStr);
                if(showInfoData) System.out.println("StockDividend " + stockInfo.StockDividend);
                break;
            case InfoStringGrammarParser.RETURN_ON_ASSETS:
                stockInfo.ReturnOnAssets = Float.valueOf(numberStr);
                if(showInfoData) System.out.println("Return on asset parsed " + stockInfo.ReturnOnAssets);
                break;
            case InfoStringGrammarParser.RETURN_ON_EQULITY:
                stockInfo.ReturnOnEquity = Float.valueOf(numberStr);
                if(showInfoData) System.out.println("Return on equity parsed " + stockInfo.ReturnOnEquity);
                break;
            case InfoStringGrammarParser.EARNING_BEFORE_TAX_MARGIN:
                stockInfo.EarningBeforeTaxMargin = Float.valueOf(numberStr);
                if(showInfoData) System.out.println("Earn before tax margin " + stockInfo.EarningBeforeTaxMargin);
                break;
            case InfoStringGrammarParser.GROSS_PROFIT_MARGIN:
                stockInfo.GrossProfitMargin = Float.valueOf(numberStr);
                if(showInfoData) System.out.println("Gross profit margin " + stockInfo.GrossProfitMargin);
                break;
            case InfoStringGrammarParser.OPERATION_PROFIT_MARGIN:
                stockInfo.OperatingProfitMargin = Float.valueOf(numberStr);
                if(showInfoData) System.out.println("Operation profit margin " + stockInfo.OperatingProfitMargin);
                break;
            case InfoStringGrammarParser.BOOK_VALUE_TITLE:
                stockInfo.BookValuePerShare = Float.valueOf(numberStr);
                if(showInfoData) System.out.println("BookValuePerShare " + stockInfo.BookValuePerShare);
                break;
            case InfoStringGrammarParser.YEAR_UNIT:
                srec = stockInfo.new seasonRecord(annualYear, 0, Float.valueOf(numberStr));
                stockInfo.yearRecordList.add(srec);
//                stockInfo.EarningsPerShare_Year_Map.put(yearAndSeasonString, Float.valueOf(numberStr));
                if(showInfoData) System.out.println("Year:" + annualYear + " season:" + recordSeason + " value:" + srec.share);
                break;
            case InfoStringGrammarParser.YEAR_BEFORE_SEASON:
                srec = stockInfo.new seasonRecord(annualYear, recordSeason, Float.valueOf(numberStr));
                stockInfo.seasonRecordList.add(srec);
//                stockInfo.EarningsPerShare_Season_Map.put(yearAndSeasonString, Float.valueOf(numberStr));
                if(showInfoData) System.out.println("Year:" + annualYear + " season:" + recordSeason + " value:" + srec.share);
                break;
            case InfoStringGrammarParser.EXCLUSION_DATE:
                stockInfo.ExclusionDate = strToDate(numberStr);
                if(showInfoData) System.out.println("ExclusionDate:" + stockInfo.ExclusionDate);
                break;
            case InfoStringGrammarParser.ELIMINATION_DATE:
                stockInfo.EliminationDate = strToDate(numberStr);
                if(showInfoData) System.out.println("EliminationDate:" + stockInfo.EliminationDate);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override public void enterReal(InfoStringGrammarParser.RealContext ctx) {
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterReal_Dollar(InfoStringGrammarParser.Real_DollarContext ctx) {
        assignRealNumber(ctx.REAL_NUMBER().getText());
        resetParsedToken();
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName() + " number:" + ctx.REAL_NUMBER());
    }
    @Override public void enterReal_Year(InfoStringGrammarParser.Real_YearContext ctx) {
        setParsedToken(InfoStringGrammarParser.YEAR_UNIT);
        yearAndSeasonString = ctx.REAL_NUMBER().getText();
        annualYear = Integer.parseInt(ctx.REAL_NUMBER().getText());
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterReal_Year_season(InfoStringGrammarParser.Real_Year_seasonContext ctx) {
        setParsedToken(InfoStringGrammarParser.YEAR_BEFORE_SEASON);
        yearAndSeasonString = ctx.REAL_NUMBER(0).getText() + ctx.REAL_NUMBER(1).getText();
        annualYear = Integer.parseInt(ctx.REAL_NUMBER(0).getText());
        recordSeason = Integer.parseInt(ctx.REAL_NUMBER(1).getText());
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterReal_Percent(InfoStringGrammarParser.Real_PercentContext ctx) {
        String revl = "0";
        if(ctx.REAL_NUMBER() != null) {
            revl = ctx.REAL_NUMBER().getText();
        }
        assignRealNumber(revl);
        resetParsedToken();
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterNot_Number(InfoStringGrammarParser.Not_NumberContext ctx) {
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName() + " " + ctx.getText());
    }
    @Override public void exitBook_Value(InfoStringGrammarParser.Book_ValueContext ctx) {
        setParsedToken(InfoStringGrammarParser.BOOK_VALUE_TITLE);
        assignRealNumber(ctx.REAL_NUMBER().getText());
        resetParsedToken();
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void exitBook_Value_Zero(InfoStringGrammarParser.Book_Value_ZeroContext ctx) {
        setParsedToken(InfoStringGrammarParser.BOOK_VALUE_TITLE);
        assignRealNumber("0");
        resetParsedToken();
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }

    @Override public void enterEmpty_Rule(InfoStringGrammarParser.Empty_RuleContext ctx) {
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterReturnOnEquity(InfoStringGrammarParser.ReturnOnEquityContext ctx) {
        setParsedToken(InfoStringGrammarParser.RETURN_ON_EQULITY);
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterReturnOnAssets(InfoStringGrammarParser.ReturnOnAssetsContext ctx) {
        setParsedToken(InfoStringGrammarParser.RETURN_ON_ASSETS);
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterEarningBeforeTaxMargin(InfoStringGrammarParser.EarningBeforeTaxMarginContext ctx) {
        setParsedToken(InfoStringGrammarParser.EARNING_BEFORE_TAX_MARGIN);
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterOperatingProfitMargin(InfoStringGrammarParser.OperatingProfitMarginContext ctx) {
        setParsedToken(InfoStringGrammarParser.OPERATION_PROFIT_MARGIN);
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterGrossProfitMargin(InfoStringGrammarParser.GrossProfitMarginContext ctx) {
        setParsedToken(InfoStringGrammarParser.GROSS_PROFIT_MARGIN);
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterCashDividend(InfoStringGrammarParser.CashDividendContext ctx) {
        setParsedToken(InfoStringGrammarParser.CASH_DIVIDE_END);
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterStockDividend(InfoStringGrammarParser.StockDividendContext ctx) {
        setParsedToken(InfoStringGrammarParser.STOCK_DIVID_END);
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterExclusionDate(InfoStringGrammarParser.ExclusionDateContext ctx) {
        setParsedToken(InfoStringGrammarParser.EXCLUSION_DATE);
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterEliminationDate(InfoStringGrammarParser.EliminationDateContext ctx) {
        setParsedToken(InfoStringGrammarParser.ELIMINATION_DATE);
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterZeroOrNoValue(InfoStringGrammarParser.ZeroOrNoValueContext ctx) {
        assignRealNumber("0");
        resetParsedToken();
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterDate(InfoStringGrammarParser.DateContext ctx) {
        String dateStr = Integer.toString(Integer.valueOf(ctx.REAL_NUMBER(0).getText()) + 1911) +
                "/" + ctx.REAL_NUMBER(1).getText() +
                "/" + ctx.REAL_NUMBER(2).getText();
        assignRealNumber(dateStr);
        resetParsedToken();
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterDate_Period(InfoStringGrammarParser.Date_PeriodContext ctx) {
        resetParsedToken();
        if(showListenerClass) System.out.println(ctx.getClass().getSimpleName());
    }
    @Override public void enterTitleRule(InfoStringGrammarParser.TitleRuleContext ctx) {
        String stockName = "";
        String childText;
        for(int i = 0;i < ctx.getChildCount();i++) {
            childText =ctx.getChild(i).getText();
            if(childText.equals("(")) {
                break;
            } else {
                stockName += ctx.getChild(i).getText();
            }
        }
        stockInfo.StockName = stockName;
        stockInfo.StockNumber = ctx.REAL_NUMBER().getText();
        if(showInfoData) System.out.println("name:" + stockInfo.StockName + " id:" + stockInfo.StockNumber);
    }
}
