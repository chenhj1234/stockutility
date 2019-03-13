package htmlparser;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

public class EarningGrammarDataListener extends  EarningGrammarBaseListener {
    public class seasonInfo {
        public static final float POSITIVE_BACK = 10000;
        public static final float NEGATIVE_BACK = -10000;
        public int earning = 0;
        public int accuEarning = 0;
        public float yearIncRatio = 0;
        public float accuYearIncRatio = 0;
        public int season;
    }
    private boolean selectMonthlyTable = true;
    public int year1 , year2;
    public ArrayList<seasonInfo> alist1 = new ArrayList<>();
    public ArrayList<seasonInfo> alist2 = new ArrayList<>();
    int currentValueState = 0;
    int currentYearIndex = 0;

    public void printInfo() {
        for(int i = 0;i < alist1.size();i++) {
            seasonInfo sea = alist1.get(i);
            System.out.println("year1:" + year1 + " season:" + sea.season + " earning:" + sea.earning + " ratio:" + sea.yearIncRatio);
        }
        for(int i = 0;i < alist2.size();i++) {
            seasonInfo sea = alist2.get(i);
            System.out.println("year2:" + year2 + " season:" + sea.season + " earning:" + sea.earning + " ratio:" + sea.yearIncRatio);
        }
    }

    public void assignToMonthlyTable(boolean isMonthlyTable) {
        selectMonthlyTable = isMonthlyTable;
    }
    void assignYearOfSeasonTable(int year) {
        if(currentYearIndex == 0) {
            year1 = year;
            currentYearIndex ++;
        } else {
            year2 = year;
            currentYearIndex = 0;
        }
        System.out.println("year1:" + year1 + " year2:" + year2);
    }

    void assignIntOfSeasonTable(int val) {
        if(currentYearIndex == 0) {
            if(currentValueState == 0) {
                seasonInfo season = new seasonInfo();
                season.season = val;
                alist1.add(season);
                currentValueState ++;
            } else if(currentValueState == 1) {
                seasonInfo season = alist1.get(alist1.size()-1);
                season.earning = val;
                currentValueState ++;
            }
        } else {
            if(currentValueState == 0) {
                seasonInfo season = new seasonInfo();
                season.season = val;
                alist2.add(season);
                currentValueState ++;
            } else if(currentValueState == 1) {
                seasonInfo season = alist2.get(alist2.size()-1);
                season.earning = val;
                currentValueState ++;
            }
        }
    }

    void assignFloatOfMonthTable(float val) {
        seasonInfo season = null;
        switch(currentValueState) {
            case 2:
                season = alist1.get(alist1.size()-1);
                season.yearIncRatio = val;
                currentValueState ++;
                break;
            case 5:
                season = alist2.get(alist2.size()-1);
                season.yearIncRatio = val;
                currentValueState ++;
                break;
            case 7:
                season = alist2.get(alist2.size()-1);
                season.accuYearIncRatio = val;
                currentValueState = 0;
                break;
            default:
                System.out.println("Invalid state:" + currentValueState + " val:" + val);
                break;
        }
    }

    void assignIntOfMonthTable(int val) {
        seasonInfo season = null;
        switch(currentValueState) {
            case 0:
                season = new seasonInfo();
                season.season = val;
                alist1.add(season);
                currentValueState ++;
                break;
            case 1:
                season = alist1.get(alist1.size()-1);
                season.earning = val;
                currentValueState ++;
                break;
            case 3:
                season = new seasonInfo();
                season.season = val;
                alist2.add(season);
                currentValueState ++;
                break;
            case 4:
                season = alist2.get(alist2.size()-1);
                season.earning = val;
                currentValueState ++;
                break;
            case 6:
                season = alist2.get(alist2.size()-1);
                season.accuEarning = val;
                currentValueState ++;
                break;
            default:
                System.out.println("Invalid state:" + currentValueState + " val:" + val);
                break;
        }
    }

    void assignFloatOfSeasonTable(float val) {
        if(currentYearIndex == 0) {
            if (currentValueState == 2) {
                currentValueState = 0;
                seasonInfo season = alist1.get(alist1.size()-1);
                season.yearIncRatio = val;
            }
            currentYearIndex ++;
        } else {
            if (currentValueState == 2) {
                currentValueState = 0;
                seasonInfo season = alist2.get(alist2.size()-1);
                season.yearIncRatio = val;
            }
            currentYearIndex = 0;
        }
    }
    @Override public void enterNumberLine(EarningGrammarParser.NumberLineContext ctx) {
        List<TerminalNode> intList = ctx.INT();
        int total = 0;
        for(int i = 0;i < intList.size();i++) {
            total = total * 1000 + Integer.parseInt(intList.get(i).getText());
        }
        if(selectMonthlyTable) {
            assignIntOfMonthTable(total);
        } else {
            assignIntOfSeasonTable(total);
        }
    }
    @Override public void enterNumberPercent(EarningGrammarParser.NumberPercentContext ctx) {
        float total = 0;
        List<TerminalNode> intList = ctx.INT();
        String floatStr = intList.get(0).getText();
        if(intList.size() > 1) {
            floatStr = floatStr + "." + intList.get(1).getText();
        }
        total = Float.parseFloat(floatStr);
        if(ctx.NEGATIVE_SIGN() != null) {
            total = -1 * total;
        }
        if(selectMonthlyTable) {
            assignFloatOfMonthTable(total);
        } else {
            assignFloatOfSeasonTable(total);
        }
    }
    @Override public void enterYearDec(EarningGrammarParser.YearDecContext ctx) {
        assignYearOfSeasonTable(Integer.parseInt(ctx.INT().getText()));
    }
    @Override public void enterValueTurnBack(EarningGrammarParser.ValueTurnBackContext ctx) {
        if(selectMonthlyTable) {
            if (ctx.NEGATIVE_BACK() != null) {
                assignFloatOfMonthTable(seasonInfo.NEGATIVE_BACK);
            } else {
                assignFloatOfMonthTable(seasonInfo.POSITIVE_BACK);
            }
        } else {
            if (ctx.NEGATIVE_BACK() != null) {
                assignFloatOfSeasonTable(seasonInfo.NEGATIVE_BACK);
            } else {
                assignFloatOfSeasonTable(seasonInfo.POSITIVE_BACK);
            }
        }
    }
    @Override public void enterZeroOrInvalid(EarningGrammarParser.ZeroOrInvalidContext ctx) {

    }
    @Override public void enterReservedValud(EarningGrammarParser.ReservedValudContext ctx) {

    }

}
