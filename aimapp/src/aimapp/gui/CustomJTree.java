/*
 * http://www.java-tips.org/java-se-tips/javax.swing/have-a-popup-attached-to-a-jtree.html
 */
package aimapp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class CustomJTree extends JTree implements ActionListener{
    JPopupMenu popup;
    JMenuItem mi;
    
    CustomJTree(DefaultMutableTreeNode dmtn) {
        super(dmtn);
    
        addMouseListener(
                new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if ( e.isPopupTrigger()) {
                    popup.show( (JComponent)e.getSource(), e.getX(), e.getY() );
                }
            }
        }
        );
        
    }
    public void actionPerformed(ActionEvent ae) {
        DefaultMutableTreeNode dmtn, node;
        
        TreePath path = this.getSelectionPath();
        dmtn = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (ae.getActionCommand().equals("insert")) {
            
        }
    }
}
