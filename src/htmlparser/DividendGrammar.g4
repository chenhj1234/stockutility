grammar DividendGrammar;

@parser::members {
    public final static int TYPE_INDEX_INT = 6;
    public final static int TYPE_INDEX_REAL = 7;
    public final static int TYPE_INDEX_CASH = 0;
    public final static int TYPE_INDEX_EARN = 1;
    public final static int TYPE_INDEX_STOCK_CAP = 2;
    public final static int TYPE_INDEX_STOCK = 3;
    public final static int TYPE_INDEX_TOTAL = 4;
    public final static int TYPE_INDEX_YEAR = 5;
    public int typeIndex = -1;
}

number
    : REAL              { typeIndex = TYPE_INDEX_REAL; }
    | INT               { typeIndex = TYPE_INDEX_INT; }
    | YEAR                      { typeIndex = TYPE_INDEX_YEAR; }
    | CASH_DIVIDEND             { typeIndex = TYPE_INDEX_CASH; }
    | EARN_DIVIDEND             { typeIndex = TYPE_INDEX_EARN; }
    | STOCK_DIVIDEND_CAPITAL    { typeIndex = TYPE_INDEX_STOCK_CAP; }
    | STOCK_DIVIDEND            { typeIndex = TYPE_INDEX_STOCK; }
    | TOTAL_DIVIDEND            { typeIndex = TYPE_INDEX_TOTAL; }
    ;

REAL
    : [0-9]+'.'[0-9]+
    ;

INT
    : [0-9]+
    ;

//年　度	現 金 股 利	盈 餘 配 股	公 積 配 股	股 票 股 利	合　計
YEAR
    : '年' WS* '度'
    ;

CASH_DIVIDEND
    : '現' WS* '金' WS* '股' WS* '利'
    ;

EARN_DIVIDEND
    : '盈' WS* '餘' WS* '配' WS* '股'
    ;

STOCK_DIVIDEND_CAPITAL
    : '公'WS*'積'WS*'配'WS*'股'
    ;

STOCK_DIVIDEND
    : '股'WS*'票'WS*'股'WS*'利'
    ;

TOTAL_DIVIDEND
    : '合'WS*'計'
    ;

WS
    : [ \t\n\r　]+ -> skip ;