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
public class APLSyntaxKit extends NestableSyntaxKit {
  /* */
  public static final int AAA = 0x01;
  
  //
  @SuppressWarnings("unchecked")
  private static final Map<Integer, String> dialects = 
    unmodifiableMap(new HashMap() {{
      put(AAA, "ASCII Annotation");
    }});
  
  //
  @SuppressWarnings("unchecked")
  private static final Map<String, Integer> extensions_map =
    unmodifiableMap(new HashMap() {{
      put("aaa", AAA);
    }});
  
  //
  /*@Override
  protected boolean isWordCharacter(char c) {
    if(c == '$')
      return true;
    return super.isWordCharacter(c);
  };*/
  
  //
  @Override
  protected final Token getToken(TokenState state) {
    return null;
  };
  
  @Override
  public Map<Integer, String> getDialectList() {
    return null;
  };
  
  @Override
  public int getDefaultDialect() {
    return 0;
  };
  
  @Override
  public Map<String, Integer> getFileExtensions() {
    return extensions_map;
  };
  
  @Override
  public String getContentType() {
    return "text/x-apl";
  };
  
  @Override
  public String getKitName() {
    return "APL";
  };
};
