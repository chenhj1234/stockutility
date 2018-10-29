package htmlparser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class HandleWebPage {
    public static final int HANDLE_PAGE_SUCCESS = 0;
    public static final int HANDLE_PAGE_NO_SUCH_FILE = -1;
    public static final int HANDLE_PAGE_PARSER_FAIL = -2;


    String mPageFileName = null;
    CharStream mInput = null;
    HTMLLexer mPageLexer = null;
    CommonTokenStream mPageTokens = null;
    public HTMLParser mPageParser = null;
    ParseTreeWalker mPageWalker = null;
    public HTMLParserTableListener mPageListener = null;
    public int getWebPageFromFile(String pageFileName) {
        mPageFileName = pageFileName;
        try {
            mInput = CharStreams.fromFileName(mPageFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return HANDLE_PAGE_NO_SUCH_FILE;
        }
        mPageLexer = new HTMLLexer(mInput);
        mPageTokens = new CommonTokenStream(mPageLexer);
        mPageParser = new HTMLParser(mPageTokens);
        mPageWalker = new ParseTreeWalker();
        mPageListener = new HTMLParserTableListener();
        mPageWalker.walk(mPageListener, mPageParser.htmlDocument());
        System.out.println("hListener.processTable");
        mPageListener.processTable();
        System.out.println("hListener.processTableContent");
        mPageListener.processTableContent();
        System.out.println("hListener.processTableColumesAndRows");
        mPageListener.processTableColumesAndRows();
        System.out.println("hListener.processPageTitle");
        mPageListener.processPageTitle();

        return HANDLE_PAGE_SUCCESS;
    }
}
