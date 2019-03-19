package htmlparser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HTMLParserDailyPriseListener extends HTMLParserBaseListener {
    private boolean DEBUG_TABLE_CONTENT = false;
    private boolean DEBUG_TABLE_INTERNAL_CONTENT = false;
    private boolean DEBUG_SCRIPT_CONTENT = false;
    private boolean DEBUG_DAILY_INFO = true;
    private boolean DEBUG_DAILY_TIME_INFO = false;
    private boolean DEBUG_TABLE_LEVEL_CONTENT = false;
    private ArrayList<HTMLParser.HtmlElementContext> tableList = new ArrayList<>();
    String mPageTitle = null;


    private boolean checkElementName(ParseTree t, String tEle) {
        ParseTree tTag = t.getChild(1);
        if(tTag != null && tTag instanceof RuleContext) {
            if(tEle == null || tEle.equalsIgnoreCase(tTag.getText())) {
                return true;
            }
        }
        return false;
    }

    @Override public void enterHtmlElement(HTMLParser.HtmlElementContext ctx) {
        String ctxText = ctx.getText();
        /*
        if(ctxText.length() > 10) {
            System.out.println("Enter ctx begin:" + ctxText.substring(0, 10) + " end:" + ctxText.substring(ctxText.length() - 11, ctxText.length() - 1));
        } else {
            System.out.println("Enter ctx begin:" + ctxText);
        }
        */
        ParseTree tTag = ctx.getChild(1);
        if(tTag != null) {
            if(checkElementName(ctx, "table")) {
                int tagend = ctx.getChildCount();
                ParseTree tagAttr = ctx.getChild(2);
                ParseTree tagEnd = ctx.getChild(tagend-2);
                ParseTree tagEndSym = ctx.getChild(tagend-1);
                if(ctx.getText().contains("列出前五十筆成交明細")) {
                    if(DEBUG_TABLE_CONTENT) System.out.println("start tTag:" + tTag.getText() + " attr:" + tagAttr.getText() + " end tag:" + tagEnd.getText() + tagEndSym.getText() + " count:" + ctx.getChildCount());
                    tableList.add(ctx);
                }
            } else if(checkElementName(ctx, "title")) {
                mPageTitle = ctx.htmlContent().getText();
            }
//            int tagend = ctx.getChildCount();
//            ParseTree tagAttr = ctx.getChild(2);
//            ParseTree tagEnd = ctx.getChild(tagend-2);
//            ParseTree tagEndSym = ctx.getChild(tagend-1);
//            System.out.println("start tTag:" + tTag.getText() + " ok:" + checkTagOk(tTag.getText(),tagEnd.getText()) + " attr:" + tagAttr.getText() + " end tag:" + tagEnd.getText() + tagEndSym.getText() + " count:" + ctx.getChildCount());
//            System.out.println("start tTag:" + tTag.getText() + " count:" + ctx.getChildCount());
        }
    }
    @Override public void enterScriptlet(HTMLParser.ScriptletContext ctx) {
        System.out.println(ctx.getText());
    }
    @Override public void enterScript(HTMLParser.ScriptContext ctx) {
        if(ctx.SCRIPT_BODY() != null) {
            if (DEBUG_SCRIPT_CONTENT)
                System.out.println(ctx.SCRIPT_BODY().getText());
        }
    }

    public String getElementTag(HTMLParser.HtmlElementContext ctx) {
        ParseTree tTag = ctx.getChild(1);
        return tTag.getText();
    }
    final private int DAILY_PRISE_TABLE_COLUMN_TIME = 0;
    final private int DAILY_PRISE_TABLE_COLUMN_BUYIN_PRISE = 1;
    final private int DAILY_PRISE_TABLE_COLUMN_SELLOUT_PRISE = 2;
    final private int DAILY_PRISE_TABLE_COLUMN_DEAL_PRISE = 3;
    final private int DAILY_PRISE_TABLE_COLUMN_PRISE_SHIFT = 4;
    final private int DAILY_PRISE_TABLE_COLUMN_AMOUNT = 5;
    private int tableParsingIndex = -1;
    public Date mLatestRecordDate = null;
    public float buyin = -1;
    public float sellout = -1;
    public float dealprise = -1;
    public int amount = -1;
    public float dailyShift = -1;

    private boolean processDateColumn(String inStr) {
        if(inStr != null && !inStr.equals("")) {
            DateFormat todayFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date todayDate = new Date();
            String todayStr = todayFormat.format(todayDate);
            DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            try {
                // To get the date object from the string just called the
                // parse method and pass the time string to it. This method
                // throws ParseException if the time string is invalid.
                // But remember as we don't pass the date information this
                // date object will represent the 1st of january 1970.
                Date date = sdf.parse(todayStr + " " + inStr);

                if(DEBUG_DAILY_TIME_INFO) System.out.println("Date and Time: " + date);
                if(mLatestRecordDate == null ) {
                    mLatestRecordDate = date;
                    if(DEBUG_DAILY_TIME_INFO) System.out.println("New date:" + sdf.format(date) + " assigned");
                    tableParsingIndex = DAILY_PRISE_TABLE_COLUMN_BUYIN_PRISE;
                } else {
                    if(mLatestRecordDate.before(date)) {
                        if(DEBUG_DAILY_TIME_INFO) System.out.println("New date:" + sdf.format(date) + " after old date:" + sdf.format(mLatestRecordDate));
                        mLatestRecordDate = date;
                        tableParsingIndex = DAILY_PRISE_TABLE_COLUMN_BUYIN_PRISE;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private boolean processBuyinColumn(String inStr) {
        if(tableParsingIndex == DAILY_PRISE_TABLE_COLUMN_BUYIN_PRISE) {
            if(inStr != null && !inStr.equals("")) {
                buyin = Float.valueOf(inStr);
            } else {
                buyin = 0;
            }
            tableParsingIndex = DAILY_PRISE_TABLE_COLUMN_SELLOUT_PRISE;
            return true;
        }
        return false;
    }

    private boolean processSelloutColumn(String inStr) {
        if(tableParsingIndex == DAILY_PRISE_TABLE_COLUMN_SELLOUT_PRISE) {
            if(inStr != null && !inStr.equals("")) {
                sellout = Float.valueOf(inStr);
            } else {
                sellout = 0;
            }
            tableParsingIndex = DAILY_PRISE_TABLE_COLUMN_DEAL_PRISE;
            return true;
        }
        return false;
    }

    private boolean processDealPriseColumn(String inStr) {
        if(tableParsingIndex == DAILY_PRISE_TABLE_COLUMN_DEAL_PRISE) {
            if(inStr != null && !inStr.equals("")) {
                dealprise = Float.valueOf(inStr);
            } else {
                dealprise = 0;
            }
            tableParsingIndex = DAILY_PRISE_TABLE_COLUMN_PRISE_SHIFT;
            return true;
        }
        return false;
    }

    private boolean processDailyShiftColumn(String inStr) {
        if(tableParsingIndex == DAILY_PRISE_TABLE_COLUMN_PRISE_SHIFT) {
            if(inStr != null && !inStr.equals("")) {
                dailyShift = Float.valueOf(inStr);
            } else {
                dailyShift = 0;
            }
            tableParsingIndex = DAILY_PRISE_TABLE_COLUMN_AMOUNT;
            return true;
        }
        return false;
    }
    private void debug_output(boolean flag, String str) {
        if(flag) System.out.println(str);
    }
    private boolean processAmountColumn(String inStr) {
        if(tableParsingIndex == DAILY_PRISE_TABLE_COLUMN_AMOUNT) {
            if(inStr != null && !inStr.equals("")) {
                amount = Integer.valueOf(inStr);
            } else {
                amount = 0;
            }
            tableParsingIndex = -1;
            debug_output(DEBUG_DAILY_INFO, "time:" + mLatestRecordDate + " prise:" + dealprise + " shift:" + dailyShift + " amount:" + amount);
            return true;
        }
        return false;
    }

    private void processColumn(DailyPriseDPListener dpListener) {

        if(processDateColumn(dpListener.timeStr)) {
        } else if(processBuyinColumn(dpListener.numberStr)){

        } else if(processSelloutColumn(dpListener.numberStr)){

        } else if(processDealPriseColumn(dpListener.numberStr)){

        } else if(processDailyShiftColumn(dpListener.shiftStr + dpListener.numberStr)){

        } else if(processAmountColumn(dpListener.numberStr)){

        } else {

        }
    }

    public void processTableColInfo(String infoStr) {
        CharStream strInput;
        //System.out.println(infoStr);
        strInput = CharStreams.fromString(infoStr);
        DailyPriseTableEntryGrammarLexer infoLexer = new DailyPriseTableEntryGrammarLexer(strInput);
        CommonTokenStream iTokens = new CommonTokenStream(infoLexer);
        DailyPriseTableEntryGrammarParser infoParser = new DailyPriseTableEntryGrammarParser(iTokens);
        ParseTreeWalker walker = new ParseTreeWalker();
        //InfoStringGrammarDataListener infoListener = new InfoStringGrammarDataListener();
        DailyPriseDPListener dpListener = new DailyPriseDPListener();
        walker.walk(dpListener, infoParser.dailyPriseTableEntry());
        processColumn(dpListener);
        //System.out.println("Infostr:" + infoParser.infostr().getText() + " child count:" + infoParser.infostr().getChildCount());
    }

    private void handleTable_td(HTMLParser.HtmlElementContext tableEntry) {
        if(DEBUG_TABLE_INTERNAL_CONTENT) System.out.println("        td entry tableEntry:" + tableEntry.htmlContent().getText());
        processTableColInfo(tableEntry.htmlContent().getText());

    }
    private void handleTable_tr(HTMLParser.HtmlElementContext tableEntry) {
        if(DEBUG_TABLE_INTERNAL_CONTENT) System.out.println("tr entry tableEntry:" + tableEntry.getText());
    }
    public void handleTableEntry(HTMLParser.HtmlElementContext tableEntry) {
        String entryTag = getElementTag(tableEntry);
        if(entryTag.equals("tr")) {
            handleTable_tr(tableEntry);
        } else if(entryTag.equals("td")) {
            handleTable_td(tableEntry);
        }
    }

    public void handleTableInternalLevel(HTMLParser.HtmlElementContext currentLevel, int level) {
        if(currentLevel == null) {
            debug_output(DEBUG_TABLE_LEVEL_CONTENT, "currentLevel is null");
            return;
        }
        if(currentLevel.htmlContent() == null) {
            debug_output(DEBUG_TABLE_LEVEL_CONTENT, "urrentLevel.htmlContent() is null, ctx:" + currentLevel.getText());
            return;
        }
        List<HTMLParser.HtmlElementContext> nextLevelElements = currentLevel.htmlContent().htmlElement();
        HTMLParser.HtmlElementContext nextLevel;
        handleTableEntry(currentLevel);
        if(nextLevelElements == null || (nextLevelElements.size() == 0)) {
            return;
        }
        for(int i = 0;i < nextLevelElements.size();i++) {
            nextLevel = nextLevelElements.get(i);
            /*
            if(getElementTag(nextLevel).equals("td")) {
                nextLevel.htmlContent().getText();
            }
            */
            handleTableInternalLevel(nextLevel, level + 2);
        }
    }

    public void processDealPriseTable() {
        HTMLParser.HtmlElementContext tblCtx = null;
        if(tableList.size() > 0) {
            tblCtx = tableList.get(0);
            if(DEBUG_TABLE_CONTENT) System.out.println("Table ctx:" + tblCtx.getText());
            handleTableInternalLevel(tblCtx, 0);
        }
        debug_output(DEBUG_DAILY_INFO, "time:" + mLatestRecordDate + " prise:" + dealprise + " shift:" + dailyShift + " amount:" + amount);
    }
}
