/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU LGPL.                                                           *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
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
import java.util.regex.*;
import br.com.ekolivre.yak.editor.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static br.com.ekolivre.yak.editor.TokenType.*;

/**
 *
 */
public class CSyntaxKit extends NestableSyntaxKit {
  /* */
  public static final int KRC = 0x01;
  public static final int C89 = 0x02;
  public static final int C99 = 0x04;
  public static final int C11 = 0x08;
  public static final int MSC = 0x10 | C11;
  public static final int GNU = 0x20 | C11;
  public static final int INO = 0x40 | GNU;
  
  /* */
  private static final int STATE_TOP =  0;
  private static final int STATE_STR =  1;
  private static final int STATE_CHR =  2;
  private static final int STATE_CPP =  3;
  private static final int STATE_CPP2 = 4;
  private static final int STATE_CPP3 = 5;
  private static final int STATE_CPP_STR = 6;
  
  //
  private int i;
  
  //
  private static class CTokenState
  extends NestableSyntaxKit.NestableTokenState {
    //
    int state;
    
    //
    boolean angle_bracket;
    
    //
    CTokenState() {
      this(STATE_TOP);
    };
    
    //
    CTokenState(int state) {
      this(state, true);
    };
    
    //
    CTokenState(int state, boolean bracket) {
      this.state = state;
      angle_bracket = bracket;
    };
    
    //
    @Override
    public int state() {
      return state;
    };
  };
  
  //
  private static final Pattern PATTERN_CPP_KRC = Pattern.compile(
    "(?<=[\r\n]|^)#"
  );
  
  //
  private static final Pattern PATTERN_CPP_RAW = Pattern.compile(
    "[^\"<\r\n/?]+"
  );
  
  //
  private static final Pattern PATTERN_CPP_KEY = Pattern.compile(
    "(?:" +
      "define|" +
      "pragma|" +
      "import|" +
      "include(?:_next)?|" +
      "if(?:n?def)?|" +
      "else|" +
      "endif|" +
      "undef|" +
      "(?:un)?assert" +
    ")\\b"
  );
  
  //
  private static final Pattern PATTERN_CPP_WRD = Pattern.compile(
    "\\w+"
  );
  
  //
  private static final Pattern PATTERN_DIGRAPH = Pattern.compile(
    "<[:%]|[:%]>|%:"
  );
  
  //
  private static final Pattern PATTERN_TRIGRAPH = Pattern.compile(
    "\\?\\?[=/'()!<>-]"
  );
  
  //
  private static final Pattern PATTERN_TRIFAIL = Pattern.compile(
    "\\?\\?(?m:.|$)"
  );
  
  //
  private static final Pattern PATTERN_ESCAPE = Pattern.compile(
    "(?:\\\\|\\?\\?/)(?:\r\n?|\n)"
  );
  
  //
  private static final Pattern PATTERN_DIGITS = Pattern.compile(
    "\\b\\d(?:[Ee][+-]?\\d|[\\w\\._$])*"
  );
  
  //
  private static final Pattern PATTERN_STRING_INCOMPLETE = Pattern.compile(
    "\"(?:(?:\\\\|\\?\\?/)(?:\r\n?|\n|.)|[^\r\n\"])*+(?>[\r\n]|\\Z)"
  );
  
  //
  private static final Pattern PATTERN_CHARACTER_INCOMPLETE = Pattern.compile(
    "'(?:(?:\\\\|\\?\\?/)(?:\r\n?|\n|.)|[^\r\n'])*+(?>[\r\n]|\\Z)"
  );
  
  //
  private static final Pattern PATTERN_STRING_ESCAPE = Pattern.compile(
    "(?:\\\\|\\?\\?/)."
  );
  
  //
  private static final Pattern PATTERN_STRING_FORMAT = Pattern.compile(
    "%[^\"]"
  );
  
  //
  private static final Pattern PATTERN_STRING_RAW = Pattern.compile(
    "[^\"\\\\%]+"
  );
  
  //
  private static final Pattern PATTERN_CHARACTER_RAW = Pattern.compile(
    "[^'\\\\%]+"
  );
  
  
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
    }});
  
  //
  @Override
  protected boolean isWordCharacter(char c) {
    if(c == '$')
      return true;
    return super.isWordCharacter(c);
  };
  
  //
  @Override
  protected final Token getToken(TokenState state) {
    if(state instanceof CTokenState) {
      CTokenState state2 = (CTokenState)state;
      switch(state2.state) {
        case STATE_TOP: return getTokenAtTop(state2);
        case STATE_STR: return getTokenAtStr(state2);
        case STATE_CHR: return getTokenAtChr(state2);
        case STATE_CPP: return getTokenAtCpp(state2);
        case STATE_CPP2: return getTokenAtCpp2(state2);
        case STATE_CPP3: return getTokenAtCpp3(state2);
        case STATE_CPP_STR: return getTokenAtCppStr(state2);
      };
    };
    
    return getTokenAtTop(new CTokenState());
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
  private Token getTokenAtTop(CTokenState same) {
    //
    Token t = checkComment(getDialect() > C89 ? "//" : null, "/*", "*/", same);
    if(t != null)
      return t;
    
    //
    if(isDialect(KRC)) {
      i = checkRegex(PATTERN_CPP_KRC);
    } else {
      if(isAtVisualSOL())
        i = checkString("#");
      else i = 0;
    };
    
    //
    if(i > 0)
      return token(i, PREPROCESSOR, new CTokenState(STATE_CPP));
    
    //
    if(getDialect() > C89) {
      i = checkKeywords("_Pragma");
      if(i > 0)
        return token(i, PREPROCESSOR_KEYWORD, same);
    };
    
    //
    i = checkKeywords("auto",
                      "break",
                      "case",
                      "char",
                      "const",
                      "continue",
                      "default",
                      "do",
                      "double",
                      "else",
                      "enum",
                      "extern",
                      "float",
                      "for",
                      "goto",
                      "if",
                      "int",
                      "long",
                      "register",
                      "return",
                      "short",
                      "signed",
                      "sizeof",
                      "static",
                      "struct",
                      "switch",
                      "typedef",
                      "union",
                      "unsigned",
                      "void",
                      "volatile",
                      "while");
    if(i > 0)
      return token(i, KEYWORD, same);
    
    //
    i = checkRegex(PATTERN_DIGRAPH);
    if(i > 0)
      return token(i, DITRIGRAPH, new CTokenState());
    
    //
    i = checkRegex(PATTERN_TRIGRAPH);
    if(i > 0)
      return token(i, DITRIGRAPH, new CTokenState());
    
    i = checkRegex(PATTERN_TRIFAIL);
    if(i > 0)
      return token(i, DITRIGRAPH.misspell(), same);
    
    //
    i = checkOneOf("<>|?:!%^&*=,.-+;~/");
    if(i > 0)
      return token(i, PUNCTUATION, new CTokenState());
    
    if(checkString("(") > 0)
      return token(1, PUNCTUATION, new CTokenState(), +'(');
    
    if(checkString(")") > 0)
      return token(1, PUNCTUATION, new CTokenState(), -'(');
    
    if(checkString("[") > 0)
      return token(1, PUNCTUATION, new CTokenState(), +'[');
    
    if(checkString("]") > 0)
      return token(1, PUNCTUATION, new CTokenState(), -'[');
    
    if(checkString("{") > 0)
      return token(1, PUNCTUATION, new CTokenState(), +'{');
    
    if(checkString("}") > 0)
      return token(1, PUNCTUATION, new CTokenState(), -'}');
    
    //
    i = checkRegex(PATTERN_DIGITS);
    if(i > 0)
      return token(i, NUMBER, new CTokenState() {
        @Override
        public boolean needLookbehind() {
          return true;
        };
      });
    
    //
    i = checkRegex(PATTERN_STRING_INCOMPLETE);
    if(i > 0)
      return token(i, STRING_INCOMPLETE, new CTokenState());
    
    //
    if(checkString("\"") > 0)
      return token(1, STRING, new CTokenState(STATE_STR));
    
    //
    i = checkRegex(PATTERN_CHARACTER_INCOMPLETE);
    if(i > 0)
      return token(i, CHARACTER_INCOMPLETE, new CTokenState());
    
    //
    if(checkString("'") > 0)
      return token(1, CHARACTER, new CTokenState(STATE_CHR));
    
    return null;
  };
  
  //
  private Token getTokenAtStr(CTokenState same) {
    if(checkString("\"") > 0)
      return token(1, STRING, new CTokenState());
    
    i = checkRegex(PATTERN_STRING_ESCAPE);
    if(i > 0)
      return token(i, STRING_ESCAPE, same);
    
    i = checkRegex(PATTERN_STRING_FORMAT);
    if(i > 0)
      return token(i, STRING_FORMAT, same);
    
    i = checkRegex(PATTERN_STRING_RAW);
    if(i > 0)
      return token(i, STRING, same);
    
    return token(1, STRING, same);
  };
  
  //
  private Token getTokenAtChr(CTokenState same) {
    if(checkString("'") > 0)
      return token(1, CHARACTER, new CTokenState());
    
    i = checkRegex(PATTERN_STRING_ESCAPE);
    if(i > 0)
      return token(i, CHARACTER_ESCAPE, same);
    
    i = checkRegex(PATTERN_CHARACTER_RAW);
    if(i > 0)
      return token(i, CHARACTER, same);
    
    return token(1, CHARACTER, same);
  };
  
  //
  private Token getTokenAtCpp(CTokenState same) {
    
    if(isNewline())
      return token(1, DEFAULT, new CTokenState());
    
    //
    if(isWhitespace())
      return token(1, PREPROCESSOR, same);
    
    //
    Token t = checkComment(getDialect() > C89 ? "//" : null, "/*", "*/", same);
    if(t != null)
      return t;
    
    //
    i = checkRegex(PATTERN_CPP_KEY);
    if(i > 0)
      return token(i, PREPROCESSOR_KEYWORD, new CTokenState(STATE_CPP2));
    
    i = checkRegex(PATTERN_CPP_WRD);
    if(i > 0)
      return token(i, PREPROCESSOR.misspell(), new CTokenState(STATE_CPP3));
    
    //
    return getTokenAtCpp3(new CTokenState(STATE_CPP3));
  };
  
  //
  private Token getTokenAtCpp2(CTokenState same) {
    if(isNewline())
      return token(1, DEFAULT, new CTokenState());
    
    //
    if(isWhitespace())
      return token(1, PREPROCESSOR, same);
    
    //
    Token t = checkComment(getDialect() > C89 ? "//" : null, "/*", "*/", same);
    if(t != null)
      return t;
    
    i = checkRegex(PATTERN_ESCAPE);
    if(i > 0)
      return token(i, DITRIGRAPH, same);
    
    i = checkRegex(PATTERN_DIGRAPH);
    if(i > 0)
      return token(i, DITRIGRAPH, new CTokenState(STATE_CPP3));
    
    i = checkRegex(PATTERN_TRIGRAPH);
    if(i > 0)
      return token(i, DITRIGRAPH, new CTokenState(STATE_CPP3));
    
    i = checkRegex(PATTERN_TRIFAIL);
    if(i > 0)
      return token(i, DITRIGRAPH.misspell(), new CTokenState(STATE_CPP3));
    
    //
    if(checkString("\"") > 0)
      return token(1, PREPROCESSOR_STRING, new CTokenState(STATE_CPP_STR,
                                                           false));
    
    //
    if(checkString("<") > 0)
      return token(1, PREPROCESSOR_STRING, new CTokenState(STATE_CPP_STR,
                                                           true));
    
    return getTokenAtCpp3(new CTokenState(STATE_CPP3));
  };
  
  //
  private Token getTokenAtCpp3(CTokenState same) {
    
    if(isNewline())
      return token(1, DEFAULT, new CTokenState());
    
    //
    Token t = checkComment(getDialect() > C89 ? "//" : null, "/*", "*/", same);
    if(t != null)
      return t;
    
    i = checkRegex(PATTERN_ESCAPE);
    if(i > 0)
      return token(i, DITRIGRAPH, same);
    
    i = checkRegex(PATTERN_DIGRAPH);
    if(i > 0)
      return token(i, DITRIGRAPH, same);
    
    i = checkRegex(PATTERN_TRIGRAPH);
    if(i > 0)
      return token(i, DITRIGRAPH, same);
    
    i = checkRegex(PATTERN_TRIFAIL);
    if(i > 0)
      return token(i, DITRIGRAPH.misspell(), same);
    
    return token(1, PREPROCESSOR, same);
  };
  
  
  //
  private Token getTokenAtCppStr(CTokenState same) {
    if(same.angle_bracket) {
      if(checkString(">") > 0)
        return token(1, PREPROCESSOR_STRING, new CTokenState(STATE_CPP3));
    } else {
      if(checkString("\"") > 0)
        return token(1, PREPROCESSOR_STRING, new CTokenState(STATE_CPP3));
    };
    
    return token(1, PREPROCESSOR_STRING, same);
  };
};
