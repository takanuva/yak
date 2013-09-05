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
import java.util.regex.*;
import br.com.ekolivre.yak.editor.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static br.com.ekolivre.yak.editor.TokenType.*;

/**
 *
 */
public class MarkdownSyntaxKit extends NestableSyntaxKit {
  //
  //public static final int DARING = 0x01;
  public static final int GITHUB = 0x02;
  //public static final int STACKO = 0x04;
  
  //
  @SuppressWarnings("unchecked")
  private static final Map<Integer, String> dialects = 
    unmodifiableMap(new TreeMap() {{
      //put(DARING, "Daring Fireball");
      put(GITHUB, "Github Flavored");
      //put(STACKO, "Stack Overflow Flavored");
    }});
  
  //
  @SuppressWarnings("unchecked")
  private static final Map<String, Integer> extensions_map =
    unmodifiableMap(new TreeMap() {{
      put("md", GITHUB);
    }});
  
  //
  private static final Pattern PATTERN_EMPHASIS = Pattern.compile(
    "(?<=\\s|\\A)(?<!\\\\)(([_*])\\2?\\2?)[^\\s_]((?<!\r\n)\r\n|(?<!\r)\r|(?<" +
    "!\n)\n|.)*?(?<=[^\\s])\\1"
  );
  
  //
  @Override
  protected final Token getToken(TokenState state) {
    
    int i;
    
    String match[] = new String[2];
    i = checkRegex(PATTERN_EMPHASIS, match);
    if(i > 0)
      switch(match[1].length()) {
        case 1: return token(i, DEFAULT_ITALIC, state);
        case 2: return token(i, DEFAULT_BOLD, state);
        case 3: return token(i, DEFAULT_BOLD_ITALIC, state);
        default: assert false;
      };
    
    return null;
  };
  
  @Override
  public Map<Integer, String> getDialectList() {
    return dialects;
  };
  
  @Override
  public int getDefaultDialect() {
    return GITHUB;
  };
  
  @Override
  public Map<String, Integer> getFileExtensions() {
    return extensions_map;
  };
  
  @Override
  public String getContentType() {
    return "text/x-markdown";
  };
  
  @Override
  public String getKitName() {
    return "Markdown";
  };
};
