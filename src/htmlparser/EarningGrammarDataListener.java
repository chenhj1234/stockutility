package htmlparser;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

public class EarningGrammarDataListener extends  EarningGrammarBaseListener {
    public class seasonInfo {
        public static final float POSITIVE_BACK = 10000;
        public static final float NEGATIVE_BACK = -10000;
        public static final float NOT_AVAILABLE_NUMBER = -20000;
        public int earning = 0;
        public int accuEarning = 0;
        public float yearIncRatio = 0;
        public float accuYearIncRatio = 0;
        public int season;
        public float achievement = 0;
        public void setEarning(int er) {
            earning = er;
        }
        public void setEarning(float er) {
            earning = (int)er;
        }
        public void setAccuEarning(int aer) {
            accuEarning = aer;
        }
        public void setAccuEarning(float aer) {
            accuEarning = (int)aer;
        }
        public void setYearIncRatio(float yir) {
            yearIncRatio = yir;
        }
        public void setYearIncRatio(int yir) {
            yearIncRatio = yir;
        }
        public void setAccuYearIncRatio(float ayir) {
            accuYearIncRatio = ayir;
        }
        public void setAccuYearIncRatio(int ayir) {
            accuYearIncRatio = ayir;
        }
        public void setSeason(int se) {
            season = se;
        }
        public void setSeason(float se) {
            season = (int)se;
        }
        public void setAchievement(float ah) {
            achievement = ah;
        }
        public void setAchievement(int ah) {
            achievement = ah;
        }
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

    private void incState() {
        currentValueState ++;
    }
    private void resetState() {
        currentValueState = 0;
    }
    private int getState() {
        return currentValueState;
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

    void assignFloatOfMonthTable(float val) {
        seasonInfo season = null;
        switch(currentValueState) {
            case 2:
                season = alist1.get(alist1.size()-1);
                season.yearIncRatio = val;
                incState();
                break;
            case 5:
                season = alist2.get(alist2.size()-1);
                season.yearIncRatio = val;
                incState();
                break;
            case 7:
                season = alist2.get(alist2.size()-1);
                season.accuYearIncRatio = val;
                incState();
                break;
            case 8:
                /* We don't really know 達成率 will appear in integer or floating, so we apply both parts */
                season = alist2.get(alist2.size()-1);
                season.setAchievement(val);
                resetState();
                break;
            default:
                System.out.println("assignFloatOfMonthTable Invalid state:" + currentValueState + " val:" + val);
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
                incState();
                break;
            case 1:
                season = alist1.get(alist1.size()-1);
                season.earning = val;
                incState();
                break;
            case 3:
                season = new seasonInfo();
                season.season = val;
                alist2.add(season);
                incState();
                break;
            case 4:
                season = alist2.get(alist2.size()-1);
                season.earning = val;
                incState();
                break;
            case 6:
                season = alist2.get(alist2.size()-1);
                season.accuEarning = val;
                incState();
                break;
            case 8:
                /* We don't really know 達成率 will appear in integer or floating, so we apply both parts */
                season = alist2.get(alist2.size()-1);
                season.setAchievement(val);
                resetState();
                break;
            default:
                System.out.println("assignIntOfMonthTable Invalid state:" + currentValueState + " val:" + val);
                break;
        }
    }

    void assignIntOfSeasonTable(int val) {
        seasonInfo season = null;
        switch(currentValueState) {
            case 0:
                // Apply for first year record
                season = new seasonInfo();
                season.setSeason(val);
                alist1.add(season);
                incState();
                break;
            case 1:
                // Apply for first year record
                season = alist1.get(alist1.size()-1);
                season.setEarning(val);
                incState();
                break;
            case 3:
                // Apply for second year record
                season = new seasonInfo();
                season.setSeason(val);
                alist2.add(season);
                incState();
                break;
            case 4:
                // Apply for second year record
                season = alist2.get(alist2.size()-1);
                season.setEarning(val);
                incState();
                break;
            case 6:
                // Apply for second year record, when type is not sure integer or float
                season = alist2.get(alist2.size()-1);
                season.setAchievement(val);
                resetState();
                break;
            default:
                System.out.println("assignIntOfSeasonTable Invalid state:" + currentValueState + " val:" + val);
                break;
        }
//        if(currentYearIndex == 0) {
//            if(currentValueState == 0) {
//                seasonInfo season = new seasonInfo();
//                season.season = val;
//                alist1.add(season);
//                incState();
//            } else if(currentValueState == 1) {
//                seasonInfo season = alist1.get(alist1.size()-1);
//                season.earning = val;
//                incState();
//            } else {
//                System.out.println("assignIntOfSeasonTable Invalid state:" + currentValueState + " val:" + val);
//            }
//        } else {
//            if(currentValueState == 0) {
//                seasonInfo season = new seasonInfo();
//                season.season = val;
//                alist2.add(season);
//                incState();
//            } else if(currentValueState == 1) {
//                seasonInfo season = alist2.get(alist2.size()-1);
//                season.earning = val;
//                incState();
//            } else {
//                System.out.println("assignIntOfSeasonTable Invalid state:" + currentValueState + " val:" + val);
//            }
//        }
    }

    void assignFloatOfSeasonTable(float val) {
        seasonInfo season = null;
        switch(currentValueState) {
            case 2:
                // Apply for first year record
                season = alist1.get(alist1.size()-1);
                season.setYearIncRatio(val);
                incState();
                break;
            case 5:
                // Apply for second year record
                season = alist2.get(alist2.size()-1);
                season.setYearIncRatio(val);
                incState();
                break;
            case 6:
                // Apply for second year record, when type is not sure integer or float
                season = alist2.get(alist2.size()-1);
                season.setAchievement(val);
                resetState();
                break;
            default:
                System.out.println("assignIntOfSeasonTable Invalid state:" + currentValueState + " val:" + val);
                break;
        }

//        if(currentYearIndex == 0) {
//            if (currentValueState == 2) {
//                resetState();
//                seasonInfo season = alist1.get(alist1.size()-1);
//                season.yearIncRatio = val;
//            } else {
//                System.out.println("assignFloatOfSeasonTable Invalid state:" + currentValueState + " val:" + val);
//            }
//            currentYearIndex ++;
//        } else {
//            if (currentValueState == 2) {
//                resetState();
//                seasonInfo season = alist2.get(alist2.size()-1);
//                season.yearIncRatio = val;
//            } else {
//                System.out.println("assignFloatOfSeasonTable Invalid state:" + currentValueState + " val:" + val);
//            }
//            currentYearIndex = 0;
//        }
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
        if(selectMonthlyTable) {
            switch (currentValueState) {
                case 0:
                case 1:
                case 3:
                case 4:
                case 6:
                    assignIntOfMonthTable((int)seasonInfo.NOT_AVAILABLE_NUMBER);
                    break;
                case 2:
                case 5:
                case 7:
                case 8:
                    assignFloatOfMonthTable(seasonInfo.NOT_AVAILABLE_NUMBER);
                    break;
                default:
                    System.out.println("enterZeroOrInvalid Invalid state:" + currentValueState + " val: -" + " selectMonthlyTable:" + selectMonthlyTable);
                    resetState();
                    break;
            }
        } else {
            switch (currentValueState) {
                case 0:
                case 1:
                case 3:
                case 4:
                    assignIntOfSeasonTable((int)seasonInfo.NOT_AVAILABLE_NUMBER);
                    break;
                case 2:
                case 5:
                case 6:
                    assignFloatOfSeasonTable(seasonInfo.NOT_AVAILABLE_NUMBER);
                    break;
                default:
                    System.out.println("enterZeroOrInvalid Invalid state:" + currentValueState + " val: -" + " selectMonthlyTable:" + selectMonthlyTable);
                    resetState();
                    break;
            }

        }
    }
    @Override public void enterReservedValud(EarningGrammarParser.ReservedValudContext ctx) {

    }

}
