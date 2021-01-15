grammar DividendGrammar;

@parser::members {
    public final static int TYPE_INDEX_CASH = 0;
    public final static int TYPE_INDEX_EARN = 1;
    public final static int TYPE_INDEX_STOCK_CAP = 2;
    public final static int TYPE_INDEX_STOCK = 3;
    public final static int TYPE_INDEX_TOTAL = 4;
    public final static int TYPE_INDEX_YEAR = 5;
    public final static int TYPE_INDEX_CASH_DATE = 6;
    public final static int TYPE_INDEX_INT = 7;
    public final static int TYPE_INDEX_REAL = 8;
    public final static int TYPE_INDEX_DATE = 9;
    public final static int TYPE_INDEX_ANNUAL_YEAR = 10;
    public int typeIndex = -1;
}

number
    : REAL              { typeIndex = TYPE_INDEX_REAL; }
    | INT               { typeIndex = TYPE_INDEX_INT; }
    | annualYear      { typeIndex = TYPE_INDEX_ANNUAL_YEAR; }
    | dateRepresentation { typeIndex = TYPE_INDEX_DATE; }
    | earningYear                      { typeIndex = TYPE_INDEX_YEAR; }
    | CASH_DIVIDEND_DATE         { typeIndex = TYPE_INDEX_CASH_DATE; }
    | CASH_DIVIDEND             { typeIndex = TYPE_INDEX_CASH; }
    | EARN_DIVIDEND             { typeIndex = TYPE_INDEX_EARN; }
    | STOCK_DIVIDEND_CAPITAL    { typeIndex = TYPE_INDEX_STOCK_CAP; }
    | STOCK_DIVIDEND            { typeIndex = TYPE_INDEX_STOCK; }
    | TOTAL_DIVIDEND            { typeIndex = TYPE_INDEX_TOTAL; }
    ;

annualYear
    : INT WS* YEAR_WORD .*?
    ;

dateRepresentation
    : INT+'-'INT+'-'INT+ WS*
    | '-'
    ;

earningYear
    : .*? YEAR_WORD  WS* ANNUAL_WORD .*?
    ;

REAL
    : [0-9]+'.'[0-9]+
    ;

INT
    : [0-9]+
    ;

YEAR_WORD
    : '年'
    ;

ANNUAL_WORD
    : '度'
   ;
//年　度	現 金 股 利	盈 餘 配 股	公 積 配 股	股 票 股 利	合　計
//YEAR
//    : YEAR_WORD WS* ANNUAL_WORD
//    ;

CASH_DIVIDEND_DATE
    : '現' WS* '金' WS* '股' WS* '利' WS* '發' WS* '放' WS* '日' WS*
    ;

CASH_DIVIDEND
    : '現' WS* '金' WS* '股' WS* '利' WS*
    ;

EARN_DIVIDEND
    : '盈' WS* '餘' WS* '配' WS* '股' WS*
    ;

STOCK_DIVIDEND_CAPITAL
    : '公'WS*'積'WS*'配'WS*'股' WS*
    ;

STOCK_DIVIDEND
    : '股'WS*'票'WS*'股'WS*'利' WS*
    ;

TOTAL_DIVIDEND
    : '合'WS*'計' WS*
    ;

WS
    : [ \t\n\r　]+ -> skip ;

ANYCHAR
    : .
    ;