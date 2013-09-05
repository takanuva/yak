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

/**
 * 
 */
%%

%class CSyntaxKit

%public
%unicode
%type Token
%extends JFlexBasedSyntaxKit<Void>

%{
  //
  public static final int KRC =  0x01;
  public static final int C89 =  0x02 | KRC;
  public static final int NA1 =  0x04 | C89;
  public static final int C99 =  0x08 | NA1;
  public static final int C11 =  0x10 | C99;
  public static final int MSC =  0x20 | C11;
  public static final int GNU =  0x40 | C11;
  public static final int INO =  0x80 | GNU;
  public static final int UPC = 0x100 | GNU;
  
  //
  @SuppressWarnings("unchecked")
  private static final Map<Integer, String> dialects = 
    unmodifiableMap(new TreeMap() {{
      put(KRC, "Old-Style K&R C");
      put(C89, "ANSI/ISO C 89");
      put(C99, "ANSI/ISO C 99");
      put(C11, "ANSI/ISO C 11");
      put(MSC, "Microsoft C");
      put(GNU, "GNU C");
      put(INO, "Arduino C");
      put(UPC, "Unified Parallel C");
    }});
  
  //
  @SuppressWarnings("unchecked")
  private static final Map<String, Integer> extensions_map =
    unmodifiableMap(new TreeMap() {{
      put("c",   C11);
      put("krc", KRC);
      put("c89", C89);
      put("c99", C99);
      put("c11", C11);
      put("msc", MSC);
      put("gnu", GNU);
      put("ino", INO);
      put("pde", INO);
      put("upc", UPC);
    }});
  
  //
  public CSyntaxKit() {
    return;
  };
  
  //
  @Override
  public Map<Integer, String> getDialectList() {
    return dialects;
  };
  
  //
  @Override
  public int getDefaultDialect() {
    return GNU;
  };
  
  //
  @Override
  public Map<String, Integer> getFileExtensions() {
    return extensions_map;
  };
  
  //
  @Override
  public String getContentType() {
    return "text/x-c";
  };
  
  // 
  @Override
  public String getKitName() {
    return "C";
  };
  
  //
  public int yypos() {
    return zzStartRead;
  };
%}

x = [diouxX]
N = [:digit:]+

%state YYPREPROC_COMMENT
%state YYPREPROCESSOR
%state YYPREPROCESSOR2
%state YYINCLUDE
%state YYINCLUDE_QUOTE
%state YYINCLUDE_ANGLE

%%

<YYINITIAL> {
  //
  ^[\ \s]*"#" {
    yybegin(YYPREPROCESSOR);
    return token(PREPROCESSOR);
  }
  
  //
  ^[\ \s]*"#"[\ \s]*"if"[\ \s]*"0"x?"0"*[\ \s]*$ {
    yybegin(YYPREPROC_COMMENT);
    return token(HEREDOC_COMMENT);
  }
  
  //
  "auto"     |
  "break"    |
  "case"     |
  "char"     |
  "const"    |
  "continue" |
  "default"  |
  "do"       |
  "double"   |
  "else"     |
  "enum"     |
  "extern"   |
  "float"    |
  "for"      |
  "goto"     |
  "if"       |
  "int"      |
  "long"     |
  "register" |
  "return"   |
  "short"    |
  "signed"   |
  "sizeof"   |
  "static"   |
  "struct"   |
  "switch"   |
  "typedef"  |
  "union"    |
  "unsigned" |
  "void"     |
  "volatile" |
  "while"    {
    return token(KEYWORD);
  }
  
  //
  "__LINE__" |
  "__FILE__" |
  "__func__" {
    if(getDialect() >= C99)
      return token(STANDARD);
  }
  
  //
  "__attribute__"       |
  "__FUNCTION__"        |
  "__PRETTY_FUNCTION__" {
    if((getDialect() & GNU) == GNU)
      return token(KEYWORD);
  }
  
  //
  "__declspec"   |
  "__FUNCTION__" |
  "__FUNCSIG__"  {
    if((getDialect() & MSC) == MSC)
      return token(KEYWORD);
  }
  
  // assert.h
  "NDEBUG" |
  "assert" {
    return token(STANDARD);
  }
  
  "main" {
    return token(STANDARD);
  }
  
  // ctype.h
  "isalnum"  |
  "isalpha"  |
  "iscntrl"  |
  "isdigit"  |
  "isgraph"  |
  "islower"  |
  "isprint"  |
  "ispunct"  |
  "isspace"  |
  "isupper"  |
  "isxdigit" |
  "tolower"  |
  "toupper"  {
    return token(STANDARD);
  }
  
  // ctype.h - C11
  "isblank" {
    if(getDialect() >= C11)
      return token(STANDARD);
  }
  
  // errno.h (variables)
  "errno" {
    return token(STANDARD);
  }
  
  // fenv.h - C99
  "feclearexcept"   |
  "feraiseexcept"   |
  "fegetexceptflag" |
  "fesetexceptflag" |
  "fegetround"      |
  "fesetround"      |
  "fegetenv"        |
  "fesetenv"        |
  "feholdexcept"    |
  "feupdateenv"     |
  "fenv_t"          |
  "fexcept_t"       {
    if(getDialect() >= C99)
      return token(STANDARD);
  }
  
  // float.h
  "FLT_RADIX"       |
  "FLT_MANT_DIG"    |
  "DBL_MANT_DIG"    |
  "LDBL_MANT_DIG"   |
  "FLT_DIG"         |
  "DBL_DIG"         |
  "LDBL_DIG"        |
  "FLT_MIN_EXP"     |
  "DBL_MIN_EXP"     |
  "LDBL_MIN_EXP"    |
  "FLT_MIN_10_EXP"  |
  "DBL_MAX_10_EXP"  |
  "LDBL_MAX_10_EXP" |
  "FLT_MAX"         |
  "DBL_MAX"         |
  "LDBL_MAX"        |
  "FLT_EPSILON"     |
  "DBL_EPSILON"     |
  "LDBL_EPSILON"    |
  "FLT_MIN"         |
  "DBL_MIN"         |
  "LDBL_MIN"        |
  "FLT_ROUNDS"      {
    return token(STANDARD);
  }
  
  // float.h - C99
  "FLT_EVAL_METHOD" |
  "DECIMAL_DIG"     {
    if(getDialect() >= C99)
      return token(STANDARD);
  }
  
  // inttypes.h - C99
  PRI{x}MAX      |
  PRI{x}{N}      |
  PRI{x}LEAST{N} |
  PRI{x}FAST{N}  |
  PRI{x}PTR      |
  SCN{x}MAX      |
  SCN{x}{N}      |
  SCN{x}LEAST{N} |
  SCN{x}FAST{N}  |
  SCN{x}PTR      {
    if(getDialect() >= C99)
      return token(STANDARD);
  }
  
  // iso646.h - NA1
  "and"    |
  "and_eq" |
  "bitand" |
  "bitor"  |
  "compl"  |
  "not"    |
  "not_eq" |
  "or"     |
  "or_eq"  |
  "xor"    |
  "xor_eq" {
    if(getDialect() >= NA1)
      return token(STANDARD);
  }
  
  // limits.h
  "CHAR_BIT"   |
  "SCHAR_MIN"  |
  "SCHAR_MAX"  |
  "UCHAR_MAX"  |
  "CHAR_MIN"   |
  "CHAR_MAX"   |
  "MB_LEN_MAX" |
  "SHRT_MIN"   |
  "SHRT_MAX"   |
  "USHRT_MAX"  |
  "INT_MIN"    |
  "INT_MAX"    |
  "UINT_MAX"   |
  "LONG_MIN"   |
  "LONG_MAX"   |
  "ULONG_MAX"  {
    return token(STANDARD);
  }
  
  // limits.h - C99
  "LLONG_MIN"  |
  "LLONG_MAX"  |
  "ULLONG_MAX" {
    if(getDialect() >= C99)
      return token(STANDARD);
  }
  
  // locale.h
  "lconv"      |
  "setlocale"  |
  "localeconv" {
    return token(STANDARD);
  }
  
  // math.h
  "cos"              |
  "sin"              |
  "tan"              |
  "acos"             |
  "asin"             |
  "atan"             |
  "atan2"            |
  "cosh"             |
  "sinh"             |
  "tanh"             |
  "acosh"            |
  "asinh"            |
  "atanh"            |
  "exp"              |
  "frexp"            |
  "ldexp"            |
  "log"              |
  "log10"            |
  "modf"             |
  "pow"              |
  "sqrt"             |
  "ceil"             |
  "floor"            |
  "fmod"             |
  "copysign"         |
  "NAN"              |
  "nextafter"        |
  "nexttoward"       |
  "fdim"             |
  "fmax"             |
  "fmin"             |
  "fabs"             |
  "abs"              |
  "fpclassify"       |
  "isfinite"         |
  "isinf"            |
  "isnan"            |
  "isnormal"         |
  "signbit"          |
  "isgreater"        |
  "isgreaterequal"   |
  "isless"           |
  "islessequal"      |
  "islessgreater"    |
  "isunordered"      |
  "math_errhandling" |
  "INFINITY"         |
  "HUGE_VAL"         |
  "HUGE_VALF"        |
  "HUGE_VALL"        |
  "MATH_ERRNO"       |
  "MATH_ERREXCEPT"   |
  "FP_FAST_FMA"      |
  "FP_FAST_FMAF"     |
  "FP_FAST_FMAL"     |
  "FP_INFINITE"      |
  "FP_NAN"           |
  "FP_NORMAL"        |
  "FP_SUBNORMAL"     |
  "FP_ZERO"          |
  "FP_ILOGB0"        |
  "FP_ILOGBNAN"      |
  "double_t"         |
  "float_t"          {
    return token(STANDARD);
  }
  
  // locale.h - C99
  "exp2"      |
  "expm1"     |
  "ilog"      |
  "log1p"     |
  "log2"      |
  "logb"      |
  "scalbn"    |
  "scalbln"   |
  "cbrt"      |
  "hypot"     |
  "erf"       |
  "erfc"      |
  "tgamma"    |
  "lgamma"    |
  "trunc"     |
  "round"     |
  "lround"    |
  "llround"   |
  "rint"      |
  "lrint"     |
  "llrint"    |
  "nearbyint" |
  "remainder" |
  "remquo"    |
  "fma"       {
    if(getDialect() >= C99)
      return token(STANDARD);
  }
  
  // setjmp.h
  "longjmp" |
  "setjmp"  |
  "jmp_buf" {
    return token(STANDARD);
  }
  
  // signal.h
  "signal"       |
  "raise"        |
  "sig_atomic_t" |
  "SIGABRT"      |
  "SIGFPE"       |
  "SIGILL"       |
  "SIGINT"       |
  "SIGSEGV"      |
  "SIGTERM"      |
  "SIG_DFL"      |
  "SIG_IGN"      |
  "SIG_ERR"      {
    return token(STANDARD);
  }
  
  // stdarg.h
  "va_list"  |
  "va_start" |
  "va_arg"   |
  "va_end"   {
    return token(STANDARD);
  }
  
  // stdarg.h - C99
  "va_copy" {
    if(getDialect() >= C99)
      return token(STANDARD);
  }
  
  // stdbool.h - C99
  "true"                          |
  "false"                         |
  "bool"                          |
  "__bool_true_false_are_defined" {
    if(getDialect() >= C99)
      return token(STANDARD);
  }
  
  
  
  
  
  
  
  u?"int"{N}"_t" {
    return token(STANDARD);
  }
  
  //
  [:jletter:][:jletterdigit:]* {
    return token(IDENTIFIER);
  }
}

<YYPREPROCESSOR> {
  "pragma"   |
  "define"   |
  "ifdef"    |
  "ifndef"   |
  "if"       |
  "else"     |
  "undef"    |
  "assert"   |
  "line"     |
  "unassert" {
    yybegin(YYPREPROCESSOR2);
    return token(PREPROCESSOR_KEYWORD);
  }
  
  "include"      |
  "import"       {
    yybegin(YYINCLUDE);
    return token(PREPROCESSOR_KEYWORD);
  }
  
  "include_next" {
    if((getDialect() & GNU) == GNU) {
      yybegin(YYINCLUDE);
      return token(PREPROCESSOR_KEYWORD);
    };
  }
  
  [^\ \t\r\n]+ {
    yybegin(YYPREPROCESSOR2);
    return token(PREPROCESSOR_KEYWORD.misspell());
  }
  
}

<YYPREPROCESSOR2> {
  [^\ \t\r\n]+ {
    return token(PREPROCESSOR);
  }
}

<YYINCLUDE> {
  "\"" {
    yybegin(YYINCLUDE_QUOTE);
    return token(PREPROCESSOR_STRING);
  }
  
  "<" {
    yybegin(YYINCLUDE_ANGLE);
    return token(PREPROCESSOR_STRING);
  }
  
  . {
    yypushback(1);
    yybegin(YYPREPROCESSOR2);
    return token(DEFAULT);
  }
}

<YYPREPROCESSOR,YYPREPROCESSOR2,YYINCLUDE,YYINCLUDE_QUOTE,YYINCLUDE_ANGLE> {
  [\r\n] {
    yypushback(1);
    yybegin(YYINITIAL);
    return token(DEFAULT);
  }
}

<YYPREPROC_COMMENT> {
  ^[\ \s]*"#"[\ \s]*"endif"[\ \t].*$ {
    yybegin(YYINITIAL);
    return token(HEREDOC_COMMENT);
  }
  
  [^\ \t\r\n]+ {
    return token(HEREDOC_COMMENT);
  }
}

<YYINCLUDE_QUOTE> {
  "\"" {
    yybegin(YYPREPROCESSOR2);
    return token(PREPROCESSOR_STRING);
  }
  
  [^\"]+ {
    return token(PREPROCESSOR_STRING);
  }
}

<YYINCLUDE_ANGLE> {
  ">" {
    yybegin(YYPREPROCESSOR2);
    return token(PREPROCESSOR_STRING);
  }
  
  [^\>]+ {
    return token(PREPROCESSOR_STRING);
  }
}

.|\r|\n {
  return null;
}

<<EOF>> {
  return null;
}
