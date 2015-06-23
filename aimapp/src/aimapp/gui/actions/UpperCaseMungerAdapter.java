package aimapp.gui.actions;

import javax.swing.JTextField;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;

public class UpperCaseMungerAdapter extends FocusAdapter {
    JTextField source;
    boolean all;
    
    public UpperCaseMungerAdapter(JTextField source, boolean all) {
        this.source = source;
        this.all = all;
    }
    
    @Override public void focusGained(FocusEvent e) {
        workTheMagic();
    }

    @Override public void focusLost(FocusEvent e) {
        workTheMagic();
    }
    
    
    public void workTheMagic() {
        String s = source.getText();
        
        if(s.trim().length()!=0) {
            if(all) {
                s = s.toUpperCase();
            } else {  //just the first letter
                s = s.replaceFirst(s.substring(0,1), s.substring(0,1).toUpperCase());
            }

            source.setText(s);
        }
    }
}
