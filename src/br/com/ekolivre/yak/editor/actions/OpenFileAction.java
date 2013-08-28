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
package br.com.ekolivre.yak.editor.actions;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.prefs.*;
import javax.swing.filechooser.*;
import br.com.ekolivre.yak.editor.*;

public class OpenFileAction extends AbstractEditorAction {
  public static interface Callback {
    void run(File filename);
  };
  
  private JFrame frame;
  private Callback callback;
  private JFileChooser chooser = new JFileChooser();
  
  public OpenFileAction(JFrame frame, Callback callback) {
    this.frame = frame;
    this.callback = callback;
    
    chooser.setMultiSelectionEnabled(true);
    
    java.util.List<DefaultSyntaxKit> list =
      DefaultSyntaxKit.getArbitraryKitsList();
    
    for(DefaultSyntaxKit kit: list) {
      
      String exts[] = kit.extensionsAsArray();
      
      if(exts != null && exts.length > 0) {
      
        String label = kit.getKitName() + " (";
        for(String ext: exts)
          label += "*." + ext + ", ";
        label = label.substring(0, label.length() - 2) + ")";
        
        if(label.length() > 2) {
          FileNameExtensionFilter filter = new FileNameExtensionFilter(
            label,
            exts
          );
          
          chooser.addChoosableFileFilter(filter);
          
          if(kit.getContentType() == DefaultSyntaxKit.defaultContentType())
            chooser.setFileFilter(filter);
        };
      };
    };
  };
  
  @Override
  protected JComponent makeComponents()[] {
    return new JComponent[] {
      makeButton("icons/document-open.png", () -> {
        int ret = chooser.showOpenDialog(frame);
        if(ret == JFileChooser.APPROVE_OPTION) {
          for(File file: chooser.getSelectedFiles())
            callback.run(file);
        };
      })
    };
  };
  
};
