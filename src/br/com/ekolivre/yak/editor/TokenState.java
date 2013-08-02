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
};
