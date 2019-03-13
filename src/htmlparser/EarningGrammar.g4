grammar EarningGrammar;

earningColumn
    : yearLine
    | numberPercent
    | numberLine
    | valueTurnBack
    | zeroOrInvalid
    | reservedValud
    ;

numberLine
    : NEGATIVE_SIGN? WS*? INT(','INT)*?
    ;

numberPercent
    : NEGATIVE_SIGN? INT ('.'INT)? '%'
    ;

yearLine
    : '<b>' WS*? yearDec WS*? '</b>'
    ;

yearDec
    : INT WS*? '年' WS*? '度'
    ;

valueTurnBack
    : NEGATIVE_BACK
    | POSITIVE_BACK
    ;

zeroOrInvalid
    : NEGATIVE_SIGN
    ;

reservedValud
    : ANY *?
    ;

NEGATIVE_SIGN
    : '-'
    ;

NEGATIVE_BACK
    : '虧轉盈'
    ;

POSITIVE_BACK
    : '盈轉虧'
    ;

INT :   [0-9]+ ;
WS  :   [ \t\n\r]+ -> skip ;
ANY : .;
