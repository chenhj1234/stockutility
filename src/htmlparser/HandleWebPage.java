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
    public DailyPriseDPListener mDailyPriseEntryListener = null;
    public HTMLParserDailyPriseListener mDailyPrisePageListener = null;
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
    public int getBasicInfoPageFromFile(String pageFileName) {
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
    public int getDailyPrisePageFromFile(String pageFileName) {
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
        mDailyPrisePageListener = new HTMLParserDailyPriseListener();
        mPageWalker.walk(mDailyPrisePageListener, mPageParser.htmlDocument());
        mDailyPrisePageListener.processDealPriseTable();

        return HANDLE_PAGE_SUCCESS;
    }
    String mAnnualDividendFileName = "";
    public HTMLParserDividendListener mDividendListener = null;

    public int getAnnualDividendPageFromFile(String pageFileName) {
        mAnnualDividendFileName = pageFileName;
        try {
            mInput = CharStreams.fromFileName(mAnnualDividendFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return HANDLE_PAGE_NO_SUCH_FILE;
        }
        mPageLexer = new HTMLLexer(mInput);
        mPageTokens = new CommonTokenStream(mPageLexer);
        mPageParser = new HTMLParser(mPageTokens);
        mPageWalker = new ParseTreeWalker();
        mDividendListener = new HTMLParserDividendListener();
        mPageWalker.walk(mDividendListener, mPageParser.htmlDocument());
        mDividendListener.processTable();

        return HANDLE_PAGE_SUCCESS;
    }

    public HTMLParserEarningListener mEarningListener = new HTMLParserEarningListener();
    public int getEarningPageFromFile(String pageFileName) {
        DividendPageParser divParser = new DividendPageParser();
        divParser.parsePage(pageFileName, mEarningListener);
        mEarningListener.processTable();
        return HANDLE_PAGE_SUCCESS;
    }
}
