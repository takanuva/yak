/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU GPL.                                                            *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*                                                                              *
* Copyright (C) Ekolivre TI, Paulo H. Torrens - 2013.                          *
* Ekolivre TI (http://www.ekolivre.com.br) claims rights over this software;   *
*   you may use for educational or personal uses. For comercial use (even as   *
*   a library), please contact the author.                                     *
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

import java.util.*;
import br.com.ekolivre.yak.editor.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static br.com.ekolivre.yak.editor.TokenType.*;

%%

%class JavaSyntaxKit

%public
%unicode
%type Token
%extends JFlexBasedSyntaxKit<Void>

%{
  //
  @SuppressWarnings("unchecked")
  private static final Map<String, Integer> extensions_map =
    unmodifiableMap(new HashMap() {{
      put("java", null);
      put("jav",  null);
    }});
  
  //
  public JavaSyntaxKit() {
    return;
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
    return extensions_map;
  };
  
  //
  @Override
  public String getContentType() {
    return "text/x-java";
  };
  
  // 
  @Override
  public String getKitName() {
    return "Java";
  };
  
  //
  public int yypos() {
    return zzStartRead;
  };
%}

//
%state YYPACKAGE
%state YYIMPORTS
%state YYIMPORT1
%state YYIMPORT2
%state YYIMPORT3
%state YYCLASS
%state YYSTRING
%state YYSTRING_INCOMPLETE
%state YYCHARACTER
%state YYCHARACTER_INCOMPLETE

%%

<YYINITIAL,YYPACKAGE,YYIMPORTS,YYIMPORT1,YYIMPORT2,YYIMPORT3,YYCLASS> {
  //
  "//" {
    return checkComment("//");
  }
  
  //
  "/*" {
    return checkComment("/*", "*/");
  }
}

<YYINITIAL> {
  //
  "package" {
    yybegin(YYPACKAGE);
    return token(HEADING);
  }
  
  //
  . {
    yybegin(YYIMPORTS);
    yypushback(1);
  }
}

<YYPACKAGE> {
  //
  ";" {
    yybegin(YYIMPORTS);
    return token(HEADING);
  }
  
  //
  . {
    return token(HEADING);
  }
}

<YYIMPORTS> {
  //
  "import" {
    yybegin(YYIMPORT1);
    return token(KEYWORD);
  }
  
  //
  .|\n|\r {
    yybegin(YYCLASS);
    yypushback(1);
  }
}

<YYIMPORT1,YYIMPORT2,YYIMPORT3> {
  ";" {
    yybegin(YYIMPORTS);
    return token(PUNCTUATION);
  }
}

<YYIMPORT3> {
  [^;] {
    return token(DEFAULT_ITALIC.misspell());
  }
}

<YYIMPORT1> {
  "static" {
    yybegin(YYIMPORT2);
    return token(KEYWORD);
  }
}

<YYIMPORT1,YYIMPORT2> {
  ([:jletterdigit:]|[\.\*])+ {
    yybegin(YYIMPORT3);
    return token(DEFAULT_ITALIC);
  }
  
  . {
    return null;
  }
}

<YYCLASS> {
  //
  "import" | "package" {
    return token(KEYWORD.misspell());
  }
  
  //
  "abstract"     |
  "assert"       |
  "boolean"      |
  "break"        |
  "byte"         |
  "case"         |
  "catch"        |
  "char"         |
  "class"        |
  "continue"     |
  "default"      |
  "do"           |
  "double"       |
  "else"         |
  "enum"         |
  "extends"      |
  "final"        |
  "finally"      |
  "float"        |
  "for"          |
  "if"           |
  "implements"   |
  "instanceof"   |
  "int"          |
  "interface"    |
  "long"         |
  "native"       |
  "new"          |
  "private"      |
  "protected"    |
  "public"       |
  "return"       |
  "short"        |
  "static"       |
  "scrictfp"     |
  "super"        |
  "switch"       |
  "synchronized" |
  "this"         |
  "throw"        |
  "throws"       |
  "transient"    |
  "try"          |
  "void"         |
  "volatile"     |
  "while"        |
  "false"        |
  "true"         |
  "null"         {
    return token(KEYWORD);
  }
  
  //
  "const" |
  "goto"  {
    return token(UNUSED_KEYWORD);
  }
  
  //
  "java.lang"("."[:jletter:][:jletterdigit:]*|".")* |
  "Appendable"                                      |
  "AutoCloseable"                                   |
  "CharSequence"                                    |
  "Cloneable"                                       |
  "Comparable"                                      |
  "Iterable"                                        |
  "Readable"                                        |
  "Runnable"                                        |
  "Thraed.UncaughtExceptionHandler"                 |
  "Boolean"                                         |
  "Byte"                                            |
  "Character"                                       |
  "Character.Subset"                                |
  "Character.UnicodeBlock"                          |
  "Class"                                           |
  "ClassLoader"                                     |
  "ClassValue"                                      |
  "Compiler"                                        |
  "Double"                                          |
  "Enum"                                            |
  "Float"                                           |
  "InheritableThreadLocal"                          |
  "Integer"                                         |
  "Long"                                            |
  "Math"                                            |
  "Number"                                          |
  "Object"                                          |
  "Package"                                         |
  "Process"                                         |
  "ProcessBuilder"                                  |
  "ProcessBuilder.Redirect"                         |
  "Runtime"                                         |
  "RuntimePermission"                               |
  "SecurityManager"                                 |
  "Short"                                           |
  "StackTraceElement"                               |
  "StrictMath"                                      |
  "String"                                          |
  "StringBuffer"                                    |
  "StringBuilder"                                   |
  "System"                                          |
  "Thread"                                          |
  "ThreadGroup"                                     |
  "ThreadLocal"                                     |
  "Throwable"                                       |
  "Void"                                            |
  "Character.UnicodeScript"                         |
  "ProcessBuilder.Redirect.Type"                    |
  "Thread.State"                                    |
  "ArithmeticException"                             |
  "ArrayIndexOutOfBoundsException"                  |
  "ArrayStoreException"                             |
  "ClassCastException"                              |
  "ClassNotFoundException"                          |
  "CloneNotSupportedException"                      |
  "EnumConstantNotPresentException"                 |
  "Exception"                                       |
  "IllegalAccessException"                          |
  "IllegalArgumentException"                        |
  "IllegalMonitorStateException"                    |
  "IllegalStateException"                           |
  "IllegalThreadStateException"                     |
  "IndexOutOfBoundsException"                       |
  "InstantiationException"                          |
  "InterruptedException"                            |
  "NegateArraySizeException"                        |
  "NoSuchFieldException"                            |
  "NoSuchMethodException"                           |
  "NullPointerException"                            |
  "NumberFormatException"                           |
  "ReflectiveOperationException"                    |
  "RuntimeException"                                |
  "SecurityException"                               |
  "StringIndexOutOfBoundsException"                 |
  "TypeNotPresentException"                         |
  "UnsupportedOperationException"                   |
  "AbstractMethodError"                             |
  "AssertionError"                                  |
  "BootstrapMethodError"                            |
  "ClassCircularityError"                           |
  "ClassFormatError"                                |
  "Error"                                           |
  "ExceptionInitializerError"                       |
  "IllegalAccessError"                              |
  "IncompatibleClassChangeError"                    |
  "InstantiationError"                              |
  "InternalError"                                   |
  "LinkageError"                                    |
  "NoClassDefFoundError"                            |
  "NoSuchFieldError"                                |
  "NoSuchMethodError"                               |
  "StackOverflowError"                              |
  "ThreadDeath"                                     |
  "UnknownError"                                    |
  "UnsatisfiedLinkError"                            |
  "UnsupportedClassVersionError"                    |
  "VerifyError"                                     |
  "VirtualMachineError"                             |
  "@Deprecated"                                     |
  "@FunctionInterface"                              |
  "@Override"                                       |
  "@SafeVarargs"                                    |
  "@SuppressWarnings"                               {
    return token(STANDARD);
  }
  
  //
  [:digit:]+([Ee][+-]?[:digit:]+)?[Ll]?              |
  [:digit:]+"."[:digit:]*([Ee][+-]?[:digit:]+)?[Ff]? |
  [:digit:]*"."[:digit:]+([Ee][+-]?[:digit:]+)?[Ff]? |
  0[Xx]([:digit:]|[A-Fa-f])+                         {
    return token(NUMBER);
  }
  
  //
  "@"[:jletter:][:jletterdigit:]* {
    return token(ANNOTATION);
  }
  
  //
  [:jletter:][:jletterdigit:]* {
    return token(IDENTIFIER);
  }
  
  //
  "\"" / ("\\".|"\\"[\r][\n]?|"\\"[\n]|[^\"\\\r\n])*$ {
    yypush(YYSTRING_INCOMPLETE);
    return token(STRING_INCOMPLETE);
  }
  
  //
  "\"" {
    yypush(YYSTRING);
    return token(STRING);
  }
  
  //
  [;:?<>=/+-*&\^%!,.~] {
    return token(PUNCTUATION);
  }
  
  //
  "(" {
    return token(PUNCTUATION, +'(');
  }
  
  //
  ")" {
    return token(PUNCTUATION, -'(');
  }
  
  //
  "[" {
    return token(PUNCTUATION, +'[');
  }
  
  //
  "]" {
    return token(PUNCTUATION, -'[');
  }
  
  //
  "{" {
    return token(PUNCTUATION, +'{');
  }
  
  //
  "}" {
    return token(PUNCTUATION, -'{');
  }
}

/*<YYSTRING> {
  //
  "\"" {
    yybegin(YYCLASS);
    return token(STRING);
  }
  
  //
  "\\". {
    return token(STRING_ESCAPE);
  }
  
  //
  "%". {
    return token(STRING_FORMAT);
  }
  
  //
  [^\"\\%\r\n]+ {
    return token(STRING);
  }
}

<YYCHARACTER> {
  //
  "'" {
    yybegin(YYCLASS);
    return token(CHARACTER);
  }
  
  //
  "\\". {
    return token(CHARACTER_ESCAPE);
  }
  
  //
  [^\'\\\r\n]+ {
    return token(CHARACTER);
  }
}*/









<YYSTRING> {
  //
  "\"" {
    yypop();
    return token(STRING);
  }
  
  //
  "\\".|"\\"\r\n?|"\\"\n {
    return token(STRING_ESCAPE);
  }
  
  //
  [^\"\$\r\n]+ {
    return token(STRING);
  }
}

<YYSTRING_INCOMPLETE> {
  //
  "\\"[^\r\n] {
    return token(STRING_ESCAPE);
  }
  
  //
  "\\"\r\n?|"\\"\n {
    return token(STRING_ESCAPE);
  }
  
  //
  "\\" {
    return token(STRING_ESCAPE);
  }
  
  //
  [^\\\r\n]+ {
    return token(STRING_INCOMPLETE);
  }
  
  //
  [\r\n] {
    yypop();
    return token(DEFAULT);
  }
}







.|\r|\n {
  return null;
}

<<EOF>> {
  return null;
}
