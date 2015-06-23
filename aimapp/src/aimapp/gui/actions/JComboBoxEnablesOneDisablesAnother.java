package aimapp.gui.actions;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.AbstractAction;

public class JComboBoxEnablesOneDisablesAnother extends AbstractAction {
    JComboBox source;
    JComboBox target1, target2;
    String[] trigger;
    
    
    public JComboBoxEnablesOneDisablesAnother(JComboBox source, JComboBox target1, JComboBox target2, String[] trigger) {
        this.source = source;
        this.target1 = target1;
        this.target2 = target2;
        this.trigger = trigger;
    }
    
    @Override public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        if (containsTrigger((String)source.getSelectedItem())) {
            this.swapAround(target2, target1);
            /*if(target2.getItemCount()==0)
                target2.setSelectedIndex(-1);
            else
                target2.setSelectedIndex(0);
            
            target1.setEnabled(true);
            target2.setEnabled(false);*/
            System.out.println("you may now edit!!");
        } else {
            this.swapAround(target1, target2);
            /*if(target1.getItemCount()==0)
                target1.setSelectedIndex(-1);
            else
                target1.setSelectedIndex(0);
            
            target1.setEnabled(false);
            target2.setEnabled(true);*/
            System.out.println("no more editing for you!!!");
        }
    }
    
    protected void swapAround(JComboBox jcb1, JComboBox jcb2) {
        if(jcb1.getItemCount()==0)
                jcb1.setSelectedIndex(-1);
            else
                jcb1.setSelectedIndex(0);
            
            jcb1.setEnabled(false);
            jcb2.setEnabled(true);
    }
    
    protected boolean containsTrigger(String s) {
        for (int i=0;i<trigger.length; i++) {
            if(trigger[i].equals(s))
                return true;
        }
        
        return false;
    }
}
