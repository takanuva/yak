/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU GPL.                                                            *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*                                                                              *
* Copyright (C) Ekolivre TI, Paulo H. Torrens - 2013.                          *
* Ekolivre TI (http://www.ekolivre.com.br) claims rights over this software;   *
*   you may use for educational or personal uses. For comercial use (even as   *
*   a library), please contact the author.                                     *
********************************************************************************
* This file is part of Ekolivre's YAK.                                         *
*                                                                              *
* YAK is free software: you can redistribute it and/or modify it under the     *
*   terms of the GNU General Public License as published by the Free Software  *
*   Foundation, either version 3 of the License, or (at your option) any later *
*   version.                                                                   *
*                                                                              *
* YAK is distributed in the hope that it will be useful, but WITHOUT ANY       *
*   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS  *
*   FOR A PARTICULAR PURPOSE. See the GNU General Public License for more      *
*   details.                                                                   *
*                                                                              *
* You should have received a copy of the GNU General Public License along with *
*   YAK.  If not, see <http://www.gnu.org/licenses/>.                          *
*******************************************************************************/
package br.com.ekolivre.yak.editor.kits;

import java.util.*;
import br.com.ekolivre.yak.editor.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static br.com.ekolivre.yak.editor.TokenType.*;

%%

%class IEC_61131_3SyntaxKit

%public
%unicode
%type Token
%extends JFlexBasedSyntaxKit<Void>
%caseless

%{
  //
  public static final int ST = 0x01;
  public static final int IL = 0x02;
  //public static final int SFC = 0x04;
  public static final int STL = 0x08;
  public static final int ALL = ST | IL /*| SFC*/ | STL;
  
  //
  @SuppressWarnings("unchecked")
  private static final Map<Integer, String> dialects = 
    unmodifiableMap(new HashMap() {{
      put(ST,  "Structured Text");
      put(IL,  "Instruction List");
      //put(SFC, "Sequential Function Chart");
      put(ALL, "Mixed Code");
    }});
  
  //
  @SuppressWarnings("unchecked")
  private static final Map<String, Integer> extensions_map =
    unmodifiableMap(new HashMap() {{
      put("iec", ALL);
      put("scl", ALL);
      put("plc", ALL);
      put("stl", ALL);
      put("awl", ALL);
    }});
  
  //
  public IEC_61131_3SyntaxKit() {
    return;
  };
  
  //
  @Override
  public Map<Integer, String> getDialectList() {
    return dialects;
  };
  
  //
  @Override
  public int getDefaultDialect() {
    return ALL;
  };
  
  //
  @Override
  public Map<String, Integer> getFileExtensions() {
    return extensions_map;
  };
  
  //
  @Override
  public String getContentType() {
    return "text/x-iec-61131-3";
  };
  
  // 
  @Override
  public String getKitName() {
    return "IEC 61131-3";
  };
  
  
  
  public String getKeywordDescription(String keyword) {
    switch(keyword.toUpperCase()) {
      case "ANY":
        return "" + "";
    };
    
    return "<error>";
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
%state YYSTRING2
%state YYSTRING_INCOMPLETE
%state YYSTRING_INCOMPLETE2

%state YYTYPE
%state YYSTRUCT
%state YYVAR
%state YYFB
%state YYFUN
%state YYPROGRAM
%state YYSTEP
%state YYTRANSITION
%state YYACTION
%state YYCONFIGURATION
%state YYRESOURCE

%state YYIF
%state YYCASE
%state YYFOR
%state YYWHILE
%state YYREPEAT

%state YYIL
%state YYST

%%

<YYINITIAL> {
  //
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_REPEAT"         |
  "END_WHILE"          {
    return token(KEYWORD.misspell());
  }
}

<YYTYPE> {
  //
  "ELSIF"              |
  "ELSE"               |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_TYPE" {
    yypop();
    return token(KEYWORD);
  }
}

<YYSTRUCT> {
  //
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_STRUCT" {
    yypop();
    return token(KEYWORD);
  }
}

<YYVAR> {
  //
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_VAR" {
    yypop();
    return token(KEYWORD);
  }
}

<YYFUN> {
  //
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_FUNCTION" {
    yypop();
    return token(KEYWORD);
  }
}

<YYFB> {
  //
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_FUNCTION_BLOCK" {
    yypop();
    return token(KEYWORD);
  }
}

<YYPROGRAM> {
  //
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_PROGRAM" {
    yypop();
    return token(KEYWORD);
  }
}

<YYSTEP> {
  //
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_STEP" {
    yypop();
    return token(KEYWORD);
  }
}

<YYTRANSITION> {
  //
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_TRANSITION" {
    yypop();
    return token(KEYWORD);
  }
}

<YYACTION> {
  //
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_ACTION" {
    yypop();
    return token(KEYWORD);
  }
}

<YYCONFIGURATION> {
  //
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_CONFIGURATION" {
    yypop();
    return token(KEYWORD);
  }
}

<YYRESOURCE> {
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_RESOURCE" {
    yypop();
    return token(KEYWORD);
  }
}

<YYIF> {
  //
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_CASE"           |
  "END_FOR"            |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "ELSIF" |
  "ELSE"  {
    return token(KEYWORD);
  }
  
  //
  "END_IF" {
    yypop();
    return token(KEYWORD);
  }
}

<YYCASE> {
  //
  "ELSIF"              |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_FOR"            |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "ELSE" {
    return token(KEYWORD);
  }
  
  //
  "END_CASE" {
    yypop();
    return token(KEYWORD);
  }
}

<YYFOR> {
  //
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_WHILE"          |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_FOR" {
    yypop();
    return token(KEYWORD);
  }
}

<YYWHILE> {
  //
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_REPEAT"         {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_WHILE" {
    yypop();
    return token(KEYWORD);
  }
}

<YYREPEAT> {
  "ELSIF"              |
  "ELSE"               |
  "END_TYPE"           |
  "END_STRUCT"         |
  "END_VAR"            |
  "END_FUNCTION"       |
  "END_FUNCTION_BLOCK" |
  "END_PROGRAM"        |
  "END_STEP"           |
  "END_TRANSITION"     |
  "END_ACTION"         |
  "END_CONFIGURATION"  |
  "END_RESOURCE"       |
  "END_IF"             |
  "END_CASE"           |
  "END_FOR"            |
  "END_WHILE"          {
    return token(KEYWORD.misspell());
  }
  
  //
  "END_REPEAT" {
    yypop();
    return token(KEYWORD);
  }
}

<YYINITIAL,YYTYPE,YYSTRUCT,YYVAR,YYFB,YYFUN,YYPROGRAM,YYSTEP,YYTRANSITION,
 YYACTION,YYCONFIGURATION,YYRESOURCE,YYIF,YYCASE,YYFOR,YYWHILE,YYREPEAT,YYIL,
 YYST> {
  //
  "(*" {
    return checkComment("(*", "*)");
  }
  
  //
  "//" {
    return checkComment("//");
  }
  
  //
  "'" / ("$".|[^\'\r\n])*$ {
    yypush(YYSTRING_INCOMPLETE);
    return token(STRING_INCOMPLETE);
  }
  
  //
  "'" {
    yypush(YYSTRING);
    return token(STRING);
  }
  
  //
  "\"" / ("$".|[^\"\r\n])*$ {
    yypush(YYSTRING_INCOMPLETE2);
    return token(STRING_INCOMPLETE);
  }
  
  //
  "\"" {
    yypush(YYSTRING2);
    return token(STRING);
  }
  
  //
  [-+=<>?,.;:\^&|*]+ {
    return token(PUNCTUATION);
  }
  
  //
  "TYPE" {
    yypush(YYTYPE);
    return token(KEYWORD);
  }
  
  //
  "STRUCT" {
    yypush(YYSTRUCT);
    return token(KEYWORD);
  }
  
  //
  "VAR"          |
  "VAR_INPUT"    |
  "VAR_OUTPUT"   |
  "VAR_IN_OUT"   |
  "VAR_EXTERNAL" |
  "VAR_ACCESS"   |
  "VAR_GLOBAL"   {
    yypush(YYVAR);
    return token(KEYWORD);
  }
  
  //
  "FUNCTION_BLOCK" {
    yypush(YYFB);
    return token(KEYWORD);
  }
  
  //
  "FUNCTION" {
    yypush(YYFUN);
    return token(KEYWORD);
  }
  
  //
  "PROGRAM" {
    yypush(YYPROGRAM);
    return token(KEYWORD);
  }
  
  //
  "INITIAL_STEP" |
  "STEP"         {
    yypush(YYSTEP);
    return token(KEYWORD);
  }
  
  //
  "TRANSITION" {
    yypush(YYTRANSITION);
    return token(KEYWORD);
  }
  
  //
  "ACTION" {
    yypush(YYACTION);
    return token(KEYWORD);
  }
  
  //
  "CONFIGURATION" {
    yypush(YYCONFIGURATION);
    return token(KEYWORD);
  }
  
  //
  "RESOURCE" {
    yypush(YYRESOURCE);
    return token(KEYWORD);
  }
  
  //
  "IF" {
    yypush(YYIF);
    if(yystate() == YYIL)
      return token(KEYWORD.misspell());
    return token(KEYWORD);
  }
  
  //
  "CASE" {
    yypush(YYCASE);
    return token(KEYWORD);
  }
  
  //
  "FOR" {
    yypush(YYFOR);
    return token(KEYWORD);
  }
  
  //
  "WHILE" {
    yypush(YYWHILE);
    return token(KEYWORD);
  }
  
  //
  "REPEAT" {
    yypush(YYREPEAT);
    return token(KEYWORD);
  }
  
  //
  "TRUE"          |
  "FALSE"         |
  "TIME_OF_DAY"   |
  "TOD"           |
  "DATE"          |
  "D"             |
  "DATE_AND_TIME" |
  "DT"            |
  "STRING"        |
  "TIME"          |
  "SINT"          |
  "INT"           |
  "DINT"          |
  "LINT"          |
  "USINT"         |
  "UINT"          |
  "UDINT"         |
  "ULINT"         |
  "REAL"          |
  "LREAL"         |
  "BOOL"          |
  "BYTE"          |
  "WORD"          |
  "DWORD"         |
  "LWORD"         |
  "ARRAY"         |
  "OF"            |
  "R_EDGE"        |
  "F_EDGE"        |
  "RETAIN"        |
  "CONSTANT"      |
  "AT"            |
  "FROM"          |
  "TO"            |
  "READ_WRITE"    |
  "READ_ONLY"     |
  "TASK"          |
  "SINGLE"        |
  "INTERVAL"      |
  "PRIORITY"      |
  "WITH"          |
  "OR"            |
  "XOR"           |
  "AND"           |
  "MOD"           |
  "NOT"           |
  "RETURN"        |
  "THEN"          |
  "DO"            |
  "BY"            |
  "UNTIL"         |
  "EXIT"          {
    return token(KEYWORD);
  }
  
  //
  "SR"            |
  "RS"            |
  "TP"            |
  "R_TRIG"        |
  "F_TRIG"        |
  "CTD"           |
  "CTUD"          |
  "CTU"           |
  "TOF"           |
  "TON"           |
  "ANY"           |
  "ANY_NUM"       |
  "ANY_REAL"      |
  "ANY_INT"       |
  "ANY_BIT"       |
  "ANY_DATE"      {
    return token(STANDARD);
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
  .|\r|\n {
    return null;
  }
}

<YYSTRING> {
  //
  "'" {
    yypop();
    return token(STRING);
  }
  
  //
  "$". {
    return token(STRING_ESCAPE);
  }
  
  //
  [^\'\$\r\n]+ {
    return token(STRING);
  }
}

<YYSTRING_INCOMPLETE> {
  //
  "$"[^\r\n] {
    return token(STRING_ESCAPE);
  }
  
  //
  "$" {
    return token(STRING_ESCAPE);
  }
  
  //
  [^$\r\n]+ {
    return token(STRING_INCOMPLETE);
  }
  
  //
  [\r\n] {
    yypop();
    return token(KEYWORD);
  }
}

<YYSTRING2> {
  //
  "\"" {
    yypop();
    return token(STRING);
  }
  
  //
  "$". {
    return token(STRING_ESCAPE);
  }
  
  //
  [^\"\$\r\n]+ {
    return token(STRING);
  }
}

<YYSTRING_INCOMPLETE2> {
  //
  "$"[^\r\n] {
    return token(STRING_ESCAPE);
  }
  
  //
  "$" {
    return token(STRING_ESCAPE);
  }
  
  //
  [^$\r\n]+ {
    return token(STRING_INCOMPLETE);
  }
  
  //
  [\r\n] {
    yypop();
    return token(KEYWORD);
  }
}

<<EOF>> {
  return null;
}
