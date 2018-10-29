grammar TestGrammar;

statement : ID expr;

expr : OP expr
     | (NA | ID | OP)
     ;

takePhoto : TAKE_PHOTO_ACTION_TW TAKE_PHOTO_ITEM_TW;

NA : 'n'[a-zA-Z ]*'d' ;

ID
  : (
    'a'..'z'
    | 'A'..'Z'
    | '0'..'9'
    | ('+'|'-'|'*'|'/'|'_')
    )+ ;

OP : '='
  | '~'
  | '{'
  | '}'
  | ','
  ;

WS : [ \t\r\n]+ -> skip;

TAKE_PHOTO_ACTION_TW   : '拍' | '照';
TAKE_PHOTO_ITEM_TW : '相';




fragment
DIGIT
    : [0-9]
    ;