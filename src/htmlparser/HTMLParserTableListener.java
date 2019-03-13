package htmlparser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.List;

public class HTMLParserTableListener extends HTMLParserBaseListener {

    public InfoStringGrammarDataListener infoListener = new InfoStringGrammarDataListener();
    public StackBasicInformation stockInfo = new StackBasicInformation();
    public boolean showTableVisit = false;
    public boolean showTableContent = false;
    String mPageTitle = null;
    private class ParseTableColume {
        public ArrayList<String> columes = new ArrayList<>();
    };
    private class ParseTableMeta {
        public ArrayList parentTable = new ArrayList();
        public ArrayList<ParseTableColume> rows = new ArrayList<>();
        public boolean markedAsParent = false;
    };
    public ArrayList<ParseTableMeta> tableMetadata = new ArrayList<>();

    public HTMLParserTableListener() {
        infoListener.setShowListenerClass(false);
        infoListener.setShowInfoData(true);
        infoListener.setShowExceptionStock(false);
        setShowTableContent(false);
        setShowTableVisit(false);
    }

    public void setShowTableContent(boolean st) {
        showTableContent = st;
    }

    public void setShowTableVisit(boolean st) {
        showTableVisit = st;
    }

    private ArrayList<HTMLParser.HtmlElementContext> tableList = new ArrayList<>();
    @Override public void enterHtmlTagName(HTMLParser.HtmlTagNameContext ctx) {
    }

    private boolean checkElementName(ParseTree t, String tEle) {
        ParseTree tTag = t.getChild(1);
        if(tTag != null && tTag instanceof RuleContext) {
            if(tEle == null || tEle.equalsIgnoreCase(tTag.getText())) {
                return true;
            }
        }
        return false;
    }
    private boolean checkTagOk(String tag, String endTag) {
        if(tag.equals(endTag)) {
            return true;
        }
        if(tag.equals("input") || tag.equals("meta") || tag.equals("img") || tag.equals("link")) {
            return true;
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
                //if(showTableContent) System.out.println("start tTag:" + tTag.getText() + " attr:" + tagAttr.getText() + " end tag:" + tagEnd.getText() + tagEndSym.getText() + " count:" + ctx.getChildCount());
                tableList.add(ctx);
            } else if(checkElementName(ctx, "title")) {
                mPageTitle = ctx.htmlContent().getText();
            }
            int tagend = ctx.getChildCount();
            ParseTree tagAttr = ctx.getChild(2);
            ParseTree tagEnd = ctx.getChild(tagend-2);
            ParseTree tagEndSym = ctx.getChild(tagend-1);
            //System.out.println("start tTag:" + tTag.getText() + " ok:" + checkTagOk(tTag.getText(),tagEnd.getText()) + " attr:" + tagAttr.getText() + " end tag:" + tagEnd.getText() + tagEndSym.getText() + " count:" + ctx.getChildCount());
            //System.out.println("start tTag:" + tTag.getText() + " count:" + ctx.getChildCount());
        }
    }
    @Override public void exitHtmlElement(HTMLParser.HtmlElementContext ctx) {
        if(checkElementName(ctx, "table")) {
            //System.out.println("Exit ---------------------------");
            //System.out.println("ctx:" + ctx.getText());
        }
    }
    @Override public void enterDtd(HTMLParser.DtdContext ctx) {
    }
    @Override public void enterEveryRule(ParserRuleContext ctx) {
        //String ctxText = ctx.getText();
        //System.out.println("Enter ctx begin:" + ctxText.substring(0,20) + " end:" + ctxText.substring(ctxText.length() - 21,ctxText.length() - 1));
        //System.out.println("rule index:" + ctx.getRuleIndex());
    }


    public void processTable() {
        int idx, idx2;
        ParseTableMeta meta;
        if(tableList.size() > 0) {
            meta = new ParseTableMeta();
            tableMetadata.add(meta);
        }

        for(idx = 1; idx < tableList.size(); idx++) {
            ParseTree ctx = tableList.get(idx);
            ParseTree parentCtx;
            meta = new ParseTableMeta();
            tableMetadata.add(meta);
            while ((parentCtx = ctx.getParent()) != null) {
                if(!checkElementName(parentCtx, "table")) {
                    ctx = parentCtx;
                    continue;
                }
                for (idx2 = 0; idx2 < idx; idx2++) {
                    HTMLParser.HtmlElementContext ctx1 = tableList.get(idx2);
                    if(ctx1 == parentCtx) {
                        //System.out.println("Find table i:" + idx + " with parent table j:" + idx2);
                        meta.parentTable.add(idx2);
                        tableMetadata.get(idx2).markedAsParent = true;
                        break;
                    }
                }
                if(idx2 == idx) break;
                else ctx = parentCtx;
            }
        }
    }

    public String getElementTag(HTMLParser.HtmlElementContext ctx) {
        ParseTree tTag = ctx.getChild(1);
        return tTag.getText();
    }

    public void handleTableEntry(HTMLParser.HtmlElementContext tableEntry, ParseTableMeta tableMeta) {
        String entryTag = getElementTag(tableEntry);
        if(entryTag.equals("tr")) {
            tableMeta.rows.add(new ParseTableColume());
        } else if(entryTag.equals("td")) {
            int lastIndex = tableMeta.rows.size() - 1;
            tableMeta.rows.get(lastIndex).columes.add(tableEntry.htmlContent().getText());
        }
    }
    ParseTableMeta currentTableMeta = null;
    public void showNextLevel(HTMLParser.HtmlElementContext currentLevel, int level) {
        List<HTMLParser.HtmlElementContext> nextLevelElements = currentLevel.htmlContent().htmlElement();
        HTMLParser.HtmlElementContext nextLevel;
        String spaces = "";
        if(level != 0)
            spaces = new String(new char[level]).replace('\0', ' ');
        handleTableEntry(currentLevel, currentTableMeta);
        if(nextLevelElements == null || (nextLevelElements.size() == 0)) {
            if(showTableVisit)  System.out.println(spaces + " -- " + currentLevel.htmlContent().getText());
            return;
        }
        for(int i = 0;i < nextLevelElements.size();i++) {
            nextLevel = nextLevelElements.get(i);
            String trStr = "";

            if(getElementTag(nextLevel).equals("td")) {
                trStr = nextLevel.htmlContent().getText();
            }
            if(showTableVisit)  System.out.println(spaces + "next " + i + " level:" + getElementTag(nextLevel) +
                    " \"" + trStr + "\"");
            showNextLevel(nextLevel, level + 2);
        }
    }
    public void processTableContent() {
        for(int i = 0;i < tableMetadata.size();i++) {
            if(!tableMetadata.get(i).markedAsParent) {
                currentTableMeta = tableMetadata.get(i);
                if(tableList.get(i).getText().contains("營業毛利率")) {
                    // System.out.println(tableList.get(i).getText());
                    HTMLParser.HtmlElementContext nextLevel = tableList.get(i);
                    showNextLevel(nextLevel, 0);
                }
                if(tableList.get(i).getText().contains("除權日期")) {
                    HTMLParser.HtmlElementContext nextLevel = tableList.get(i);
                    showNextLevel(nextLevel, 0);

                }
                if(tableList.get(i).getText().contains("產業類別")) {
                    HTMLParser.HtmlElementContext nextLevel = tableList.get(i);
                    showNextLevel(nextLevel, 0);
                }
            }
        }

    }

    public void processTableColInfo(String infoStr) {
        CharStream strInput;
        //System.out.println(infoStr);
        strInput = CharStreams.fromString(infoStr);
        InfoStringGrammarLexer infoLexer = new InfoStringGrammarLexer(strInput);
        CommonTokenStream iTokens = new CommonTokenStream(infoLexer);
        InfoStringGrammarParser infoParser = new InfoStringGrammarParser(iTokens);
        ParseTreeWalker walker = new ParseTreeWalker();
        //InfoStringGrammarDataListener infoListener = new InfoStringGrammarDataListener();
        walker.walk(infoListener, infoParser.numberRule());
        //System.out.println("Infostr:" + infoParser.infostr().getText() + " child count:" + infoParser.infostr().getChildCount());
    }

    public void processPageTitle() {
        CharStream strInput;
        strInput = CharStreams.fromString(mPageTitle);
        InfoStringGrammarLexer infoLexer = new InfoStringGrammarLexer(strInput);
        CommonTokenStream iTokens = new CommonTokenStream(infoLexer);
        InfoStringGrammarParser infoParser = new InfoStringGrammarParser(iTokens);
        ParseTreeWalker walker = new ParseTreeWalker();
        //InfoStringGrammarDataListener infoListener = new InfoStringGrammarDataListener();
        walker.walk(infoListener, infoParser.titleRule());
        //System.out.println("Infostr:" + infoParser.infostr().getText() + " child count:" + infoParser.infostr().getChildCount());
    }
    public void processTableColumes(List<String> tCols) {
        String oneCol;
        for(int i = 0;i < tCols.size(); i++) {
            oneCol = tCols.get(i);
            if(showTableContent) System.out.println("Col " + i + " str:\"" + oneCol + "\"");
            processTableColInfo(oneCol);
        }
    }
    public void processTableRows(List<ParseTableColume> tRows) {
        for(int i = 0;i < tRows.size(); i++) {
            ParseTableColume oneRow = tRows.get(i);
            if(showTableContent) System.out.println("Row " + i);
            processTableColumes(oneRow.columes);
        }
    }
    public void processTableColumesAndRows() {
        ParseTableMeta meta = null;
        for(int i = 0;i < tableMetadata.size();i++) {
            meta = tableMetadata.get(i);
            if(meta.rows.size() != 0) {
                processTableRows(meta.rows);
            }
        }
    }
}
