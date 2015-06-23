package aimapp.gui.actions;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class JComboBoxTextFieldAction extends AbstractAction {
    JComboBox source;
    JTextField target;
    String trigger;
    
    public JComboBoxTextFieldAction(JComboBox source, JTextField target, String trigger) {
        this.source = source;
        this.target = target;
        this.trigger = trigger;
    }
    
    @Override public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        if(source.getSelectedIndex() != -1) {
            if ( ((String)source.getSelectedItem()).equals(trigger) ) {
                target.setEnabled(true);
                System.out.println("you may now edit!!");
            } else {
                target.setText("");
                target.setEnabled(false);
                System.out.println("no more editing for you!!!");
            }
        }
    }
}
