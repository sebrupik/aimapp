package aimapp.gui.actions;

import aimapp.AimApp;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

public class ReferralTabPopupMenuDeleteAction extends ReferralTabPopupMenuAbstractAction {
    
    public ReferralTabPopupMenuDeleteAction(AimApp owner, JTabbedPane target, PreparedStatement pS) {
        super(owner, target, pS);
    }
    
    @Override void uniqueMethod(int rpk) {
        if( rp.getUpdateBool() == false ) {
            System.out.println("...this one has not been entered into the DB");
            int result = JOptionPane.showConfirmDialog(null, "This referral has not been added to the DB. Really delete?", "Confirm Delete", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if(result==0) { //yes
                target.removeTabAt(index);
            }
        } else {
            System.out.println("...this one has been entered into the DB, you sure you want to delete it??!");
            int result = JOptionPane.showConfirmDialog(null, "This referral has been entered into the DB. Really delete?", "Confirm Delete", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if(result==0) { //yes   
                //do some sophisticated SQL to remove the entry
                target.removeTabAt(index);
                deleteReferral(rp.getReferralPriKey());
            }   
        }
    }
    
    private void deleteReferral(int rpk) {
        try {
            pS.setInt(1, rpk);
            owner.getdbConnection().executeUpdate(pS);
            
            pS.clearParameters();
        } catch(java.sql.SQLException sqle) { owner.exceptionEncountered(_class+"/deleteReferral", sqle); 
        }
    }
    
}