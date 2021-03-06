package com.company;

import java.util.ArrayList;

public class GetArgs {
    public final static String OPT_SINGLE_STOCK = "-s";
    public final static String OPT_DATE = "--date";
    public final static String OPT_BUY_DATE = "--buydate";
    public final static String OPT_SELL_DATE = "--selldate";
    public final static String OPT_START_DATE = "--startdate";
    public final static String OPT_END_DATE = "--enddate";
    ArrayList<String> options = new ArrayList<>(), params = new ArrayList<>();
    ArrayList<Boolean> switches = new ArrayList<>(), optHasParms = new ArrayList<>();
    public GetArgs() {
        options = new ArrayList<>();
        params = new ArrayList<>();
        switches = new ArrayList<>();
        optHasParms = new ArrayList<>();
        addOption(OPT_SINGLE_STOCK, true);
        addOption(OPT_DATE, true);
        addOption(OPT_BUY_DATE, true);
        addOption(OPT_SELL_DATE, true);
        addOption(OPT_START_DATE, true);
        addOption(OPT_END_DATE, true);
    }
    public void addOption(String opt, boolean hasParm) {
        options.add(opt);
        params.add("");
        optHasParms.add(hasParm);
        switches.add(false);
    }
    public int findArg(String arg) {
        int i;
        for(i = 0;i < options.size();i++) {
            if(options.get(i).equals(arg)) {
                break;
            }
        }
        if(i == options.size()) {
            return -1;
        }
        return i;
    }
    public boolean isArgOn(String arg) {
        int opt = findArg(arg);
        if(opt < 0) {
            return false;
        }
        return switches.get(opt);
    }
    public String findParm(String arg) {
        int opt = findArg(arg);
        if(opt < 0 || params.get(opt) == null || params.get(opt).equals("")) {
            return null;
        }
        return params.get(opt);
    }
    public void processArgs(String[] args) {
        int opt;
        for(int i = 0;i < args.length;i++) {
            opt = findArg(args[i]);
            if(opt < 0)
                continue;
            switches.set(opt,true);
            if(optHasParms.get(opt).booleanValue()) {
                i++;
                params.set(opt, args[i]);
            }
        }
    }
}
