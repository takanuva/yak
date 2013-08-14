/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU LGPL.                                                           *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*******************************************************************************/
package br.com.ekolivre.yak.editor.kits;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import br.com.ekolivre.yak.editor.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static br.com.ekolivre.yak.editor.TokenType.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class GuessMeSyntaxKit extends NestableSyntaxKit {
  //
  private static class GuessMeTokenState extends TokenState {
    //
    int state = 0;
    
    //
    boolean reset[] = null;
    
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
  private static final String CLASSIFIER = "/classifier.bys";
  
  //
  private static HashMap<String, HashMap<String, Integer>> classifier;
  
  static {
    
    try {
      InputStream is = GuessMeSyntaxKit.class.getResourceAsStream(CLASSIFIER);
      ObjectInputStream ois = new ObjectInputStream(is);
      
      classifier = (HashMap<String, HashMap<String, Integer>>)ois.readObject();
    } catch(Throwable t) {
      //
    };
    
  };
  
  //
  public GuessMeSyntaxKit() {
    
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
    if(state instanceof GuessMeTokenState) {
      GuessMeTokenState state2 = (GuessMeTokenState)state;
      
    };
    
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
    return "Guess me! :)";
  };
};
