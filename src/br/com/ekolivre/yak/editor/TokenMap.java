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
