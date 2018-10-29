grammar InfoStringGrammar;

fragment DIGIT
    : ('0'..'9')
    ;

infostr
    : numberRule EOF
    ;

/*
INT_NUMBER
    : DIGIT+?
    ;
*/

NUMBER_DOT
    : '.'
    ;
/*
REAL_NUMBER
    : DIGIT+ (NUMBER_DOT DIGIT+)?
    ;
*/
REAL_NUMBER
    : '-'? '0'..'9'+('.' '0'..'9'+)?
    ;

DOLLOR_UNIT
    : '元'
    ;

PERCENT_UNIT
    : '%'
    ;

SEASON_UNIT
    : '季'
    ;

YEAR_BEFORE_SEASON
    : '第'
    ;

YEAR_UNIT
    : '年'
    ;

MORNING_SEP
    : '上午'
    ;

HOUR_UNIT
    : '點'
    ;

MINUTE_UNIT
    : '分'
    ;

AFTORNOON_SEP
    : '下午'
    ;

BOOK_VALUE_TITLE
    : '每股淨值:'
    ;

WS
    : [ \t\r\n　] -> skip
    ;

NOT_NUMBER
    : ~('0'..'9'|'-')+?
    ;

ENGCHAR
    : ('a'..'z') +
    ;

RETURN_ON_EQULITY
    : '股東權益報酬率'
    ;

RETURN_ON_ASSETS
    : '資產報酬率'
    ;

EARNING_BEFORE_TAX_MARGIN
    : '稅前淨利率'
    ;

OPERATION_PROFIT_MARGIN
    : '營業利益率'
    ;

GROSS_PROFIT_MARGIN
    : '營業毛利率'
    ;

CASH_DIVIDE_END
    : '現金股利'
    ;

STOCK_DIVID_END
    : '股票股利'
    ;

EXCLUSION_DATE
    : '除權日期'
    ;

ELIMINATION_DATE
    : '除息日期'
    ;

ZERO_OR_NO_VALUE
    : '-'
    ;

numberRule
    : REAL_NUMBER # Real
    | REAL_NUMBER DOLLOR_UNIT # Real_Dollar
    | REAL_NUMBER YEAR_UNIT # Real_Year
    | REAL_NUMBER YEAR_BEFORE_SEASON REAL_NUMBER SEASON_UNIT # Real_Year_season
    | REAL_NUMBER PERCENT_UNIT # Real_Percent
    | BOOK_VALUE_TITLE REAL_NUMBER DOLLOR_UNIT # Book_Value
    | BOOK_VALUE_TITLE ZERO_OR_NO_VALUE # Book_Value_Zero
    | MORNING_SEP REAL_NUMBER HOUR_UNIT (REAL_NUMBER MINUTE_UNIT)? # Morning_Rule
    | AFTORNOON_SEP REAL_NUMBER HOUR_UNIT (REAL_NUMBER MINUTE_UNIT)? # Afternoon_Rule
    | RETURN_ON_EQULITY # ReturnOnEquity
    | RETURN_ON_ASSETS # ReturnOnAssets
    | EARNING_BEFORE_TAX_MARGIN # EarningBeforeTaxMargin
    | OPERATION_PROFIT_MARGIN # OperatingProfitMargin
    | GROSS_PROFIT_MARGIN # GrossProfitMargin
    | CASH_DIVIDE_END # CashDividend
    | STOCK_DIVID_END # StockDividend
    | EXCLUSION_DATE # ExclusionDate
    | ELIMINATION_DATE # EliminationDate
    | REAL_NUMBER '/' REAL_NUMBER '/' REAL_NUMBER # Date
    | REAL_NUMBER '/' REAL_NUMBER '/' REAL_NUMBER '-'  REAL_NUMBER '/' REAL_NUMBER '/' REAL_NUMBER # Date_Period
    | ZERO_OR_NO_VALUE # ZeroOrNoValue
    | NOT_NUMBER # Not_Number
    | # Empty_Rule
    ;

titleRule
    : ~('0'..'9') + '(' REAL_NUMBER ')' + ~('0'..'9') +
    ;