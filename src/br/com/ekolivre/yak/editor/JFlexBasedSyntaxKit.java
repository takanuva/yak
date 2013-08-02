package br.com.ekolivre.yak.editor;

import java.io.*;
import java.util.*;
import static java.lang.System.*;

public abstract class JFlexBasedSyntaxKit extends NestableSyntaxKit {
  //
  private class JFlexBasedTokenState extends TokenState {
    private int yystate;
    
    private JFlexBasedTokenState(int state) {
      yystate = state;
    };
    
    @Override
    public int state() {
      return yystate;
    };
  };
  
  //
  @Override
  public Token getToken(TokenState state) {
    
    yyreset(getCurrentReader());
    
    if(state instanceof JFlexBasedTokenState)
      yybegin(((JFlexBasedTokenState)state).yystate);
    else yybegin(0);
    
    try {
      return yylex();
    } catch(IOException e) {
      //
    };
    
    return null;
  };
  
  /**
   *
   */
  protected Token token(AbstractTokenType type) {
    return token(type, null);
  };
  
  /**
   *
   */
  protected Token token(AbstractTokenType type, Integer match) {
    return new Token(
      getCurrentOffset() + yypos(),
      yylength(),
      type,
      new JFlexBasedTokenState(
        yystate()
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
