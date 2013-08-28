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
package br.com.ekolivre.yak.editor;

public abstract class TokenState
implements Comparable<TokenState> {
  
  @Override
  public int compareTo(TokenState tkn) {
    int aux = getClass().toString().compareTo(tkn.getClass().toString());
    if(aux == 0) {
      int x = state();
      int y = tkn.state();
      
      if(x > y)
        return 1;
      if(y > x)
        return -1;
    };
    return aux;
  };
  
  /**
   *
   */
  public abstract int state();
  
  /**
   *
   */
  public boolean needLookbehind() {
    return state() != 0;
  };
  
  /**
   *
   */
  public boolean toplevel() {
    return state() == 0;
  };
  
  /**
   *
   */
  @Override
  public String toString() {
    return "(" + getClass().getSimpleName() + ": " + state() + ")";
  };
};
