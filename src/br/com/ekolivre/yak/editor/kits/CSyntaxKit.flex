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

/**
 * 
 */
%%

%class CSyntaxKit

%public
%unicode
%type Token
%extends JFlexBasedSyntaxKit<Void>

%{
  //
  public static final int KRC = 0x01;
  public static final int C89 = 0x02;
  public static final int C99 = 0x04;
  public static final int C11 = 0x08;
  public static final int MSC = 0x10 | C11;
  public static final int GNU = 0x20 | C11;
  public static final int INO = 0x40 | GNU;
  public static final int UPC = 0x80 | C11;
  
  //
  @SuppressWarnings("unchecked")
  private static final Map<Integer, String> dialects = 
    unmodifiableMap(new HashMap() {{
      put(KRC, "Old-Style K&R C");
      put(C89, "ANSI/ISO C 89");
      put(C99, "ANSI/ISO C 99");
      put(C11, "ANSI/ISO C 11");
      put(MSC, "Microsoft C");
      put(GNU, "GNU C");
      put(INO, "Arduino C");
      put(UPC, "Unified Parallel C");
    }});
  
  //
  @SuppressWarnings("unchecked")
  private static final Map<String, Integer> extensions_map =
    unmodifiableMap(new HashMap() {{
      put("c",   C11);
      put("krc", KRC);
      put("c89", C89);
      put("c99", C99);
      put("c11", C11);
      put("msc", MSC);
      put("gnu", GNU);
      put("ino", INO);
      put("pde", INO);
      put("upc", UPC);
    }});
  
  //
  public CSyntaxKit() {
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
    return GNU;
  };
  
  //
  @Override
  public Map<String, Integer> getFileExtensions() {
    return extensions_map;
  };
  
  //
  @Override
  public String getContentType() {
    return "text/x-c";
  };
  
  // 
  @Override
  public String getKitName() {
    return "C";
  };
  
  //
  public int yypos() {
    return zzStartRead;
  };
%}

%state YYPREPROCESSOR
%state YYINCLUDE

%%

^[\ \s]*"#" {
  yybegin(YYPREPROCESSOR);
  return token(PREPROCESSOR);
}

<YYINITIAL> {
  "auto"     |
  "break"    |
  "case"     |
  "char"     |
  "const"    |
  "continue" |
  "default"  |
  "do"       |
  "double"   |
  "else"     |
  "enum"     |
  "extern"   |
  "float"    |
  "for"      |
  "goto"     |
  "if"       |
  "int"      |
  "long"     |
  "register" |
  "return"   |
  "short"    |
  "signed"   |
  "sizeof"   |
  "static"   |
  "struct"   |
  "switch"   |
  "typedef"  |
  "union"    |
  "unsigned" |
  "void"     |
  "volatile" |
  "while"    {
    return token(KEYWORD);
  }
  
  [:jletter:][:jletterdigit:]* {
    return token(IDENTIFIER);
  }
}

.|\r|\n {
  return null;
}

<<EOF>> {
  return null;
}
