grammar TestNumberGrammar;

INT: '0'..'9'+('.' '0'..'9'+)?;
INT_TW
    : ('一'|'二'|'三'|'四'|'五'|'六'|'七'|'八'|'九'|'十')+
    ;
HOUR_YW
    : '時'|'點'
    ;
MINUTE_TW
    : '分'
    ;

date
    : INT
    | INT '/' INT ( '/' INT )?
    ;

time_tw
    : INT_TW HOUR_YW (INT_TW MINUTE_TW)?
    ;

ALARM_KEY_TW
    : '鬧鐘'
    ;
alarm_tw
    : '設定' time_tw CONNE*? ALARM_KEY_TW
    ;

CONNE: '的';

WS : [ \t\r\n]+ -> skip;