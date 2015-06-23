package aimapp.gui.actions;

import java.awt.event.FocusEvent;
import javax.swing.JTextField;

public class JTextFieldSelectTextListener implements java.awt.event.FocusListener {
    JTextField target;
    public JTextFieldSelectTextListener(JTextField target) { this.target = target; }
    @Override public void focusGained(FocusEvent e) { target.selectAll(); }
    @Override public void focusLost(FocusEvent e) { }
}
