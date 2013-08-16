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
  private LinkedHashMap<String, Integer> tokens;
  private String prev;
  
  private void learn(LinkedHashMap<String, Integer> map)
  throws IOException {
    tokens = map;
    prev = null;
    yylex();
  };
  
  private void keepTrack() {
    String s = yytext().toLowerCase();
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
    LinkedHashMap<String, LinkedHashMap<String, Integer>> languages, int sum
  ) {
    //
    tokens = new LinkedHashMap<String, Integer>();
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
    for(Map.Entry<String, LinkedHashMap<String, Integer>> entry:
        languages.entrySet()) {
      
      double d = tokens_probability(entry.getValue(), sum);
      
      if(max == null || d > max) {
        max = d;
        res = entry.getKey();
      };
      
      //out.printf("Probability for %s is %s%n", entry.getKey(), d);
      
    };
    
    //
    return res;
  };
  
  private double tokens_probability(LinkedHashMap<String, Integer> language,
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
                                   LinkedHashMap<String, Integer> language, int sum) {
    if(language.size() == 0)
      return 1.0;
    
    Integer i = language.get(token);
    if(i == null || i == 0)
      return 1.0 / sum;
    else
      return ((double)i) / language.get(null);
    
  };
  
  private static final void learn(File file,
                                  LinkedHashMap<String, Integer> map) {
    //
    if(file.isDirectory()) {
      for(File child: file.listFiles()) {
        learn(child, map);
      };
    } else {
      try {
        GenericDetectionScanner scanner = new GenericDetectionScanner(
          new FileReader(file)
        );
        
        scanner.learn(map);
      } catch(Exception e) {
        return;
      };
    };
  };
  
  public static final void main(String... args) throws Throwable {
    
    int sum = 0;
    LinkedHashMap<String, Map<String, Integer>> languages = new LinkedHashMap<>();
    
    File database = new File(args[0]);
    
    if(database.isDirectory()) {
      
      for(File dir: database.listFiles()) {
        
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();        
        String mime = "text/" + dir.getName();
        
        if(!dir.isDirectory())
          continue;
        
        learn(dir, map);
        
        int aux = 0;
        //for(Integer i: map.values())
        //  aux += i;
        
        for(Map.Entry<String, Integer> e: map.entrySet()) {
          aux += e.getValue();
        };
        
        
        map.put(null, aux);
        sum += aux;
        
        languages.put(mime, map);
        
      };
      
    };
    
    /*
    GenericDetectionScanner scanner = new GenericDetectionScanner(
      new FileReader(new File(args[1]))
    );
    
    out.printf("File is: %s%n", scanner.classify(languages, sum));
    */
    File out = new File(args[1]);
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(out));
    oos.writeObject(languages);
    oos.writeInt(sum);
    oos.close();
  };
%}

%state YYNOT_SOF

%%

<YYINITIAL> {
  // A shebang line! :3
  "#!".*[\r\n] {
    //out.printf("We got a shebang!!!%n");
    keepTrack();
  }
  
  // We didn't get a shebang, just tokenize the remaining file
  .|\r|\n {
    yybegin(YYNOT_SOF);
    yypushback(1);
  }
}

<YYNOT_SOF> {
  
  // Skip newlines
  (" "|\t|\n|\r)+ {
    // Ignore
  }
  
  // Skip possible comments!
  "/*" ~"*/"         |
  "(*" ~"*)"         |
  "{-" ~"-}"         |
  "\"\"\"" ~"\"\"\"" |
  "<!--" ~"-->"      |
  "<![CDATA[" ~"]]>" |
  "=begin" ~"=end"   |
  ^"=" ~[\r\n]"=cut" |
  "#" ~[\r\n]        |
  "DNL" ~[\r\n]      |
  "REM" ~[\r\n]      {
    // Ignore
  }
  
  // Skip possible string literals!
  "\"" ~"\""   |
  "\'" ~"\'"   |
  "\"" ~[\r\n] |
  "\'" ~[\r\n] {
    // Ignore
  }
  
  // Skip common number literals
  (0[XxOo])?[A-Fa-f0-9]+[\.A-Fa-f0-9]* {
    // Ignore
  }
  
  // Common (multi-character) tokens for many programming languages
  ":="                          |
  "<>"                          |
  "<?"                          |
  "?>"                          |
  "<="                          |
  ">="                          |
  "=>"                          |
  "=<"                          |
  "->"                          |
  "<-"                          |
  "!="                          |
  "<<<"                         |
  ">>>"                         |
  "<<"                          |
  ">>"                          |
  "--"                          |
  "++"                          |
  "**"                          |
  "..."                         |
  "[]"                          |
  ":"[\r\n]                     | // Helps to identify Python over Ruby :)
  "||"                          |
  "&&"                          |
  "<"[:jletterdigit:]+">"       |
  "<"[:jletterdigit:]+"/>"      |
  [:letter:]+"#"[:letter:]*     |
  [:letter:]*"#"[:letter:]+     |
  ([:letter:]|[:digit:]|[_\-])+ {
    keepTrack();
  }
  
  // Raw characters (I hope this helps...)
  [~`!@#$%\^&*()\-\+=\[\]{}\\|/?<>,.:;] {
    keepTrack();
  }
  
  //
  . {
    
  }
  
  //
  \r|\n {
    // Ignore
  }
}

// End Of File
<<EOF>> {
  return;
}
