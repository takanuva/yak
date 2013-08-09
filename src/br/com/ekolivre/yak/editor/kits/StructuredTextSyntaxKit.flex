/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU LGPL.                                                           *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*******************************************************************************/
package br.com.ekolivre.yak.editor.kits;

import java.util.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static br.com.ekolivre.yak.editor.TokenType.*;

%%

%class StructuredTextSyntaxKit

%public
%unicode
%type br.com.ekolivre.yak.editor.Token
%extends br.com.ekolivre.yak.editor.JFlexBasedSyntaxKit<Void>
%caseless

%{
  //
  @SuppressWarnings("unchecked")
  private static final Map<String, Integer> extensions_map =
    unmodifiableMap(new HashMap() {{
      put("st", null);
    }});
  
  //
  public StructuredTextSyntaxKit() {
    return;
  };
  
  //
  @Override
  public Map<Integer, String> getDialectList() {
    return null;
  };
  
  //
  @Override
  public int getDefaultDialect() {
    return 0;
  };
  
  //
  @Override
  public Map<String, Integer> getFileExtensions() {
    return extensions_map;
  };
  
  //
  @Override
  public String getContentType() {
    return "text/x-structured-text";
  };
  
  // 
  @Override
  public String getKitName() {
    return "Structured Text";
  };
  
  //
  public int yypos() {
    return zzStartRead;
  };
%}


//
INT = _*[:digit:]([:digit:]|_)*
FIXED = {INT}"."{INT}

//
TIME = ("TIME"|"T")"#""-"?
USEC = {INT}"US"         | {FIXED}"US"
MSEC = {INT}"MS" {USEC}? | {FIXED}"MS" | {USEC}
SEC  = {INT}"S"  {MSEC}? | {FIXED}"S"  | {MSEC}
MIN  = {INT}"M"  {SEC}?  | {FIXED}"M"  | {SEC}
HOUR = {INT}"H"  {MIN}?  | {FIXED}"H"  | {MIN}
DAY  = {INT}"D"  {HOUR}? | {FIXED}"D"  | {HOUR}

//
TIME_LIT = {TIME}{DAY}

//
%state YYSTRING

%%

<YYINITIAL> {
  //
  "//" {
    return checkComment("//");
  }
  
  //
  "(*" {
    return checkComment("(*", "*)");
  }
  
  //
  "TRUE"               |
  "FALSE"              |
  "TIME_OF_DAY"        |
  "TOD"                |
  "DATE"               |
  "D"                  |
  "DATE_AND_TIME"      |
  "DT"                 |
  "STRING"             |
  "TIME"               |
  "SINT"               |
  "INT"                |
  "DINT"               |
  "LINT"               |
  "USINT"              |
  "UINT"               |
  "UDINT"              |
  "ULINT"              |
  "REAL"               |
  "LREAL"              |
  "BOOL"               |
  "BYTE"               |
  "WORD"               |
  "DWORD"              |
  "LWORD"              |
  "ANY"                |
  "ANY_NUM"            |
  "ANY_REAL"           |
  "ANY_INT"            |
  "ANY_BIT"            |
  "ANY_DATE"           |
  "TYPE"               |
  "END_TYPE"           |
  "ARRAY"              |
  "OF"                 |
  "STRUCT"             |
  "END_STRUCT"         |
  "VAR_INPUT"          |
  "END_VAR"            |
  "R_EDGE"             |
  "F_EDGE"             |
  "VAR_OUTPUT"         |
  "RETAIN"             |
  "VAR_IN_OUT"         |
  "VAR"                |
  "CONSTANT"           |
  "VAR_EXTERNAL"       |
  "VAR_GLOBAL"         |
  "AT"                 |
  "FUNCTION"           |
  "END_FUNCTION"       |
  "FUNCTION_BLOCK"     |
  "END_FUNCTION_BLOCK" |
  "PROGRAM"            |
  "END_PROGRAM"        |
  "VAR_ACCESS"         |
  "INTIAL_STEP"        |
  "END_STEP"           |
  "TRANSITION"         |
  "FROM"               |
  "TO"                 |
  "END_TRANSITION"     |
  "ACTION"             |
  "END_ACTION"         |
  "CONFIGURATION"      |
  "END_CONFIGURATION"  |
  "RESOURCE"           |
  "END_RESOURCE"       |
  "READ_WRITE"         |
  "READ_ONLY"          |
  "TASK"               |
  "SINGLE"             |
  "INTERVAL"           |
  "PRIORITY"           |
  "WITH"               |
  "OR"                 |
  "XOR"                |
  "AND"                |
  "MOD"                |
  "NOT"                |
  "RETURN"             |
  "IF"                 |
  "THEN"               |
  "ELSIF"              |
  "ELSE"               |
  "END_IF"             |
  "CASE"               |
  "END_CASE"           |
  "FOR"                |
  "DO"                 |
  "END_FOR"            |
  "BY"                 |
  "WHILE"              |
  "END_WHILE"          |
  "REPEAT"             |
  "UNTIL"              |
  "END_REPEAT"         |
  "EXIT"               {
    return token(KEYWORD);
  }
  
  //
  "END_"?("UNION"|"CLASS") {
    return token(IMPL_KEYWORD);
  }
  
  //
  [:jletter:][:jletterdigit:]* {
    return token(IDENTIFIER);
  }
  
  //
  "%"[QIMqim][XBWDLxbwdl][:digit:]+              |
  "%"[QIMqim][XBWDLxbwdl][:digit:]+"."[:digit:]+ |
  "%"[Rr][XBWDLxbwdl][:digit:]+                  {
    return token(SYMBOL);
  }
  
  //
  "%"[:jletterdigit:]* {
    return token(SYMBOL.misspell());
  }
  
  //
  {INT}                                                                    |
  {FIXED}                                                                  |
  {TIME_LIT}                                                               |
  ("TIME_OF_DAY"|"TOD")"#"{INT}":"{INT}":"{INT}                            |
  ("TIME_OF_DAY"|"TOD")"#"{INT}":"{INT}":"{FIXED}                          |
  ("DATE"|"D")"#"{INT}"-"{INT}"-"{INT}                                     |
  ("DATE_AND_TIME"|"DT")"#"{INT}"-"{INT}"-"{INT}"-"{INT}":"{INT}":"{INT}   |
  ("DATE_AND_TIME"|"DT")"#"{INT}"-"{INT}"-"{INT}"-"{INT}":"{INT}":"{FIXED} {
    return token(NUMBER);
  }
  
  //
  ([:jletterdigit:]|_)*"#"([:jletterdigit:]|_|"."|":"|"-")+ |
  ([:jletterdigit:]|_)+"#"([:jletterdigit:]|_|"."|":"|"-")* {
    return token(NUMBER.misspell());
  }
  
  //
  //
  "'"("$".|[^\'\\\r\n])*$ {
    return token(STRING_INCOMPLETE);
  }
  
  //
  "'" {
    yybegin(YYSTRING);
    return token(STRING);
  }
  
  //
  [-+=<>?,.;:\^&|*]+ {
    return token(PUNCTUATION);
  }
  
  //
  "(" {
    return token(PUNCTUATION, +'(');
  }
  
  //
  ")" {
    return token(PUNCTUATION, -'(');
  }
  
  //
  "[" {
    return token(PUNCTUATION, +'[');
  }
  
  //
  "]" {
    return token(PUNCTUATION, -'[');
  }
  
  /*//
  "{" {
    return token(PUNCTUATION, +'{');
  }
  
  //
  "}" {
    return token(PUNCTUATION, -'{');
  }*/
  
  //
  .|\r|\n {
    return null;
  }
}

<YYSTRING> {
  //
  "'" {
    yybegin(YYINITIAL);
    return token(STRING);
  }
  
  //
  "$". {
    return token(STRING_ESCAPE);
  }
  
  //
  [^\'$\r\n]+ {
    return token(STRING);
  }
}

<<EOF>> {
  return null;
}
