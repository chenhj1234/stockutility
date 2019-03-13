grammar DailyPriseTableEntryGrammar;

INTITEM
    : '0'..'9'+
    ;

numStr
    : INTITEM
    ;

SYMBOL_COLON
    : ':'
    ;

SYMBOL_DOT
    : '.'
    ;

SYMBOX_PRISE_INC
    : '△'
    ;

SYMBOX_PRISE_DEC
    : '▽'
    ;

timeSpec
    : numStr SYMBOL_COLON numStr SYMBOL_COLON numStr
    ;

realNumberSpec
    : numStr (SYMBOL_DOT numStr)?
    ;

priseShiftSpec
    : (SYMBOX_PRISE_INC | SYMBOX_PRISE_DEC) WS*? realNumberSpec
    ;

anyStr
    : .+?
    ;

dailyPriseTableEntry
    : timeSpec
    | realNumberSpec
    | priseShiftSpec
    | anyStr
    ;

WS
    : [ \t\n\r　] -> skip
    ;

ANYCHAR
    : .
    ;