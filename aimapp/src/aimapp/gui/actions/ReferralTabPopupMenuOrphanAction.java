package aimapp.gui.actions;

import aimapp.AimApp;

import java.sql.PreparedStatement;
import javax.swing.JTabbedPane;

public class ReferralTabPopupMenuOrphanAction extends ReferralTabPopupMenuAbstractAction {
    
    public ReferralTabPopupMenuOrphanAction(AimApp owner, JTabbedPane target, PreparedStatement pS) {
        super(owner, target, pS);
    }

    @Override void uniqueMethod(int rpk) {
        try {
            pS.setInt(1, rpk);
            owner.getdbConnection().executeUpdate(pS);
            
            pS.clearParameters();
        } catch(java.sql.SQLException sqle) { owner.exceptionEncountered(_class+"/uniqueReferral", sqle); 
        }
    }
    
}