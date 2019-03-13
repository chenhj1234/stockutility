package htmlparser;

public class DailyPriseDPListener extends DailyPriseTableEntryGrammarBaseListener {
    private final boolean DEBUG_GRAMMAR_SPEC = false;
    String timeStr = "";
    String numberStr = "";
    String shiftStr = "";
    String otherStr = "";
    @Override public void enterTimeSpec(DailyPriseTableEntryGrammarParser.TimeSpecContext ctx) {
        if(DEBUG_GRAMMAR_SPEC) System.out.println(ctx.getClass().getSimpleName() + " " + ctx.getText());
        timeStr = ctx.getText();
    }
    @Override public void enterRealNumberSpec(DailyPriseTableEntryGrammarParser.RealNumberSpecContext ctx) {
        if(DEBUG_GRAMMAR_SPEC) System.out.println(ctx.getClass().getSimpleName() + " " + ctx.getText());
        numberStr = ctx.getText();
    }
    @Override public void enterAnyStr(DailyPriseTableEntryGrammarParser.AnyStrContext ctx) {
        if(DEBUG_GRAMMAR_SPEC) System.out.println(ctx.getClass().getSimpleName() + " " + ctx.getText());
        otherStr = ctx.getText();
    }
    @Override public void enterPriseShiftSpec(DailyPriseTableEntryGrammarParser.PriseShiftSpecContext ctx) {
        if(DEBUG_GRAMMAR_SPEC) System.out.println(ctx.getClass().getSimpleName() + " " + ctx.getText());
        if(ctx.SYMBOX_PRISE_DEC() != null) {
            shiftStr = "-";
        } else {
            shiftStr = "+";
        }
    }

}
