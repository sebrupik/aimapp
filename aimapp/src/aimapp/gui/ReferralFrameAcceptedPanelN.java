package aimapp.gui;

import jdbcApp.components.JComboBoxHolder;
import aimapp.gui.actions.JComboBoxTextFieldAction;

//import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class ReferralFrameAcceptedPanelN extends ReferralFrameAcceptedPanelAb {
    JTextField otherTxt;
    
    public ReferralFrameAcceptedPanelN(JComboBoxHolder parentJCBH) {
        super(parentJCBH);
        
        //parentJCBH.addItem("NOT_ACCEPTED", "SELECT not_accepted FROM aim_list_not_accepted", new JComboBox());
        otherTxt = new JTextField(40);
        parentJCBH.getJCBox("NOT_ACCEPTED").addActionListener(new JComboBoxTextFieldAction(parentJCBH.getJCBox("NOT_ACCEPTED"), otherTxt, "Other (Specify)"));
        
        add(new JLabel("Reason for not being accetped"));  add(parentJCBH.getJCBox("NOT_ACCEPTED"));
        add(otherTxt);
        
    }
    
    @Override public boolean isModified() {
        if(parentJCBH.getJCBox("NOT_ACCEPTED").getSelectedIndex()!=0)
            return true;
        else 
            return false;
    }
    
    @Override public void clearFields() {
        parentJCBH.getJCBox("NOT_ACCEPTED").setSelectedIndex(0);
        otherTxt.setText("");
    }
    
    public void populateFields(String[] obj) {
        parentJCBH.setSelectedItem("NOT_ACCEPTED", obj[0]);
        otherTxt.setText(obj[1]);
    }
    
    public String[] getFields() {
        return new String[]{ (String)parentJCBH.getSelectedItem("NOT_ACCEPTED"), otherTxt.getText() };
    }
    
    @Override public void setEnabled(boolean tof) {
        super.setEnabled(tof);
        
    }
}
