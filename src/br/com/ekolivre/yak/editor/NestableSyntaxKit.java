/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU LGPL.                                                           *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*******************************************************************************/
package br.com.ekolivre.yak.editor;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.geom.*;
import java.util.regex.*;
import java.awt.Graphics;
import javax.swing.text.*;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import static java.lang.System.*;
import static java.util.Collections.*;
import static br.com.ekolivre.yak.editor.TokenType.*;

/**
 *
 */
public abstract class NestableSyntaxKit extends DefaultSyntaxKit {
  /* package */
  final AbstractTokenType SOLTokenType = new AbstractTokenType() {
    //
    @Override
    public int write(Segment s, int x, int y, Graphics g, SyntaxView e,
                     Dimension size, boolean invert) {
      // Just to be sure
      assert(s.length() > 0);
      
      //
      return getSkin().drawStartOfLine(s, x, y, g, e, size, invert);
    };
    
    //
    @Override
    public int compareTo(AbstractTokenType other) {
      return toString().compareTo(other.toString());
    };
    
    //
    @Override
    public boolean isComment() {
      return false;
    };
  };
  
  /* package */
  final AbstractTokenType EOLTokenType = new AbstractTokenType() {
    //
    @Override
    public int write(Segment s, int x, int y, Graphics g, SyntaxView e,
                     Dimension size, boolean invert) {
      // Just to be sure
      assert(s.length() > 0);
      
      //
      FontMetrics m = g.getFontMetrics();
      int a = m.getAscent();
      int h = a + m.getDescent();
      
      //
      int w = Utilities.getTabbedTextWidth(s, m, x, e, 0);
      
      //
      int rx = x - 1;
      int ry = y - a;
      int rw = w + 2;
      int rh = h;
      
      //
      
      Graphics2D g2d = (Graphics2D)g;
      GeneralPath path = new GeneralPath();
      path.moveTo(rx, ry + rh - 1);
      for(int i = 0; i < rw; i += 4) {
        path.lineTo(rx + i, ry + rh - 3);
        path.lineTo(rx + i + 2, ry + rh -1);
      };
      g2d.setColor(Color.RED);
      g2d.draw(path);
      
      //
      return x + w;
    };
    
    //
    @Override
    public int compareTo(AbstractTokenType other) {
      return toString().compareTo(other.toString());
    };
    
    //
    @Override
    public boolean isComment() {
      return true;
    };
  };
  
  /* package */
  final AbstractTokenType WhitespaceTokenType = new AbstractTokenType() {
    //
    @Override
    public int write(Segment s, int x, int y, Graphics g, SyntaxView e,
                     Dimension size, boolean invert) {
      // Just to be sure
      assert(s.length() > 0);
      
      //
      return getSkin().drawWhitespaces(s, x, y, g, e, size, invert);
    };
    
    //
    @Override
    public int compareTo(AbstractTokenType other) {
      return toString().compareTo(other.toString());
    };
    
    //
    @Override
    public boolean isComment() {
      return true;
    };
  };
  
  //
  private class CommentTokenState extends TokenState {
    private TokenState next;
    private String close;
    private TokenType type;
    private CommentTokenState(TokenState next, String close, TokenType type) {
      //
      assert next != null;
      
      //
      this.next = next;
      this.close = close;
      this.type = type;
    };
    
    private boolean mayClose() {
      if(close == null) {
        if(isNewline())
          return true;
        return false;
      };
      
      for(int i = 0; i < close.length(); i++) {
        if(off + i >= len)
          return false;
        if(close.charAt(i) != seq.charAt(off + i))
          return false;
      };
      
      return true;
    };
    
    private Token getToken() {
      
      if(mayClose())
        return token(close == null ? 0 : close.length(), type, next);
      
      return token(1, type, this);
    };
    
    //
    @Override
    public int state() {
      return 0;
    };
  };
  
  //
  private class DocTokenState extends CommentTokenState {
    private DocTokenState(TokenState next, String close) {
      super(next, close, DOC_COMMENT);
    };
  };
  
  //
  private class SpecTokenState extends CommentTokenState {
    private SpecTokenState(TokenState next, String close) {
      super(next, close, SPEC_COMMENT);
    };
  };
  
  //
  protected static abstract class NestableTokenState extends TokenState {
    protected NestableTokenState() {
      
    };
  };
  
  //
  private static final Pattern PATTERN_SOL = Pattern.compile(
    "(?<=\\A|[\r\n])[ \t]+"
  );
  
  private static final Pattern PATTERN_EOL = Pattern.compile(
    "[ \t]+(?=\\Z|[\r\n])"
  );
  
  private static final Pattern PATTERN_WHITESPACE = Pattern.compile(
    "[ \t]+"
  );
  
  private static final Pattern PATTERN_SHEBANG = Pattern.compile(
    "(?ms:#!.*?(?:$|\\z))"
  );
  
  //
  private CharSequence seq;
  private int off;
  private int len;
  private TokenState sta;
  
  /**
   *
   */
  protected boolean isWordCharacter(int i) {
    if(i < 0)
      return false;
    if(i >= len)
      return false;
    return isWordCharacter(seq.charAt(i));
  };
  
  /**
   *
   */
  protected boolean isWordCharacter(char c) {
    // Character.isJavaIdentifierPart
    if(Character.isLetterOrDigit(c))
      return true;
    if(c == '_')
      return true;
    return false;
  };
  
  /**
   *
   */
  private boolean isNewline(int pos) {
    if(pos < 0)
      return true;
    if(pos >= len)
      return true;
    if(seq.charAt(pos) == '\n')
      return true;
    if(seq.charAt(pos) == '\r')
      return true;
    
    return false;
  };
  
  /**
   *
   */
  protected boolean isNewline() {
    return isNewline(off);
  };
  
  /**
   *
   */
  private boolean isWhitespace(int pos) {
    if(pos < 0)
      return true;
    if(pos >= len)
      return true;
    if(seq.charAt(pos) == ' ')
      return true;
    if(seq.charAt(pos) == '\t')
      return true;
    
    return false;
  };
  
  /**
   *
   */
  protected boolean isWhitespace() {
    return isWhitespace(off);
  };
  
  /**
   *
   */
  protected boolean isAtVisualSOL() {
    //
    int pos = off - 1;
    
    //
    while(pos >= 0 && isWhitespace(pos))
      pos--;
    
    //
    return isNewline(pos);
  };
  
  /**
   *
   */
  protected abstract Token getToken(TokenState state);
  
  /*protected int checkComment(String start) {
    
    for(int i = 0; i < start.length(); i++) {
      if(off + i >= len)
        return 0;
      if(start.charAt(i) != seq.charAt(off + i))
        return 0;
    };
    
    for(int i = off + start.length(); i <= len; i++) {
      if(isNewline(i))
        return i - off;
    };
    
    // Should never come here, for the matter
    return len - off;
  };
  
  protected int checkComment(String start, String end) {
    for(int i = 0; i < start.length(); i++) {
      if(off + i >= len)
        return 0;
      if(start.charAt(i) != seq.charAt(off + i))
        return 0;
    };
    outer: for(int i = start.length(); i < len - off; i++) {
      for(int j = 0; j < end.length(); j++) {
        if(off + i + j >= len)
          break outer;
        if(end.charAt(j) != seq.charAt(off + i + j))
          continue outer;
      };
      return i + end.length();
    };
    return len - off;
  };
  
  protected int checkComment(Pattern start, Pattern end) {
    
    Matcher matcher = start.matcher(seq).region(off, len)
                                        .useTransparentBounds(true)
                                        .useAnchoringBounds(false);
    if(matcher.lookingAt()) {
      int aux = matcher.end() - matcher.start();
      matcher = end.matcher(seq).useTransparentBounds(true)
                                .useAnchoringBounds(false);
      for(int i = aux; i < len - off; i++) {
        matcher.region(off + i, len);
        if(matcher.lookingAt()) {
          return i + matcher.end() - matcher.start();
        };
      };
      return len - off;
    };
    
    
    return 0;
  };*/
  
  
  
  
  
  protected Token checkComment(String line) {
    return checkComment(line, null, null, sta);
  };
  
  protected Token checkComment(String line, TokenState state) {
    return checkComment(line, null, null, state);
  };
  
  protected Token checkComment(String mul_open, String mul_close) {
    return checkComment(null, mul_open, mul_close, sta);
  };
  
  protected Token checkComment(String mul_open, String mul_close,
                               TokenState state) {
    return checkComment(null, mul_open, mul_close, state);
  };
  
  protected Token checkComment(String line, String mul_open, String mul_close) {
    return checkComment(line, mul_open, mul_close, sta);
  };
  
  protected Token checkComment(String line, String mul_open, String mul_close,
                               TokenState state) {
    // TODO: Check Line Comment
    singleline: do {
      if(line != null) {
        int l = line.length();
        // Do we have the open code here?
        for(int i = 0; i < l; i++) {
          if(off + i >= len)
            break singleline;
          if(line.charAt(i) != seq.charAt(off + i))
            break singleline;
        };
        
        if(off + l < len) {
          char c = seq.charAt(off + l);
          char x = line.charAt(l - 1);
          
          if(c == x || c == '!') {
            return token(l + 1, DOC_COMMENT, new DocTokenState(state, null));
          };
          
          if(c == '@') {
            return token(l + 1, SPEC_COMMENT, new SpecTokenState(state, null));
          };
          
        };
        return token(l, LINE_COMMENT, new CommentTokenState(state, null,
                                                            LINE_COMMENT));
      };
    } while(false);
    
    // Check multiline comment
    multiline: do {
      if(mul_open != null && mul_close != null) {
        int l = mul_open.length();
        // Do we have the open code here?
        for(int i = 0; i < l; i++) {
          if(off + i >= len)
            break multiline;
          if(mul_open.charAt(i) != seq.charAt(off + i))
            break multiline;
        };
        if(off + l < len) {
          char c = seq.charAt(off + l);
          char x = mul_open.charAt(l - 1);
          
          if(c == x || c == '!') {
            return token(l + 1, DOC_COMMENT, new DocTokenState(state,
                                                               mul_close));
          };
          
          if(c == '@') {
            return token(l + 1, SPEC_COMMENT, new SpecTokenState(state,
                                                                 mul_close));
          };
        };
        
        return token(l, BLOCK_COMMENT, new CommentTokenState(state, mul_close,
                                                             BLOCK_COMMENT));
      };
    } while(false);
    
    return null;
  };
  
  
  
  
  
  
  
  
  
  
  
  
  protected int checkRegex(Pattern pattern) {
    return checkRegex(pattern, null);
  };
  
  protected int checkRegex(Pattern pattern, String matches[]) {
    Matcher matcher = pattern.matcher(seq).region(off, len)
                                          .useTransparentBounds(true)
                                          .useAnchoringBounds(false);
    if(matcher.lookingAt()) {
      
      if(matches != null) {
        int end = Math.max(matches.length, matcher.groupCount());
        for(int i = 0; i < end; i++) {
          matches[i] = seq.subSequence(
            matcher.start(i),
            matcher.end(i)
          ).toString();
        };
      };
      
      return matcher.end() - matcher.start();
    };
    
    return 0;
  };
  
  protected int checkString(String word) {
    
    int i;
    for(i = 0; i < word.length(); i++) {
      if(off + i >= len)
        return 0;
      if(word.charAt(i) != seq.charAt(off + i))
        return 0;
    };
    
    return i;
  };
  
  protected int checkKeywords(String... words) {
    
    if(isWordCharacter(off - 1))
      return 0;
    
    looking: for(String word: words) {
      
      int i;
      for(i = 0; i < word.length(); i++) {
        if(off + i >= len)
          continue looking;
        if(word.charAt(i) != seq.charAt(off + i))
          continue looking;
      };
      
      if(isWordCharacter(off + i))
        continue looking;
      
      return i;
    };
    
    return 0;
  };
  
  protected int checkKeywordsI(String... words) {
    
    if(isWordCharacter(off - 1))
      return 0;
    
    looking: for(String word: words) {
      
      int i;
      for(i = 0; i < word.length(); i++) {
        if(off + i >= len)
          continue looking;
        if(Character.toUpperCase(word.charAt(i)) !=
           Character.toUpperCase(seq.charAt(off + i)))
        
          continue looking;
      };
      
      if(isWordCharacter(off + i))
        continue looking;
      
      return i;
    };
    
    return 0;
  };
  
  /**
   *
   */
  protected int checkOneOf(String str) {
    
    for(int i = 0; i < str.length(); i++) {
      if(str.charAt(i) == seq.charAt(off))
        return 1;
    };
    
    
    return 0;
  };
  
  /**
   *
   */
  protected boolean isAtSOF() {
    return off == 0;
  };
  
  
  
  
  
  
  
  
  
  
  
  
  
  /**
   *
   */
  protected final Token token(int size, AbstractTokenType type,
                              TokenState state) {
    return token(size, type, state, null);
  };
  
  /**
   *
   */
  protected final Token token(int size, AbstractTokenType type,
                              TokenState state, Integer match) {
    return new Token(off, size, type, state, match);
  };
  
  /**
   *
   */
  private final Token getToken2(TokenState state) {
    
    if(isAtSOF() && enableShebang()) {
      // Look for a shebang line :)
      int i = checkRegex(PATTERN_SHEBANG);
      if(i > 0)
        return token(i, SHEBANG, state);
    };
    
    if(automaticallyParseSOL()) {
      int i = checkRegex(PATTERN_SOL);
      if(i > 0)
        return token(i, SOLTokenType, state);
    };
      
    if(automaticallyParseEOL()) {
      int i = checkRegex(PATTERN_EOL);
      if(i > 0)
        return token(i, EOLTokenType, state);
    };
    
    int i = checkRegex(PATTERN_WHITESPACE);
    if(i > 0)
      return token(i, WhitespaceTokenType, state);
    
    if(state instanceof CommentTokenState)
      return ((CommentTokenState)state).getToken();
    else if(state instanceof DocTokenState)
      return null;
    else if(state instanceof SpecTokenState)
      return null;
    
    return getToken(state);
  };
  
  /**
   *
   */
  protected boolean automaticallyParseSOL() {
    return true;
  };
  
  /**
   *
   */
  protected boolean automaticallyParseEOL() {
    return true;
  };
  
  /**
   *
   */
  protected boolean enableShebang() {
    return true;
  };
  
  //
  /*protected final synchronized Token nestGetToken(NestableSyntaxKit child,
                                                  TokenState state) {
    child.seq = seq;
    child.off = off;
    child.len = len;
    
    return child.getToken2(state);
  };*/
  
  //
  protected final Reader getCurrentReader() {
    return getReader(off, len - off);
  };
  
  //
  protected final Reader getFullReader() {
    return getReader(0, seq.length());
  };
  
  //
  protected final Reader getReader(int from, int len) {
    // Be sure we don't get too much input
    int diff = seq.length() - (from + len);
    if(diff < 0)
      len -= diff;
    
    // Get the appropriate reader
    if(seq instanceof Segment) {
      Segment seg = (Segment)seq;
      return new CharArrayReader(seg.array, from, len);
    };
    return new StringReader(seq.subSequence(from, from + len).toString());
  };
  
  //
  protected int getCurrentOffset() {
    return off;
  };
  
  //
  @Override
  public final synchronized List<Token> parse(CharSequence s, int o, int l,
                                              int limit, TokenState state) {
    //
    List<Token> list = new ArrayList<Token>();
    
    //
    seq = s;
    off = o;
    len = l;
    sta = state;
    
    //
    Token tkn = null;
    
    //
    while(off < limit) {
      Token aux = tkn;
      tkn = getToken2(sta);
      if(tkn == null)
        off++;
      else {
        list.add(tkn);
        sta = tkn.getState();
        off = tkn.end();
      };
    };
    
    //
    return list;
  };
};
