package htmlparser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class BasicInfoParser {
    private boolean DEBUG_TOKEN_STREAM = false;
    public HTMLParserTableListener mHtmlListener = null;
    public HTMLParser mHtmlParser = null;
    public HTMLLexer mHtmlLexer = null;
    CharStream mInput = null;
    public boolean parseBasicInfoPage(String basicInfoPage) {
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
        ParseTreeWalker walker = new ParseTreeWalker();
        System.out.println("begin ...");
        //parser.htmlDocument();
        System.out.println("start walk ...");
        mHtmlListener = new HTMLParserTableListener();
        walker.walk(mHtmlListener, mHtmlParser.htmlDocument());
        System.out.println("hListener.processTable");
        mHtmlListener.processTable();
        System.out.println("hListener.processTableContent");
        mHtmlListener.processTableContent();
        System.out.println("hListener.processTableColumesAndRows");
        mHtmlListener.processTableColumesAndRows();
        System.out.println("hListener.processPageTitle");
        mHtmlListener.processPageTitle();

        return true;
    }
}
