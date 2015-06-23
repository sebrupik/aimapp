package aimapp.gui;

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

public class ReferralFrameAcceptedTab extends JTabbedPane {
    public ReferralFrameAcceptedTab() { }
    
    @Override public Component add(Component component) {
        return super.add(component);
    }
    
    @Override public void setSelectedIndex(int index) {
        int sel = this.getSelectedIndex();
        ReferralFrameAcceptedPanelAb curPane = (ReferralFrameAcceptedPanelAb)getSelectedComponent();
        if(curPane != null) {
            if(curPane.isModified()) {
                int n = JOptionPane.showConfirmDialog(this, "Moving away from this tab will delete it's contents. Continue?", "Delete contents", JOptionPane.YES_NO_OPTION);
                System.out.println("you chose : "+n);
                if(n == 0) { //yes
                    curPane.clearFields();
                } else { //no
                    return;
                }
            } 
        }
        super.setSelectedIndex(index);
    }
}
