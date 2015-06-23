package aimapp.gui;

import aimapp.AimApp;
//import aimapp.gui.actions.JComboBoxEnablesOneDisablesAnother;
import jdbcApp.components.JComboBoxHolder;

import java.awt.BorderLayout;
//import java.awt.event.ActionListener;
//import java.text.ParseException;
import javax.swing.BorderFactory;
//import javax.swing.JComboBox;
//import javax.swing.JLabel;
import javax.swing.JPanel;

public class ReferralFrameAcceptedPanelY extends ReferralFrameAcceptedPanelAb {
    ContactTimePanel ctPanel;
    
    public ReferralFrameAcceptedPanelY(AimApp owner, JComboBoxHolder parentJCBH, int client_priKey, int referral_priKey) {
        super(parentJCBH);
        
        setLayout(new BorderLayout(2,2));
        JPanel jcbPanel = new JPanel();
        ctPanel = new ContactTimePanel(owner, client_priKey, referral_priKey);
        ctPanel.setBorder(BorderFactory.createTitledBorder("Client Contact"));
       
        
        add(jcbPanel, BorderLayout.NORTH);
        add(ctPanel, BorderLayout.CENTER);
    }
    
    @Override public boolean isModified() {
        if(parentJCBH.getJCBox("ACCEPTED_CATEGORY").getSelectedIndex()!=0 | parentJCBH.getJCBox("ACCEPTED_CATEGORY_IMCA").getSelectedIndex()!=0 | parentJCBH.getJCBox("ACCEPTED_ISSUES").getSelectedIndex()!=0)
            return true;
        else 
            return false;
    }
    
    @Override public void clearFields() { }
    public void populateFields(String[] obj, int rpk) { ctPanel.refreshTable(rpk); }
    public int insertDB(int rpk) throws java.text.ParseException { return ctPanel.insertDB(rpk); }
    
    public String[] getFields() {
        return new String[]{""};
        //return new String[]{ (String)parentJCBH.getSelectedItem("ACCEPTED_CATEGORY"), (String)parentJCBH.getSelectedItem("ACCEPTED_CATEGORY_IMCA"), (String)parentJCBH.getSelectedItem("ACCEPTED_ISSUES")};
    }
    
    @Override public void setEnabled(boolean tof) { ctPanel.setEnabled(tof); }
}