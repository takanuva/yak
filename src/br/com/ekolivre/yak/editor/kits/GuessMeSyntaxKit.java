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
package br.com.ekolivre.yak.editor.kits;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.text.*;
import br.com.ekolivre.yak.editor.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static br.com.ekolivre.yak.editor.TokenType.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class GuessMeSyntaxKit extends DefaultSyntaxKit {
  //
  private static class GuessMeTokenState extends TokenState {
    //
    int state = 0;
    
    //
    String current_mime[];
    
    //
    DefaultSyntaxKit kit;
    
    //
    Reader reader;
    
    //
    Thread thread[];
    
    //
    TokenState child;
    
    //
    GuessMeTokenState(String current_mime[], String mime, Reader reader,
                      Thread thread[], TokenState child) {
      this.current_mime = current_mime;
      this.kit = DefaultSyntaxKit.getKitForContentType(mime);
      this.reader = reader;
      this.thread = thread;
      this.child = child;
    };
    
    //
    GuessMeTokenState(GuessMeTokenState other, TokenState child) {
      this.current_mime = other.current_mime;
      this.kit = other.kit;
      this.reader = other.reader;
      this.thread = other.thread;
      this.child = child;
    };
    
    //
    @Override
    public int state() {
      return child.state();
    };
    
    //
    @Override
    public boolean needLookbehind() {
      boolean res;
      
      synchronized(current_mime) {
        res = current_mime[0] != kit.getContentType();
      };
      
      res = res || child.needLookbehind();
      
      return res;
    };
  };
  
  //
  private static final String CLASSIFIER = "/classifier.bys";
  
  //
  private static LinkedHashMap<String, LinkedHashMap<String, Integer>> langs;
  private static Integer sum;
  
  static {
    
    try {
      InputStream is = GuessMeSyntaxKit.class.getResourceAsStream(CLASSIFIER);
      ObjectInputStream ois = new ObjectInputStream(is);
      
      langs = (LinkedHashMap<String, LinkedHashMap<String, Integer>>)ois.readObject();
      sum = ois.readInt();
    } catch(Throwable t) {
      //
    };
    
  };
  
  //
  public GuessMeSyntaxKit() {
    
  };
  
  /*//
  @Override
  protected final Token getToken(TokenState state) {
    if(state instanceof GuessMeTokenState) {
      return ((GuessMeTokenState)state).getChildToken();
    };
    // If we fall here, this means that we are at position 0... we then should
    // generate a placeholder token (len: 0) with a detector thread; this way
    // we may asynchronously keep trying to detect what language is being typed
    return getPlaceholderToken();
  };*/
  
  
  @Override
  public final synchronized List<Token> parse(CharSequence s, int o, int l,
                                              int limit, TokenState state) {
    //
    if(o == 0) {
      // Start here!
      Reader reader = getReader(s, l);
      
      String mime = detectMimeType(reader);
      String detector[] = {mime};
      
      Thread thread = classifyDocument(reader, detector);
      Thread classifier[] = {thread};
      
      state = new GuessMeTokenState(detector, mime, reader, classifier, state);
      
      thread.start();
    };
    
    assert state != null;
    assert state instanceof GuessMeTokenState;
    
    GuessMeTokenState state2 = (GuessMeTokenState)state;
    
    if(!state2.thread[0].isAlive()) {
      state2.thread[0] = classifyDocument(state2.reader, state2.current_mime);
      state2.thread[0].start();
    };
    
    List<Token> sub = state2.kit.parse(s, o, l, limit, state2.child);
    
    List<Token> res = new ArrayList<Token>();
    
    Token tmp = null;
    for(Token i: sub) {
      GuessMeTokenState ts = new GuessMeTokenState(state2, i.getState());
      tmp = new Token(i.start(), i.length(), i.getType(), ts, i.match());
      res.add(tmp);
    };
    
    return res;
    
  };
  
  //
  private Reader getReader(CharSequence seq, int length) {
    
    // Check only the first 10kb of code
    int limit = 10240;
    if(limit > length)
      length = limit;
    
    if(seq instanceof Segment) {
      Segment seg = (Segment)seq;
      return new CharArrayReader(seg.array, 0, limit);
    };
    return new StringReader(seq.subSequence(0, limit).toString());
    
  };
  
  //
  private Thread classifyDocument(final Reader reader,
                                  final String detector[]) {
    return new Thread(() -> {
      try {
        // Sleep for 4 seconds
        Thread.sleep(4000000);
        
        String mime = detectMimeType(reader);
        
        synchronized(detector) {
          detector[0] = mime;
        };
        
        
      } catch(InterruptedException e) {
        //
      } finally {
        
      };
    });
  };
  
  //
  private synchronized String detectMimeType(Reader reader) {
    GenericDetectionScanner scanner = new GenericDetectionScanner(reader);
    return scanner.classify(langs, sum);
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
    return "text/x-source-code";
  };
  
  // 
  @Override
  public String getKitName() {
    return "Guess me! :)";
  };
};
