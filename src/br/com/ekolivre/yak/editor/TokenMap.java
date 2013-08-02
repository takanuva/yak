package br.com.ekolivre.yak.editor;

import java.util.*;
import java.lang.ref.*;

/**
 *
 */
public class TokenMap {  
  /**
   *
   */
  public class TokenRange {
    /**
     *
     */
    public class TokenIterator implements Iterator<Map.Entry<Integer, Token>> {
      /**
       *
       */
      public boolean hasNext() {
        return false;
      };
      
      /**
       *
       */
      public Map.Entry<Integer, Token> next() throws NoSuchElementException {
        return null;
      };
      
      /**
       *
       */
      public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
      };
    };
    
  };
  
};
