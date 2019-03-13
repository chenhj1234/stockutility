package htmlparser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class HTMLParserEarningListener extends HTMLParserDataTableListener {
    public EarningGrammarDataListener monthListener = null;
    public EarningGrammarDataListener searnBeforeTaxListener = null;
    public EarningGrammarDataListener searnAfterTaxListener = null;
    public EarningGrammarDataListener listener = null;
    @Override
    public void processTdColumn(String tdStr) {
        CharStream strInput;
        strInput = CharStreams.fromString(tdStr);
        EarningGrammarLexer infoLexer = new EarningGrammarLexer(strInput);
        CommonTokenStream iTokens = new CommonTokenStream(infoLexer);
        EarningGrammarParser infoParser = new EarningGrammarParser(iTokens);
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, infoParser.earningColumn());
    }
    public void processTable() {
        tableDec dec = null;
        HTMLParser.HtmlElementContext ctx = null;
        for(int i = 0;i < tableList.size();i++) {
            dec = tableList.get(i);
            if(dec.isParent) continue;
            ctx = dec.ctx;

            if(ctx.getText().contains("累計營收") && ctx.getText().contains("年增率")) {
                System.out.println("Found 每 月 營 收 變 化");
                listener = new EarningGrammarDataListener();
                monthListener = listener;
                monthListener.assignToMonthlyTable(true);
                processSingleTable(dec);
                monthListener.printInfo();
            }
            if(ctx.getText().contains("稅 後 盈 餘") && ctx.getText().contains("年 增 率")) {
                System.out.println("Found 稅 後 盈 餘");
                listener = new EarningGrammarDataListener();
                searnAfterTaxListener = listener;
                listener.assignToMonthlyTable(false);
                processSingleTable(dec);
                listener.printInfo();
            }
            if(ctx.getText().contains("稅 前 盈 餘") && ctx.getText().contains("年 增 率")) {
                System.out.println("Found 稅 前 盈 餘");
                listener = new EarningGrammarDataListener();
                searnBeforeTaxListener = listener;
                listener.assignToMonthlyTable(false);
                processSingleTable(dec);
                listener.printInfo();
            }
        }
    }

}
