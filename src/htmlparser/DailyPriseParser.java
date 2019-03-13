package htmlparser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class DailyPriseParser {
    private boolean DEBUG_TOKEN_STREAM = false;
    public HTMLParser mHtmlParser = null;
    public HTMLLexer mHtmlLexer = null;
    CharStream mInput = null;
    public HTMLParserDailyPriseListener mDailyPriseListener = null;
    public boolean parseDailyPrisePage(String basicInfoPage) {
        try {
            mInput = CharStreams.fromFileName(basicInfoPage);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        mHtmlLexer = new HTMLLexer(mInput);
        // create a buffer of tokens pulled from the lexer
        if(DEBUG_TOKEN_STREAM) {
            Token token;
            while (true) {
                token = mHtmlLexer.nextToken();
                if (token.getType() == Token.EOF) {
                    break;
                }

                System.out.println("Token: ‘" + token.getText() + "’" + " index:" + token.getType());
            }
            mHtmlLexer.reset();
        }
        CommonTokenStream tokens = new CommonTokenStream(mHtmlLexer);
        mHtmlParser = new HTMLParser(tokens);
        System.out.println("begin ...");
        //System.out.println(mHtmlParser.htmlDocument().getText());
        mDailyPriseListener = new HTMLParserDailyPriseListener();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(mDailyPriseListener, mHtmlParser.htmlDocument());
        mDailyPriseListener.processDealPriseTable();
        return true;
    }
}
