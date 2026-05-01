/*
 * json.g4
 *
 * Antlr gramma file for json
 *
 * Copyright (c) Joe Xue (lgxue@hotmail.com) 2026
*/

grammar Json;

json
    : value EOF
    ;

object
    : '{' (pair (',' pair)*)? '}'
    ;

pair
    : STRING ':' value
    ;

array
    : '[' (value (',' value)*)? ']'
    ;

value
    : STRING
    | NUMBER
    | BOOLEAN
    | NULL
    | object
    | array
    ;

STRING
    : '"' (ESC | SAFECODEPOINT)* '"'
    ;

fragment ESC
    : '\\' (["\\/bfnrt] | UNICODE)
    ;

fragment UNICODE
    : 'u' HEX HEX HEX HEX
    ;

fragment HEX
    : [0-9a-fA-F]
    ;

fragment SAFECODEPOINT
    : ~ ["\\\u0000-\u001F]
    ;

NUMBER
    : '-'? INT ('.' [0-9]+)? EXP?
    ;

fragment INT
    : '0'
    | [1-9] [0-9]*
    ;


fragment EXP
    : [Ee] [+-]? [0-9]+
    ;

BOOLEAN
    : 'true'
    | 'false'
    ;

NULL
    : 'null'
    ;

WS
    : [ \t\n\r]+ -> skip
    ;
