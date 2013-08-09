/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU LGPL.                                                           *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
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
