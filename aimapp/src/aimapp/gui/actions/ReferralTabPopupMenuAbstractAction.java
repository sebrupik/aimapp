package aimapp.gui.actions;

import aimapp.AimApp;
import aimapp.gui.ReferralPanel;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

abstract class ReferralTabPopupMenuAbstractAction extends AbstractAction {
    final String _class;
    AimApp owner;
    JTabbedPane target;
    PreparedStatement pS;
    ReferralPanel rp;
    int index;
    
    public ReferralTabPopupMenuAbstractAction(AimApp owner, JTabbedPane target, PreparedStatement pS) {
        this.owner = owner;
        this.target = target;
        this.pS = pS;
        this._class = this.getClass().getName();
    }

    @Override public void actionPerformed(ActionEvent e) {
        index = target.getSelectedIndex();
        System.out.println("tab index is "+index);
        int rpk;
        
        if( (index != target.getTabCount()-1) & !(target.getTitleAt(index).equals("Add")) ) {
            rp = (ReferralPanel)target.getComponentAt(index);
            rpk = rp.getReferralPriKey();
            
            uniqueMethod(rpk);
            
        } else {
             System.out.println("leave this tab alone");
        }
    }
    
    abstract void uniqueMethod(int rpk);
}