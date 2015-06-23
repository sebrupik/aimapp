package aimapp.gui.actions;

import aimapp.AimApp;
import java.sql.PreparedStatement;
import javax.swing.JTabbedPane;


/**
 * List all orphaned and not (temporarily) deleted referrals and assign them to the currently selected client
 * 
 * @author Seb
 */
public class ReferralTabPopupMenuAssignAction  extends ReferralTabPopupMenuAbstractAction {
    int cpk;
    
    public ReferralTabPopupMenuAssignAction(AimApp owner, JTabbedPane target, PreparedStatement pS) {
        super(owner, target, pS);
    }
    
    public ReferralTabPopupMenuAssignAction(AimApp owner, JTabbedPane target, PreparedStatement pS, int cpk) {
        this(owner, target, pS);
        this.cpk = cpk;
    }
    
    @Override void uniqueMethod(int pk) {
        
    }
    
}
