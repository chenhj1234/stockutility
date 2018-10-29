grammar TestDateGrammar;

REAL_NUMBER
    : '0'..'9'+('.' '0'..'9'+)?
    ;

NOT_NUMBER
    : ~('0'..'9'|'-')+?
    ;

numberRule
    : REAL_NUMBER # Real
    | REAL_NUMBER '/' REAL_NUMBER '/' REAL_NUMBER # Date
    | NOT_NUMBER
    |
    ;
