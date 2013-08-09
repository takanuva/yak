/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU LGPL.                                                           *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
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
public class GuessMeSyntaxKit extends NestableSyntaxKit {
  //
  private static class GuessMeTokenState extends TokenState {
    //
    int state = 0;
    
    GuessMeTokenState() {
      // TODO
    };
    
    //
    @Override
    public int state() {
      return state;
    };
  };
  
  //
  @Override
  protected boolean isWordCharacter(char c) {
    // TODO :3
    
    return super.isWordCharacter(c);
  };
  
  //
  @Override
  protected final Token getToken(TokenState state) {
    return null;
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
    return null;
  };
  
  //
  @Override
  public String getContentType() {
    return "text/x-programming";
  };
  
  // 
  @Override
  public String getKitName() {
    return "\0Guess me! :)";
  };
};
