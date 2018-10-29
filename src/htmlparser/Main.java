package htmlparser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Main {
    public static void main(String[] args) {
        CharStream mInput = null;
        try {
            if (args.length == 0) {
                //mInput = CharStreams.fromString("< html ><meta aaa=\"bbb\"><link aaa=\"bbb\"><head></head><body><table>aaa<table>bbb<table>zzz</table></table><table>qqq</table><table>rrr</table>ccc</table><table>ddd</table></body></html>");
                //mInput = CharStreams.fromString("1.03元");
                mInput = CharStreams.fromFileName("/Users/chenhj1234/Android/sample/repo/htmlparser/company_3176.html");
                //mInput = CharStreams.fromFileName("/Users/chenhj1234/Android/sample/repo/htmlparser/5519_u8.html");
            } else {
                mInput = CharStreams.fromFileName(args[0]);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        /*
        InfoStringGrammarLexer infoLexer = new InfoStringGrammarLexer(mInput);
        CommonTokenStream iTokens = new CommonTokenStream(infoLexer);
        InfoStringGrammarParser infoParser = new InfoStringGrammarParser(iTokens);
        System.out.println("parser tree:" + infoParser.numberRule().getText());
        mInput = CharStreams.fromString("< html ><meta aaa=\"bbb\"><link aaa=\"bbb\"><head></head><body><table>aaa<table>bbb<table>zzz</table></table><table>qqq</table><table>rrr</table>ccc</table><table>ddd</table></body></html>");
        */
        HTMLLexer hLexer = new HTMLLexer(mInput);
        // create a buffer of tokens pulled from the lexer
        /*
        Token token;
        while (true) {
            token = hLexer.nextToken();
            if (token.getType() == Token.EOF) {
                break;
            }

            System.out.println("Token: ‘" + token.getText() + "’" + " index:" + token.getType());
        }
        hLexer.reset();
        */
        CommonTokenStream tokens = new CommonTokenStream(hLexer);
        HTMLParser parser = new HTMLParser(tokens);
        ParseTreeWalker walker = new ParseTreeWalker();
        System.out.println("begin ...");
        //parser.htmlDocument();
        System.out.println("start walk ...");
        HTMLParserTableListener hListener = new HTMLParserTableListener();
        walker.walk(hListener, parser.htmlDocument());
        System.out.println("hListener.processTable");
        hListener.processTable();
        System.out.println("hListener.processTableContent");
        hListener.processTableContent();
        System.out.println("hListener.processTableColumesAndRows");
        hListener.processTableColumesAndRows();
        System.out.println("hListener.processPageTitle");
        hListener.processPageTitle();

        /*
        mInput = CharStreams.fromString("37 元");
        InfoStringGrammarLexer infoLexer = new InfoStringGrammarLexer(mInput);
        CommonTokenStream iTokens = new CommonTokenStream(infoLexer);
        InfoStringGrammarParser infoParser = new InfoStringGrammarParser(iTokens);
        InfoStringGrammarDataListener infoListener = new InfoStringGrammarDataListener();
        walker.walk(infoListener, infoParser.numberRule());
        InfoStringGrammarParser.NumberRuleContext ctx = infoParser.numberRule();
        System.out.println("parser tree:" + infoParser.infostr().getText() +
                " " + ctx.getRuleIndex() +
                " " + ctx.getAltNumber());*/

    }
}
