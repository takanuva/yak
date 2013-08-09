/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU LGPL.                                                           *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*******************************************************************************/
package br.com.ekolivre.yak.editor;

import java.io.*;
import java.util.*;
import static java.lang.System.*;

public abstract class JFlexBasedSyntaxKit<T> extends NestableSyntaxKit {
  //
  protected class JFlexBasedTokenState
  extends NestableSyntaxKit.NestableTokenState {
    private int yystate;
    private T user_data;
    
    private JFlexBasedTokenState(int state, T t) {
      yystate = state;
      user_data = t;
    };
    
    public T userData() {
      return user_data;
    };
    
    @Override
    public int state() {
      return yystate;
    };
  };
  
  //
  protected JFlexBasedTokenState yystate;
  
  //
  @Override
  @SuppressWarnings("unchecked")
  public final Token getToken(TokenState state) {
    
    yyreset(getCurrentReader());
    
    if(state instanceof JFlexBasedSyntaxKit<?>.JFlexBasedTokenState) {
      yystate = (JFlexBasedTokenState)state;
      yybegin(yystate.yystate);
    } else {
      yystate = new JFlexBasedTokenState(0, null);
      yybegin(0);
    };
    
    try {
      return yylex();
    } catch(IOException e) {
      // Never happens
    };
    
    return null;
  };
  
  /**
   *
   */
  protected final Token token(AbstractTokenType type) {
    return token(type, null, null);
  };
  
  /**
   *
   */
  protected final Token token(AbstractTokenType type, Integer match) {
    return token(type, match, null);
  };
  
  /**
   *
   */
  protected final Token token(AbstractTokenType type, T t) {
    return token(type, null, t);
  };
  
  /**
   *
   */
  protected final Token token(AbstractTokenType type, Integer match, T t) {
    return new Token(
      getCurrentOffset() + yypos(),
      yylength(),
      type,
      new JFlexBasedTokenState(
        yystate(),
        t
      ),
      match
    );
  };
  
  //
  public abstract void yyreset(Reader reader);
  public abstract Token yylex() throws IOException;
  public abstract int yylength();
  public abstract int yystate();
  public abstract int yypos();
  public abstract void yybegin(int state);
  public abstract String yytext();
};
