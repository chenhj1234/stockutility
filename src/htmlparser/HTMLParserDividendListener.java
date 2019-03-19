package htmlparser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

public class HTMLParserDividendListener extends HTMLParserBaseListener {
    public class tableDec {
        public HTMLParser.HtmlElementContext ctx;
        public boolean isParent = false;
        public tableDec(HTMLParser.HtmlElementContext c, boolean b) {
            ctx = c;
            isParent = b;
        }
    }
    //年　度	現 金 股 利	盈 餘 配 股	公 積 配 股	股 票 股 利	合　計

    public class dividendRecord {
        public int year;
        public float[] dividend = new float[5];
    }
    int ANNUAL_COLUMN = -1;
    int CASH_COLUMN = -1;
    int EARN_COLUMN = -1;
    int STOCK_CAP_COLUMN = -1;
    int STOCK_COLUMN = -1;
    int TOTAL_COLUMN = -1;
    int currentIndex = 0;

    public ArrayList<tableDec> tableList = new ArrayList<>();
    public ArrayList<dividendRecord> divRecList = new ArrayList<>();
    private dividendRecord divRec = null;
    @Override
    public void enterHtmlElement(HTMLParser.HtmlElementContext ctx) {
        List<HTMLParser.HtmlTagNameContext> tags = ctx.htmlTagName();
        if(tags.size() == 2) {
            if(tags.get(0).getText().equalsIgnoreCase("table")) {
                boolean isTbl = ctx.htmlContent().getText().contains("</table>");
                tableList.add(new tableDec(ctx, isTbl));
            }
        }
    }
    private void debug_output(boolean flag, String str) {
        if(flag) System.out.println(str);
    }
    private boolean DEBUG_TABLE_INTERNAL_CONTENT = false;
    private boolean DEBUG_TABLE_LEVEL_CONTENT = true;

    private void processTdColumn(String tdStr) {
        CharStream strInput;
        //System.out.println(infoStr);
        strInput = CharStreams.fromString(tdStr);
        DividendGrammarLexer infoLexer = new DividendGrammarLexer(strInput);
        CommonTokenStream iTokens = new CommonTokenStream(infoLexer);
        DividendGrammarParser infoParser = new DividendGrammarParser(iTokens);
        ParseTree pt = infoParser.number();
        if(infoParser.typeIndex >= infoParser.TYPE_INDEX_INT) {
            if (currentIndex == 0) {
                divRec = new dividendRecord();
                divRec.year = Integer.valueOf(pt.getText());
                currentIndex++;
            } else if (currentIndex < infoParser.TYPE_INDEX_INT) {
                divRec.dividend[currentIndex - 1] = Float.valueOf(pt.getText());
                currentIndex++;
                if(currentIndex == infoParser.TYPE_INDEX_INT) {
                    divRecList.add(divRec);
                    currentIndex = 0;
                    System.out.println("Add year:" + divRec.year + " div:" +
                                    divRec.dividend[0] + " " +
                                    divRec.dividend[1] + " " +
                                    divRec.dividend[2] + " " +
                                    divRec.dividend[3] + " " +
                                    divRec.dividend[4]);

                }
            } else {
                System.out.println("Error:");
            }
        }
    }

    private void handleTable_td(HTMLParser.HtmlElementContext tableEntry) {
        if(DEBUG_TABLE_INTERNAL_CONTENT) System.out.println("        td entry tableEntry:" + tableEntry.htmlContent().getText());
        processTdColumn(tableEntry.htmlContent().getText());
    }

    private void handleTable_tr(HTMLParser.HtmlElementContext tableEntry) {
        if(DEBUG_TABLE_INTERNAL_CONTENT) System.out.println("tr entry tableEntry:" + tableEntry.getText());
    }

    private String getElementTag(HTMLParser.HtmlElementContext ctx) {
        List<HTMLParser.HtmlTagNameContext> tgs = ctx.htmlTagName();
        if(tgs != null && tgs.size() > 0) {
            return tgs.get(0).getText();
        }
        return "";
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

    public void processTable() {
        for(int i = 0;i < tableList.size();i++) {
            tableDec dec = tableList.get(i);
            //System.out.println("table i:" + i + " is table:" + dec.isParent);
            if(!dec.isParent && dec.ctx.htmlContent().getText().replaceAll("\n", "").matches(".*盈\\s+餘\\s+配\\s+股.*")) {
                //System.out.println("Found ctx:" + dec.ctx.htmlContent().getText());
                handleTableInternalLevel(dec.ctx, 0);
            }
        }
    }
}