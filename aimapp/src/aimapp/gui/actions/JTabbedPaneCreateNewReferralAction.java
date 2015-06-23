package aimapp.gui.actions;

import aimapp.AimApp;
import aimapp.gui.ReferralPanel;
import java.awt.event.MouseEvent;
import javax.swing.JTabbedPane;

 public class JTabbedPaneCreateNewReferralAction implements java.awt.event.MouseListener {
    private final String _class;
    AimApp owner;
    JTabbedPane target;
    int client_priKey;
    int count;
    
    public JTabbedPaneCreateNewReferralAction(AimApp owner, JTabbedPane target, int client_priKey) {
        this._class = this.getClass().getName();
        this.owner = owner;
        this.target = target;
        this.client_priKey = client_priKey;
        
        count = 1;
    }
    
    private void theMethod() {
        int numTabs = target.getTabCount();
        if( target.getTitleAt(target.getSelectedIndex()).equals("Add") ) {
            target.insertTab("New Referral"+count, null, new ReferralPanel(owner, client_priKey, -1), "", numTabs-1 );
            target.setSelectedIndex(numTabs-1);
            count++;
        }
    }

    @Override public void mouseClicked(MouseEvent e) { this.theMethod(); }
    @Override public void mousePressed(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }
}
