package aimapp.gui.actions;

import java.awt.event.FocusEvent;
import javax.swing.JTextField;

public class JTextFieldCursorAtEnd implements java.awt.event.FocusListener {
    JTextField target;
    String default_text;
    public JTextFieldCursorAtEnd(JTextField target, String default_text) { 
        this.target = target; 
        this.default_text = default_text;
    }
    
    @Override public void focusGained(FocusEvent e) {
        target.setCaretPosition(target.getText().trim().length());
    }
    
    @Override public void focusLost(FocusEvent e) { 
        int i = target.getText().trim().length();
        if(i==0) {
            target.setText(default_text);
        } else {
            target.setText(target.getText().toUpperCase());
        }
    }
}