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
package br.com.ekolivre.yak.editor;

import java.util.*;
import static java.lang.System.*;

public class MatchTreeMap extends TreeMap<Integer, Token> {
  
  Map<Integer, TreeMap<Integer, Token>> map = new HashMap<>();
  
  public MatchTreeMap() {
    super();
  };
  
  public MatchTreeMap(NavigableMap<Integer, Token> copy) {
    super(copy);
  };
  
  @Override
  public Token put(Integer i, Token t) {
    assert t.match() != null;
    assert t.match() != 0;
    
    int key = Math.abs(t.match());
    
    if(!map.containsKey(key)) {
      
    };
    
    
    return t;
  };
  
};
