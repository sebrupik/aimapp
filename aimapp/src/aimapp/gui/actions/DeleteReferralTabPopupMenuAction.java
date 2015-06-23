package aimapp.gui.actions;

import aimapp.AimApp;
import aimapp.gui.ReferralPanel;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

public class DeleteReferralTabPopupMenuAction extends AbstractAction {
    private final String _class;
    AimApp owner;
    JTabbedPane target;
    PreparedStatement deletePS;
    
    public DeleteReferralTabPopupMenuAction(AimApp owner, JTabbedPane target, PreparedStatement deletePS) {
        this.owner = owner;
        this.target = target;
        this.deletePS = deletePS;
        this._class = this.getClass().getName();
        
    }

    @Override public void actionPerformed(ActionEvent e) {
        int index = target.getSelectedIndex();
        System.out.println("tab index is "+index);
        
        if( (index != target.getTabCount()-1) & !(target.getTitleAt(index).equals("Add")) ) {
            ReferralPanel rp = (ReferralPanel)target.getComponentAt(index);
            System.out.println("we can delete this one --- "+target.getTitleAt(index));
            System.out.println(rp);
            
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
        } else {
            System.out.println("leave this tab alone");
        }
    }
    
    private void deleteReferral(int rpk) {
        try {
            deletePS.setInt(1, rpk);
            owner.getdbConnection().executeUpdate(deletePS);
            
            deletePS.clearParameters();
        } catch(java.sql.SQLException sqle) { owner.exceptionEncountered(_class+"/deleteReferral", sqle); 
        }
    }
    
}