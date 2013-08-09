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
import static java.util.Collections.*;

%%

%class GenericDetectionScanner

%public
%unicode
%caseless
%type void

%{
  private HashMap<String, Integer> tokens;
  private String prev;
  
  private void learn(HashMap<String, Integer> map)
  throws IOException {
    tokens = map;
    prev = null;
    yylex();
  };
  
  private void keepTrack() {
    String s = yytext();
    Integer i = tokens.get(s);
    if(i == null)
      i = 0;
    tokens.put(s, ++i);
    
    /*if(prev != null) {
      String x = prev + " " + s;
      i = tokens.get(x);
      if(i == null)
        i = 0;
      tokens.put(x, ++i);
    };
    
    prev = s;*/
  };
  
  public String classify(
    HashMap<String, HashMap<String, Integer>> languages, int sum
  ) {
    //
    tokens = new HashMap<String, Integer>();
    prev = null;
    try {
      yylex();
    } catch(IOException e) {
      return null;
    };
    
    //
    String res = null;
    Double max = null;
    
    //
    for(Map.Entry<String, HashMap<String, Integer>> entry:
        languages.entrySet()) {
      
      double d = tokens_probability(entry.getValue(), sum);
      
      if(max == null || d > max) {
        max = d;
        res = entry.getKey();
      };
      
      out.printf("Probability for %s is %s%n", entry.getKey(), d);
      
    };
    
    //
    return res;
  };
  
  private double tokens_probability(HashMap<String, Integer> language,
                                    int sum) {
    double res = 0.0;
    for(String token: tokens.keySet()) {
      double aux = token_probability(token, language, sum);
      double tmp = Math.log(aux);
      //out.printf("aux = %s; tmp = %s%n", aux, tmp);
      res += tmp;
    };
    return res;
  };
  
  private double token_probability(String token,
                                   HashMap<String, Integer> language, int sum) {
    if(language.size() == 0)
      return 1.0;
    
    Integer i = language.get(token);
    if(i == null || i == 0)
      return 1.0 / sum;
    else
      return ((double)i) / language.get(null);
    
  };
  
  public static final void main(String... args) throws Throwable {
    
    int sum = 0;
    HashMap<String, HashMap<String, Integer>> languages = new HashMap<>();
    
    File database = new File(args[0]);
    
    if(database.isDirectory()) {
      
      for(File dir: database.listFiles()) {
        
        HashMap<String, Integer> map = new HashMap<>();        
        String mime = "text/" + dir.getName();
        
        if(!dir.isDirectory())
          continue;
        
        for(File file: dir.listFiles()) {
          try {
            GenericDetectionScanner scanner = new GenericDetectionScanner(
              new FileReader(file)
            );
            
            scanner.learn(map);
          } catch(Exception e) {
            continue;
          };
        };
        
        int aux = 0;
        for(Integer i: map.values())
          aux += i;
        
        map.put(null, aux);
        sum += aux;
        
        
        languages.put(mime, map);
        
      };
      
    };
    
    
    GenericDetectionScanner scanner = new GenericDetectionScanner(
      new FileReader(new File(args[1]))
    );
    
    out.printf("File is: %s%n", scanner.classify(languages, sum));
    
    
    
  };
%}

%%

// Skip possible comments!
"/*" ~"*/"         |
"(*" ~"*)"         |
"{-" ~"-}"         |
"\"\"\"" ~"\"\"\"" |
"<!--" ~"-->"      {
  // Ignore
}


// Skip possible string literals!
"\"" ~"\"" | "\'" ~"\'" {
  // Ignore
}

// Skip common number literals
(0[XxOo])?[A-Fa-f0-9]+[\.A-Fa-f0-9]* {
  // Ignore
}

// Common (multi-character) tokens for many programming languages
":="                      |
"<>"                      |
"<?"                      |
"?>"                      |
"<="                      |
">="                      |
"<!--"                    |
"-->"                     |
"=>"                      |
"=<"                      |
"->"                      |
"<-"                      |
"!="                      |
"<<<"                     |
">>>"                     |
"<<"                      |
">>"                      |
"--"                      |
"++"                      |
"**"                      |
"..."                     |
"[]"                      |
":"[\r\n]                 | // Helps to identify Python over Ruby :)
"||"                      |
"&&"                      |
"<"[:jletterdigit:]+">"   |
"<"[:jletterdigit:]+"/>"  |
[:letter:]+"#"[:letter:]* |
[:letter:]*"#"[:letter:]+ |
[:jletterdigit:]+         {
  keepTrack();
}

// Skip newlines
" "|\t|\n|\r {
  // Ignore
}

// Raw characters (I hope this helps...)
[^\ \t] {
  keepTrack();
}

// End Of File
<<EOF>> {
  return;
}
