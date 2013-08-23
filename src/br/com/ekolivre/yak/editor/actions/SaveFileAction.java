/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU GPL.                                                            *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*                                                                              *
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

public class SaveFileAction extends AbstractEditorAction {
  public static interface Callback {
    void run(File filename);
  };
  
  private JFrame frame;
  private DefaultSyntaxKit kit;
  private File file;
  private Callback callback;
  private JFileChooser chooser = new JFileChooser();
  
  public SaveFileAction(JFrame frame, DefaultSyntaxKit kit, File file,
                                                            Callback callback) {
    this.frame = frame;
    this.kit = kit;
    this.file = file;
    this.callback = callback;
    
    java.util.List<DefaultSyntaxKit> list =
      DefaultSyntaxKit.getArbitraryKitsList();
    
    for(DefaultSyntaxKit aux: list) {
      
      String exts[] = aux.extensionsAsArray();
      
      if(exts != null && exts.length > 0) {
      
        String label = aux.getKitName() + " (";
        for(String ext: exts)
          label += "*." + ext + ", ";
        label = label.substring(0, label.length() - 2) + ")";
        
        if(label.length() > 2) {
        
          FileNameExtensionFilter filter = new FileNameExtensionFilter(
            label,
            exts
          );
          
          chooser.addChoosableFileFilter(filter);
          
          if(kit.getContentType() == aux.getContentType())
            chooser.setFileFilter(filter);
        };
      };
    };
  };
  
  @Override
  protected JComponent makeComponents()[] {
    return new JComponent[] {
      makeButton("icons/document-save.png", () -> {
        if(file == null) {
          //
          int ret = chooser.showSaveDialog(frame);
          if(ret == JFileChooser.APPROVE_OPTION) {
            file = normalize(chooser.getSelectedFile());
            callback.run(file);
          };
        } else callback.run(file);
      }),
      makeButton("icons/document-save-as.png", () -> {
        //
        int ret = chooser.showSaveDialog(frame);
        if(ret == JFileChooser.APPROVE_OPTION) {
          file = normalize(chooser.getSelectedFile());
          callback.run(file);
        };
      })
    };
  };
  
  private File normalize(File file) {
    return file;
  };
};
