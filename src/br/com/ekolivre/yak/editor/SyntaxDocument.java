/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU LGPL.                                                           *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*******************************************************************************/
package br.com.ekolivre.yak.editor;

import java.util.*;
import javax.swing.*;
import java.awt.Point;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.event.*;
import static java.lang.System.*;
import static java.util.Collections.*;

/**
 *
 */
public class SyntaxDocument extends PlainDocument {
  /**
   *
   */
  public static class DummyTokenState extends TokenState {
    //
    @Override
    public int state() {
      return 0;
    };
  };
  
  /**
   *
   */
  private DefaultSyntaxKit kit;
  
  /**
   *
   */
  private volatile TreeMap<Integer, Token> tokens;
  
  /**
   *
   */
  private volatile MatchTreeMap match_map;
  
  /**
   *
   */
  public SyntaxDocument(DefaultSyntaxKit kit) {
    this.kit = kit;
    this.tokens = new TreeMap<Integer, Token>();
    this.match_map = new MatchTreeMap();
    putProperty(PlainDocument.tabSizeAttribute, 2);
  };
  
  //
  public DefaultSyntaxKit getKit() {
    return kit;
  };
  
  //
  @Override
  protected void fireChangedUpdate(DocumentEvent e) {
    invalidateFrom(e.getOffset());
    super.fireChangedUpdate(e);
  };
  
  //
  @Override
  protected void fireInsertUpdate(DocumentEvent e) {
    invalidateFrom(e.getOffset());
    super.fireInsertUpdate(e);
  };
  
  //
  @Override
  protected void fireRemoveUpdate(DocumentEvent e) {
    invalidateFrom(e.getOffset());
    super.fireRemoveUpdate(e);
    
    //
    if(e.getOffset() == 0) {
      // TODO: Handle bug here
    };
  };
  
  public int getNumberOfLines() {
    return getLineForPos(getLength());
  };
  
  public int getLineForPos(Token tkn) {
    return getLineForPos(tkn.start());
  };
  
  public int getStartOfLine(int pos) {
    Element e = getDefaultRootElement();
    return e.getElement(e.getElementIndex(pos)).getStartOffset();
  };
  
  public int getEndOfLine(int pos) {
    Element e = getDefaultRootElement();
    return e.getElement(e.getElementIndex(pos)).getEndOffset() - 1;
  };
  
  public int getLineForPos(int pos) {
		return getDefaultRootElement().getElementIndex(pos) + 1;
  };
  
  public Point getPointForPos(int pos) {
    
    Element root = getDefaultRootElement();
    
    int y = root.getElementIndex(pos);
    int x = pos - root.getElement(y).getStartOffset();
    
    return new Point(x + 1, y + 1);
  };
  
  private void invalidateFrom(int pos) {
    // Remove all tokens after given position
    tokens.tailMap(pos).clear();
    
    // Check if the position belongs in between the last known token
    while(tokens.size() > 0 && tokens.get(tokens.lastKey()).end() >= pos)
      tokens.pollLastEntry();
    
    // Always go back a few tokens (to be sure =P)
    if(tokens.size() > 0) tokens.pollLastEntry();
    if(tokens.size() > 0) tokens.pollLastEntry();
    if(tokens.size() > 0) tokens.pollLastEntry();
    
    // Check if our tokens need lookbehind
    while(tokens.size() > 0 && tokens.get(tokens.lastKey()).getState()
                                                           .needLookbehind())
      tokens.pollLastEntry();
    
    // Finally, update our match map
    /// TODO: remake this
    if(tokens.size() > 0)
      //
      match_map = new MatchTreeMap(
        match_map.headMap(tokens.lastKey(), true)
      );
    else match_map.clear();
  };
  
  private synchronized void assureItsParsed(int end) {
    if(tokens.higherKey(end) == null) {
      parseUntil(end);
    };
  };
  
  public synchronized Iterable<Token> getTokens() {
    return getTokens(0, getLength());
  };
  
  public synchronized Iterable<Token> getTokens(int from, int to) {
    assureItsParsed(to);
    
    Integer f = tokens.floorKey(from);
    if(f != null)
      if(tokens.get(f).end() < from)
        f = null;
    
    Integer t = tokens.floorKey(to);
    if(t != null)
      if(t < from)
        t = null;
    
    try {
      return tokens.subMap(
        f == null ? from : f, true,
        t == null ? to :   t, true
      ).values();
    } catch(IllegalArgumentException e) {
      // Actually, should never fall here
      /*err.printf("FAIL (%s)!!! from = %d, to = %d, f = %s, t = %s%n",
                 e, from, to, f, t);*/
    };
    return tokens.values();
  };
  
  public synchronized Collection<Token> getMatchMap() {
    return match_map.values();
  };
  
  /**
   * 
   */
  private synchronized void parseUntil(int end) {
    
    end += 2048;
    if(end > getLength())
      end = getLength();
    
    //
    int start;
    TokenState state;
    
    try {
      //
      start = tokens.lastKey();
      state = tokens.get(start).getState();
      
      // This should fix everything, bro! =D
      start = tokens.get(start).end();
      
    } catch(Throwable t) {
      tokens.clear();
      match_map.clear();
      start = 0;
      state = new DummyTokenState();
    };
    
    if(start >= end)
      // Will this ever happen? :~
      return;
    
    //
    Segment seg = new Segment();
    
    //
    try {
      
      getText(0, getLength(), seg);
      //out.printf("  parsing from %d to %d...%n", start, end);
      
      List<Token> parsed = kit.parse(seg, start, getLength(), end, state);
      
      Token last = tokens.get(start);
      if(parsed != null)
        for(Token tkn: parsed) {
          
          // Optimize tokens
          if(last != null) {
            if(last.compareTo(tkn) == 0) {
              // This should be the only acceptable behaviour
              assert last.end() == tkn.start();
              
              //
              last.increase(tkn.length());
              continue;
            };
          };
          
          // Add it to our map
          tokens.put(tkn.start(), tkn);
          
          // And to our match tree, if needed
          if(tkn.match() != null)
            match_map.put(tkn.start(), tkn);
          
          last = tkn;
        };
      
    } catch(BadLocationException e) {
      return;
    }/* finally {
      //out.printf("%n%n%n%n");
      out.printf("  Document's tokens:%n ");
      for(Token t: tokens.values())
        out.printf(" %s", t);
      out.printf("%n%n%n%n");
    }*/;
  };
  
  public void getDisplayText(int pos, int len, Segment seg)
  throws BadLocationException {
    kit.getDisplayText(pos, len, seg);
  };
  
};
