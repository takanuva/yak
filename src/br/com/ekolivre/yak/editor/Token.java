/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU GPL.                                                            *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*                                                                              *
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
package br.com.ekolivre.yak.editor;

import static java.lang.System.*;

public class Token
implements Comparable<Token> {
  /**
   *
   */
  private final AbstractTokenType type;
  
  /**
   *
   */
  private final TokenState state;
  
  /**
   *
   */
  private final int offset;
  
  /**
   *
   */
  private int length;
  
  /**
   *
   */
  Integer match;
  
  /**
   *
   */
  public Token(int offset, int length, AbstractTokenType type, TokenState state,
               Integer match) {
    //
    assert state != null;
    
    //
    this.offset = offset;
    this.length = length;
    this.state = state;
    this.type = type;
    this.match = match;
  };
  
  /**
   *
   */
  public int start() {
    return offset;
  };
  
  /**
   *
   */
  public int length() {
    return length;
  };
  
  /**
   *
   */
  public int end() {
    return start() + length();
  };
  
  /**
   *
   */
  public TokenState getState() {
    return state;
  };
  
  /**
   *
   */
  public AbstractTokenType getType() {
    return type;
  };
  
  /**
   *
   */
  public void increase(int length) {
    this.length += length;
  };
  
  /**
   *
   */
  public Integer match() {
    return match;
  };
  
  /**
   *
   */
  @Override
  public int compareTo(Token tkn) {
    //
    if(match != null)
      return -1;
    
    //
    int aux = getType().compareTo(tkn.getType());
    if(aux == 0) {
      //
      if(getState() == null)
        if(tkn.getState() == null)
          aux = 0;
        else return -1;
      else if(tkn.getState() == null)
        return 1;
      else aux = getState().compareTo(tkn.getState());
      
      //
      if(aux == 0) {
        //
        if(end() < tkn.start())
          return -1;
        if(start() > tkn.end())
          return 1;
        return 0;
      };
    };
    
    //
    return aux;
  };
  
  /**
   *
   */
  @Override
  public String toString() {
    return String.format("%s[%d:%d|%s]", getType(), start(), end(), getState());
  };
};
