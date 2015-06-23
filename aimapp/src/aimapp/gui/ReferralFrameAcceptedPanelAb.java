package aimapp.gui;

import jdbcApp.components.JComboBoxHolder;

import javax.swing.JPanel;

public abstract class ReferralFrameAcceptedPanelAb extends JPanel {
    JComboBoxHolder parentJCBH;
    
    public ReferralFrameAcceptedPanelAb(JComboBoxHolder parentJCBH) {
        this.parentJCBH = parentJCBH;
    }
    
    public abstract boolean isModified();
    public abstract void clearFields();
}
